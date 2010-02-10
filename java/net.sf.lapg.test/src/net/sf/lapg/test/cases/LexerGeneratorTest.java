package net.sf.lapg.test.cases;

import junit.framework.TestCase;

import org.junit.Assert;

import net.sf.lapg.LexerTables;
import net.sf.lapg.api.Action;
import net.sf.lapg.api.Lexem;
import net.sf.lapg.api.Symbol;
import net.sf.lapg.lex.LexicalBuilder;
import net.sf.lapg.test.TestNotifier;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class LexerGeneratorTest extends TestCase {

	TestLexem[] INPUT1 = {
			new TestLexem(0, 0, "string", "[a-z][A-Z]?", "bC", "aZ", "zA", "q"),
			new TestLexem(1, 0, "number", "[0-9]+", "1", "12", "323", "2111111"),
			new TestLexem(2, 0, "hex", "0x[0-9a-zA-Z]+", "0x23bd", "0x1", "0x0"),
			new TestLexem(3, 0, "hex", "\\n|\\t|\\r", "\n", "\t", "\r"),
			new TestLexem(4, 0, "hex", "[\\xAAAA-\\xAABB]", "\uaab0", "\uaabb", "\uaaaa", "\uaaaf"),
	};

	public void testGenerator() {
		LexerTables lt = LexicalBuilder.compile(INPUT1, new TestNotifier(), 0);
		for(TestLexem tl : INPUT1) {
			for(String s : tl.getSamples()) {
				int res = nextToken(lt, s);
				Assert.assertEquals("For "+s+" Expected " + tl.getRegexp()+ ";",tl.index, res);
			}
		}
	}

	private int nextToken(LexerTables lr, String s) {
		int state = 0;
		int index = 0;

		while(state >= 0) {
			int chr = index < s.length() ? s.codePointAt(index++) : 0;
			state = lr.change[state][chr>=0 && chr < lr.char2no.length ? lr.char2no[chr]: 1];
		}
		if( state == -1 ) {
			return -1;
		}
		return -state-2;
	}

	private static class TestLexem implements Lexem {

		private final int index;
		private final int prio;
		private final String name;
		private final String regexp;
		private final String[] samples;

		public TestLexem(int index, int prio, String name, String regexp, String ... samples) {
			this.index = index;
			this.prio = prio;
			this.name = name;
			this.regexp = regexp;
			this.samples = samples;
		}

		public Action getAction() {
			return null;
		}

		public int getGroups() {
			return 1;
		}

		public int getPriority() {
			return prio;
		}

		public String getRegexp() {
			return regexp;
		}

		public String[] getSamples() {
			return samples;
		}

		public Symbol getSymbol() {
			return new Symbol() {
				public boolean isTerm() {
					throw new NotImplementedException();
				}

				public boolean isDefined() {
					throw new NotImplementedException();
				}

				public String getType() {
					throw new NotImplementedException();
				}

				public String getName() {
					return name;
				}

				public int getIndex() {
					return index;
				}

				public void addAnnotation(String name, Object value) {
					throw new UnsupportedOperationException();
				}

				public Object getAnnotation(String name) {
					return null;
				}
			};
		}
	}

}