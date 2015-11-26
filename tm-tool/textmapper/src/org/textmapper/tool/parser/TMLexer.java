/**
 * Copyright 2002-2015 Evgeny Gryaznov
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
package org.textmapper.tool.parser;

import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.textmapper.tool.parser.action.SActionLexer;
import org.textmapper.tool.parser.action.SActionParser;

public class TMLexer {

	public static class Span {
		public Object value;
		public int symbol;
		public int state;
		public int line;
		public int offset;
		public int endoffset;
	}

	public interface States {
		int initial = 0;
		int afterAt = 1;
		int afterAtID = 2;
	}

	public interface Tokens {
		int Unavailable_ = -1;
		int eoi = 0;
		int regexp = 1;
		int scon = 2;
		int icon = 3;
		int _skip = 4;
		int _skip_comment = 5;
		int Percent = 6;
		int ColonColonEqual = 7;
		int ColonColon = 8;
		int Or = 9;
		int OrOr = 10;
		int Equal = 11;
		int EqualEqual = 12;
		int ExclamationEqual = 13;
		int EqualGreater = 14;
		int Semicolon = 15;
		int Dot = 16;
		int Comma = 17;
		int Colon = 18;
		int Lsquare = 19;
		int Rsquare = 20;
		int Lparen = 21;
		int Rparen = 22;
		int LcurlyTilde = 23;
		int Rcurly = 24;
		int Less = 25;
		int Greater = 26;
		int Mult = 27;
		int Plus = 28;
		int PlusEqual = 29;
		int Questionmark = 30;
		int Exclamation = 31;
		int Tilde = 32;
		int Ampersand = 33;
		int AmpersandAmpersand = 34;
		int Dollar = 35;
		int Atsign = 36;
		int error = 37;
		int ID = 38;
		int Ltrue = 39;
		int Lfalse = 40;
		int Lnew = 41;
		int Lseparator = 42;
		int Las = 43;
		int Limport = 44;
		int Lset = 45;
		int Lbrackets = 46;
		int Linline = 47;
		int Lprio = 48;
		int Lshift = 49;
		int Lreturns = 50;
		int Linput = 51;
		int Lleft = 52;
		int Lright = 53;
		int Lnonassoc = 54;
		int Lgenerate = 55;
		int Lassert = 56;
		int Lempty = 57;
		int Lnonempty = 58;
		int Limplicit = 59;
		int Lparam = 60;
		int Lstring = 61;
		int Lbool = 62;
		int Lint = 63;
		int Lsymbol = 64;
		int Lnoeoi = 65;
		int Lsoft = 66;
		int Lclass = 67;
		int Linterface = 68;
		int Lvoid = 69;
		int Lspace = 70;
		int Llayout = 71;
		int Llanguage = 72;
		int Llalr = 73;
		int Llexer = 74;
		int Lparser = 75;
		int Lreduce = 76;
		int code = 77;
		int Lcurly = 78;
	}

	public interface ErrorReporter {
		void error(String message, int line, int offset, int endoffset);
	}

	public static final int TOKEN_SIZE = 2048;

	private Reader stream;
	final private ErrorReporter reporter;

	final private char[] data = new char[2048];
	private int datalen;
	private int tokenOffset;
	private int l;
	private int charOffset;
	private int chr;

	private int state;

	final private StringBuilder tokenBuffer = new StringBuilder(TOKEN_SIZE);

	private int tokenLine;
	private int currLine;
	private int currOffset;

	private int deep = 0;
	private int templatesStart = -1;
	private boolean skipComments = true;

	int getTemplatesStart() {
		return templatesStart;
	}

	public void setSkipComments(boolean skip) {
		this.skipComments = skip;
	}

	private boolean skipAction() throws IOException {
		final int[] ind = new int[] { 0 };
		SActionLexer.ErrorReporter innerreporter = new SActionLexer.ErrorReporter() {
			@Override
			public void error(String message, int line, int offset) {
				reporter.error(message, line, offset, offset + 1);
			}
		};
		SActionLexer l = new SActionLexer(innerreporter) {
			@Override
			protected int nextChar() throws IOException {
				if (ind[0] < 2) {
					return ind[0]++ == 0 ? '{' : chr;
				}
				TMLexer.this.advance();
				return chr;
			}
		};
		SActionParser p = new SActionParser(innerreporter);
		try {
			p.parse(l);
		} catch (SActionParser.ParseException e) {
			reporter.error("syntax error in action", getLine(), getOffset(), getOffset() + 1);
			return false;
		}
		return true;
	}

	private String unescape(String s, int start, int end) {
		StringBuilder sb = new StringBuilder();
		end = Math.min(end, s.length());
		for (int i = start; i < end; i++) {
			char c = s.charAt(i);
			if (c == '\\') {
				if (++i == end) {
					break;
				}
				c = s.charAt(i);
				if (c == 'u' || c == 'x') {
					// FIXME process unicode
				} else if (c == 'n') {
					sb.append('\n');
				} else if (c == 'r') {
					sb.append('\r');
				} else if (c == 't') {
					sb.append('\t');
				} else {
					sb.append(c);
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public TMLexer(Reader stream, ErrorReporter reporter) throws IOException {
		this.reporter = reporter;
		reset(stream);
	}

	public void reset(Reader stream) throws IOException {
		this.state = 0;
		tokenLine = currLine = 1;
		currOffset = 0;
		this.stream = stream;
		datalen = stream.read(data);
		l = 0;
		tokenOffset = -1;
		if (l + 1 >= datalen) {
			if (l < datalen) {
				data[0] = data[l];
				datalen = Math.max(stream.read(data, 1, data.length - 1) + 1, 1);
			} else {
				datalen = stream.read(data);
			}
			l = 0;
		}
		charOffset = l;
		chr = l < datalen ? data[l++] : -1;
		if (chr >= Character.MIN_HIGH_SURROGATE && chr <= Character.MAX_HIGH_SURROGATE && l < datalen &&
				Character.isLowSurrogate(data[l])) {
			chr = Character.toCodePoint((char) chr, data[l++]);
		}
	}

	protected void advance() throws IOException {
		if (chr == -1) return;
		currOffset += l - charOffset;
		if (chr == '\n') {
			currLine++;
		}
		if (l + 1 >= datalen) {
			if (tokenOffset >= 0) {
				tokenBuffer.append(data, tokenOffset, l - tokenOffset);
				tokenOffset = 0;
			}
			if (l < datalen) {
				data[0] = data[l];
				datalen = Math.max(stream.read(data, 1, data.length - 1) + 1, 1);
			} else {
				datalen = stream.read(data);
			}
			l = 0;
		}
		charOffset = l;
		chr = l < datalen ? data[l++] : -1;
		if (chr >= Character.MIN_HIGH_SURROGATE && chr <= Character.MAX_HIGH_SURROGATE && l < datalen &&
				Character.isLowSurrogate(data[l])) {
			chr = Character.toCodePoint((char) chr, data[l++]);
		}
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getTokenLine() {
		return tokenLine;
	}

	public int getLine() {
		return currLine;
	}

	public void setLine(int currLine) {
		this.currLine = currLine;
	}

	public int getOffset() {
		return currOffset;
	}

	public void setOffset(int currOffset) {
		this.currOffset = currOffset;
	}

	public String tokenText() {
		return tokenBuffer.toString();
	}

	public int tokenSize() {
		return tokenBuffer.length();
	}

	private static final short tmCharClass[] = {
		1, 1, 1, 1, 1, 1, 1, 1, 1, 35, 4, 1, 1, 9, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
		35, 14, 6, 10, 31, 8, 30, 2, 21, 22, 27, 28, 18, 7, 17, 5,
		34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 11, 16, 26, 12, 15, 29,
		32, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33,
		33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 19, 3, 20, 1, 33,
		1, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33,
		33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 33, 23, 13, 25, 24, 1
	};

	private static final short tmStateMap[] = {
		0, 0, 1
	};

	private static final short[] tmRuleSymbol = unpack_short(78,
		"\46\1\2\3\0\4\5\6\7\10\11\12\13\14\15\16\17\20\21\22\23\24\25\26\27\30\31\32\33\34" +
		"\35\36\37\40\41\42\43\44\47\50\51\52\53\54\55\56\57\60\61\62\63\64\65\66\67\70\71" +
		"\72\73\74\75\76\77\100\101\102\103\104\105\106\107\110\111\112\113\114\115\116");

	private static final int tmClassesCount = 36;

	private static final short[] tmGoto = unpack_vc_short(1908,
		"\1\ufffe\1\uffff\1\2\1\uffff\1\3\1\4\1\5\1\6\1\7\1\3\1\10\1\11\1\12\1\13\1\14\1\15" +
		"\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\33\1\34\1\35" +
		"\1\36\1\37\1\40\1\3\2\uffff\1\2\1\uffff\1\3\1\4\1\5\1\6\1\7\1\3\1\10\1\11\1\12\1" +
		"\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\1\41\1\26\1\27\1\30\1\31\1\32\1" +
		"\33\1\34\1\35\1\36\1\37\1\40\1\3\1\uffff\1\2\1\42\1\43\1\uffff\37\2\4\ufff8\1\3\4" +
		"\ufff8\1\3\31\ufff8\1\3\1\uffff\2\4\1\44\1\uffff\1\45\36\4\1\uffff\2\5\1\46\1\uffff" +
		"\1\5\1\47\35\5\42\uffff\1\40\1\uffff\10\ufff6\1\50\33\ufff6\1\ufff7\3\10\1\51\37" +
		"\10\13\uffea\1\52\30\uffea\14\ufff1\1\53\2\ufff1\1\54\24\ufff1\15\ufff3\1\55\26\ufff3" +
		"\14\uffdd\1\56\27\uffdd\44\uffe2\44\uffed\44\uffec\44\uffeb\44\uffe9\44\uffe8\44" +
		"\uffe7\44\uffe6\30\uffb1\1\57\13\uffb1\44\uffdc\44\uffe4\44\uffe3\44\uffe1\14\uffe0" +
		"\1\60\27\uffe0\44\uffde\36\uffdb\1\61\5\uffdb\44\uffd9\44\uffd8\7\ufffd\1\62\31\ufffd" +
		"\2\37\1\ufffd\42\ufffa\1\40\1\ufffa\30\uffb0\1\57\13\uffb0\44\ufffd\1\uffff\3\2\1" +
		"\uffff\37\2\1\uffff\3\4\1\uffff\37\4\44\ufffc\1\uffff\3\5\1\uffff\37\5\44\ufffb\1" +
		"\ufff9\3\50\1\63\37\50\44\ufff7\14\ufff4\1\64\27\ufff4\44\ufff0\44\uffee\44\ufff2" +
		"\44\uffef\44\uffe5\44\uffdf\44\uffda\7\uffff\1\62\31\uffff\2\37\1\uffff\44\ufff9" +
		"\44\ufff5");

	private static short[] unpack_vc_short(int size, String... st) {
		short[] res = new short[size];
		int t = 0;
		int count = 0;
		for (String s : st) {
			int slen = s.length();
			for (int i = 0; i < slen; ) {
				count = i > 0 || count == 0 ? s.charAt(i++) : count;
				if (i < slen) {
					short val = (short) s.charAt(i++);
					while (count-- > 0) res[t++] = val;
				}
			}
		}
		assert res.length == t;
		return res;
	}

	private static int mapCharacter(int chr) {
		if (chr >= 0 && chr < 128) return tmCharClass[chr];
		return chr == -1 ? 0 : 1;
	}

	public Span next() throws IOException {
		Span token = new Span();
		int state;

		tokenloop:
		do {
			token.offset = currOffset;
			tokenLine = token.line = currLine;
			if (tokenBuffer.length() > TOKEN_SIZE) {
				tokenBuffer.setLength(TOKEN_SIZE);
				tokenBuffer.trimToSize();
			}
			tokenBuffer.setLength(0);
			tokenOffset = charOffset;

			for (state = tmStateMap[this.state]; state >= 0; ) {
				state = tmGoto[state * tmClassesCount + mapCharacter(chr)];
				if (state == -1 && chr == -1) {
					token.endoffset = currOffset;
					token.symbol = 0;
					token.value = null;
					reporter.error("Unexpected end of input reached", token.line, token.offset, token.endoffset);
					token.offset = currOffset;
					break tokenloop;
				}
				if (state >= -1 && chr != -1) {
					currOffset += l - charOffset;
					if (chr == '\n') {
						currLine++;
					}
					if (l + 1 >= datalen) {
						tokenBuffer.append(data, tokenOffset, l - tokenOffset);
						tokenOffset = 0;
						if (l < datalen) {
							data[0] = data[l];
							datalen = Math.max(stream.read(data, 1, data.length - 1) + 1, 1);
						} else {
							datalen = stream.read(data);
						}
						l = 0;
					}
					charOffset = l;
					chr = l < datalen ? data[l++] : -1;
					if (chr >= Character.MIN_HIGH_SURROGATE && chr <= Character.MAX_HIGH_SURROGATE && l < datalen &&
							Character.isLowSurrogate(data[l])) {
						chr = Character.toCodePoint((char) chr, data[l++]);
					}
				}
			}
			token.endoffset = currOffset;

			if (state == -1) {
				if (charOffset > tokenOffset) {
					tokenBuffer.append(data, tokenOffset, charOffset - tokenOffset);
				}
				reporter.error(MessageFormat.format("invalid lexeme at line {0}: `{1}`, skipped", currLine, tokenText()), token.line, token.offset, token.endoffset);
				token.symbol = -1;
				continue;
			}

			if (state == -2) {
				token.symbol = Tokens.eoi;
				token.value = null;
				break tokenloop;
			}

			if (charOffset > tokenOffset) {
				tokenBuffer.append(data, tokenOffset, charOffset - tokenOffset);
			}

			token.symbol = tmRuleSymbol[-state - 3];
			token.value = null;

		} while (token.symbol == -1 || !createToken(token, -state - 3));
		tokenOffset = -1;
		return token;
	}

	protected boolean createToken(Span token, int ruleIndex) throws IOException {
		boolean spaceToken = false;
		switch (ruleIndex) {
			case 0:
				return createIDToken(token, ruleIndex);
			case 1: // regexp: /\/([^\/\\\n]|\\.)*\//
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				{ token.value = tokenText().substring(1, tokenSize()-1); }
				break;
			case 2: // scon: /"([^\n\\"]|\\.)*"/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				{ token.value = unescape(tokenText(), 1, tokenSize()-1); }
				break;
			case 3: // icon: /\-?[0-9]+/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				{ token.value = Integer.parseInt(tokenText()); }
				break;
			case 4: // eoi: /%%.*(\r?\n)?/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				{ templatesStart = token.endoffset; }
				break;
			case 5: // _skip: /[\n\r\t ]+/
				spaceToken = true;
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 6: // _skip_comment: /#.*(\r?\n)?/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				{ spaceToken = skipComments; }
				break;
			case 7: // '%': /%/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 8: // '::=': /::=/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 9: // '::': /::/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 10: // '|': /\|/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 11: // '||': /\|\|/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 12: // '=': /=/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 13: // '==': /==/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 14: // '!=': /!=/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 15: // '=>': /=>/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 16: // ';': /;/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 17: // '.': /\./
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 18: // ',': /,/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 19: // ':': /:/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 20: // '[': /\[/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 21: // ']': /\]/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 22: // '(': /\(/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 23: // ')': /\)/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 24: // '{~': /\{~/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 25: // '}': /\}/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 26: // '<': /</
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 27: // '>': />/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 28: // '*': /\*/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 29: // '+': /\+/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 30: // '+=': /\+=/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 31: // '?': /\?/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 32: // '!': /!/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 33: // '~': /~/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 34: // '&': /&/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 35: // '&&': /&&/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 36: // '$': /$/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 37: // '@': /@/
				state = States.afterAt;
				break;
			case 76: // code: /\{/
				switch(state) {
					case States.afterAt:
						state = States.initial;
						break;
				}
				{ skipAction(); token.endoffset = getOffset(); }
				break;
			case 77: // '{': /\{/
				state = States.initial;
				break;
		}
		return !(spaceToken);
	}

	private static Map<String,Integer> subTokensOfID = new HashMap<String,Integer>();
	static {
		subTokensOfID.put("true", 38);
		subTokensOfID.put("false", 39);
		subTokensOfID.put("new", 40);
		subTokensOfID.put("separator", 41);
		subTokensOfID.put("as", 42);
		subTokensOfID.put("import", 43);
		subTokensOfID.put("set", 44);
		subTokensOfID.put("brackets", 45);
		subTokensOfID.put("inline", 46);
		subTokensOfID.put("prio", 47);
		subTokensOfID.put("shift", 48);
		subTokensOfID.put("returns", 49);
		subTokensOfID.put("input", 50);
		subTokensOfID.put("left", 51);
		subTokensOfID.put("right", 52);
		subTokensOfID.put("nonassoc", 53);
		subTokensOfID.put("generate", 54);
		subTokensOfID.put("assert", 55);
		subTokensOfID.put("empty", 56);
		subTokensOfID.put("nonempty", 57);
		subTokensOfID.put("implicit", 58);
		subTokensOfID.put("param", 59);
		subTokensOfID.put("string", 60);
		subTokensOfID.put("bool", 61);
		subTokensOfID.put("int", 62);
		subTokensOfID.put("symbol", 63);
		subTokensOfID.put("no-eoi", 64);
		subTokensOfID.put("soft", 65);
		subTokensOfID.put("class", 66);
		subTokensOfID.put("interface", 67);
		subTokensOfID.put("void", 68);
		subTokensOfID.put("space", 69);
		subTokensOfID.put("layout", 70);
		subTokensOfID.put("language", 71);
		subTokensOfID.put("lalr", 72);
		subTokensOfID.put("lexer", 73);
		subTokensOfID.put("parser", 74);
		subTokensOfID.put("reduce", 75);
	}

	protected boolean createIDToken(Span token, int ruleIndex) {
		Integer replacement = subTokensOfID.get(tokenText());
		if (replacement != null) {
			ruleIndex = replacement;
			token.symbol = tmRuleSymbol[ruleIndex];
		}
		boolean spaceToken = false;
		switch(ruleIndex) {
			case 38:	// true
				switch(state) {
					case States.afterAt:
						state = States.afterAtID;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 39:	// false
				switch(state) {
					case States.afterAt:
						state = States.afterAtID;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 40:	// new
				switch(state) {
					case States.afterAt:
						state = States.afterAtID;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 41:	// separator
				switch(state) {
					case States.afterAt:
						state = States.afterAtID;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 42:	// as
				switch(state) {
					case States.afterAt:
						state = States.afterAtID;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 43:	// import
				switch(state) {
					case States.afterAt:
						state = States.afterAtID;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 44:	// set
				switch(state) {
					case States.afterAt:
						state = States.afterAtID;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 75:	// reduce
				switch(state) {
					case States.afterAt:
						state = States.afterAtID;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				break;
			case 45:	// brackets (soft)
			case 46:	// inline (soft)
			case 47:	// prio (soft)
			case 48:	// shift (soft)
			case 49:	// returns (soft)
			case 50:	// input (soft)
			case 51:	// left (soft)
			case 52:	// right (soft)
			case 53:	// nonassoc (soft)
			case 54:	// generate (soft)
			case 55:	// assert (soft)
			case 56:	// empty (soft)
			case 57:	// nonempty (soft)
			case 58:	// implicit (soft)
			case 59:	// param (soft)
			case 60:	// string (soft)
			case 61:	// bool (soft)
			case 62:	// int (soft)
			case 63:	// symbol (soft)
			case 64:	// no-eoi (soft)
			case 65:	// soft (soft)
			case 66:	// class (soft)
			case 67:	// interface (soft)
			case 68:	// void (soft)
			case 69:	// space (soft)
			case 70:	// layout (soft)
			case 71:	// language (soft)
			case 72:	// lalr (soft)
			case 73:	// lexer (soft)
			case 74:	// parser (soft)
			case 0:	// <default>
				switch(state) {
					case States.afterAt:
						state = States.afterAtID;
						break;
					case States.afterAtID:
						state = States.initial;
						break;
				}
				{ token.value = tokenText(); }
				break;
		}
		return !(spaceToken);
	}

	/* package */ static int[] unpack_int(int size, String... st) {
		int[] res = new int[size];
		boolean second = false;
		char first = 0;
		int t = 0;
		for (String s : st) {
			int slen = s.length();
			for (int i = 0; i < slen; i++) {
				if (second) {
					res[t++] = (s.charAt(i) << 16) + first;
				} else {
					first = s.charAt(i);
				}
				second = !second;
			}
		}
		assert !second;
		assert res.length == t;
		return res;
	}

	/* package */ static short[] unpack_short(int size, String... st) {
		short[] res = new short[size];
		int t = 0;
		for (String s : st) {
			int slen = s.length();
			for (int i = 0; i < slen; i++) {
				res[t++] = (short) s.charAt(i);
			}
		}
		assert res.length == t;
		return res;
	}
}
