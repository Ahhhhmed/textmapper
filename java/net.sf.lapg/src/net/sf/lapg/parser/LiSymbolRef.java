/**
 * Copyright 2002-2010 Evgeny Gryaznov
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
package net.sf.lapg.parser;

import java.util.Map;

import net.sf.lapg.api.Symbol;
import net.sf.lapg.api.SymbolRef;

public class LiSymbolRef extends LiAnnotated implements SymbolRef {

	Symbol target;
	String alias;

	public LiSymbolRef(Symbol target, String alias, Map<String,Object> annotations) {
		super(annotations);
		this.target = target;
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public Symbol getTarget() {
		return target;
	}

}
