/**
 * Copyright 2002-2012 Evgeny Gryaznov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.textmapper.lapg.parser;

import org.textmapper.lapg.api.Rule;
import org.textmapper.lapg.api.SourceElement;
import org.textmapper.lapg.api.Symbol;
import org.textmapper.lapg.api.SymbolRef;
import org.textmapper.lapg.api.builder.GrammarBuilder;
import org.textmapper.lapg.api.builder.RuleBuilder;

import java.util.*;

/**
 * evgeny, 8/6/12
 */
public class LapgRuleBuilder {

	private final RuleBuilder original;
	private final List<AbstractRulePart> parts;
	private final Map<SourceElement, Map<String, Object>> annotationsMap;
	private Symbol prio;

	public LapgRuleBuilder(GrammarBuilder builder, String alias, Symbol left, SourceElement origin, Map<SourceElement, Map<String, Object>> annotationsMap) {
		this.original = builder.rule(alias, left, origin);
		this.parts = new ArrayList<AbstractRulePart>();
		this.annotationsMap = annotationsMap;
	}

	public void add(AbstractRulePart part) {
		parts.add(part);
	}

	public void setPriority(Symbol sym) {
		prio = sym;
	}

	public Rule[] create() {
		List<RulePart[]> rules = new CompositeRulePart(false, parts.toArray(new AbstractRulePart[parts.size()])).expand();
		Rule[] result = new Rule[rules.size()];
		int index = 0;
		for (RulePart[] parts : rules) {
			RuleBuilder builder = original.copy();
			if (prio != null) {
				builder.setPriority(prio);
			}
			for (RulePart part : parts) {
				SymbolRef symbolRef = builder.addPart(part.alias, part.sym, part.unwanted, part.origin);
				annotationsMap.put(symbolRef, part.annotations);
			}
			result[index++] = builder.create();
		}
		return result;
	}

	interface AbstractRulePart {
		List<RulePart[]> expand();

		Symbol getRepresentative();
	}

	static class RulePart implements AbstractRulePart {
		private final String alias;
		private final Symbol sym;
		private final Collection<Symbol> unwanted;
		private final Map<String, Object> annotations;
		private final SourceElement origin;

		public RulePart(String alias, Symbol sym, Collection<Symbol> unwanted, Map<String, Object> annotations, SourceElement origin) {
			assert sym != null;
			this.alias = alias;
			this.sym = sym;
			this.unwanted = unwanted;
			this.origin = origin;
			this.annotations = annotations;
		}

		public RulePart(Symbol sym, SourceElement origin) {
			this(null, sym, null, null, origin);
		}

		@Override
		public List<RulePart[]> expand() {
			return Collections.singletonList(new RulePart[]{this});
		}

		@Override
		public Symbol getRepresentative() {
			return sym;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			RulePart rulePart = (RulePart) o;

			if (alias != null ? !alias.equals(rulePart.alias) : rulePart.alias != null) return false;
			if (annotations != null ? !annotations.equals(rulePart.annotations) : rulePart.annotations != null)
				return false;
			if (origin != null ? !origin.equals(rulePart.origin) : rulePart.origin != null) return false;
			if (!sym.equals(rulePart.sym)) return false;
			if (unwanted != null ? !unwanted.equals(rulePart.unwanted) : rulePart.unwanted != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = alias != null ? alias.hashCode() : 0;
			result = 31 * result + sym.hashCode();
			result = 31 * result + (unwanted != null ? unwanted.hashCode() : 0);
			result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
			result = 31 * result + (origin != null ? origin.hashCode() : 0);
			return result;
		}
	}

	static class CompositeRulePart implements AbstractRulePart {
		private final AbstractRulePart[] parts;
		private final boolean isOptional;

		public CompositeRulePart(boolean optional, AbstractRulePart... parts) {
			assert parts != null;
			isOptional = optional;
			this.parts = parts;
		}

		@Override
		public List<RulePart[]> expand() {
			boolean simplePartsOnly = true;
			for (AbstractRulePart part : parts) {
				if (!(part instanceof RulePart)) {
					simplePartsOnly = false;
					break;
				}
			}
			if (simplePartsOnly) {
				RulePart[] parts = new RulePart[this.parts.length];
				System.arraycopy(this.parts, 0, parts, 0, parts.length);
				if (isOptional && parts.length > 0) {
					return Arrays.asList(new RulePart[0],
							parts);
				} else {
					return Collections.singletonList(parts);
				}

			} else {
				List<RulePart[]> result = Collections.singletonList(new RulePart[0]);
				for (AbstractRulePart part : parts) {
					List<RulePart[]> val = part.expand();
					result = cartesianProduct(result, val);
				}
				if (isOptional) {
					for (RulePart[] p : result) {
						if (p.length == 0) {
							return result;
						}
					}
					result.add(new RulePart[0]);
				}
				return result;
			}
		}

		@Override
		public Symbol getRepresentative() {
			if (parts.length == 1) {
				return parts[0].getRepresentative();
			}
			return null;
		}

		public List<RulePart[]> cartesianProduct(List<RulePart[]> left, List<RulePart[]> right) {
			List<RulePart[]> result = new ArrayList<RulePart[]>(left.size() * right.size());
			for (RulePart[] leftElement : left) {
				for (RulePart[] rightElement : right) {
					RulePart[] elem = new RulePart[leftElement.length + rightElement.length];
					System.arraycopy(leftElement, 0, elem, 0, leftElement.length);
					System.arraycopy(rightElement, 0, elem, leftElement.length, rightElement.length);
					result.add(elem);
				}
			}
			return result;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			CompositeRulePart that = (CompositeRulePart) o;

			if (isOptional != that.isOptional) return false;
			if (!Arrays.equals(parts, that.parts)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = Arrays.hashCode(parts);
			result = 31 * result + (isOptional ? 1 : 0);
			return result;
		}
	}

	static class UnorderedRulePart implements AbstractRulePart {
		private final AbstractRulePart[] parts;

		public UnorderedRulePart(AbstractRulePart... parts) {
			assert parts != null;
			this.parts = parts;
		}

		@Override
		public List<RulePart[]> expand() {
			List<RulePart[]> result = new ArrayList<RulePart[]>();
			for (AbstractRulePart part : parts) {
				result.addAll(part.expand());
			}
			return result;
		}

		@Override
		public Symbol getRepresentative() {
			return null;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			UnorderedRulePart that = (UnorderedRulePart) o;

			if (!Arrays.equals(parts, that.parts)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(parts);
		}
	}
}
