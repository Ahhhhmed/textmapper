/**
 * Copyright 2002-2013 Evgeny Gryaznov
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
import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;
import org.textmapper.tool.parser.TMLexer.ErrorReporter;
import org.textmapper.tool.parser.TMLexer.Lexems;
import org.textmapper.tool.parser.TMTree.TextSource;
import org.textmapper.tool.parser.ast.*;
import org.textmapper.tool.parser.TMLexer.LapgSymbol;

public class TMParser {

	public static class ParseException extends Exception {
		private static final long serialVersionUID = 1L;

		public ParseException() {
		}
	}

	private final ErrorReporter reporter;

	public TMParser(ErrorReporter reporter) {
		this.reporter = reporter;
	}


	private static final boolean DEBUG_SYNTAX = false;
	TextSource source;
	private static final int[] tmAction = TMLexer.unpack_int(276,
		"\uffff\uffff\uffff\uffff\uffff\uffff\237\0\240\0\ufffd\uffff\254\0\16\0\241\0\242" +
		"\0\uffff\uffff\225\0\224\0\236\0\uffff\uffff\232\0\uffc3\uffff\uffff\uffff\251\0" +
		"\uffff\uffff\uffbd\uffff\uffff\uffff\uffff\uffff\235\0\uffb7\uffff\uffff\uffff\uffff" +
		"\uffff\231\0\uffff\uffff\uff8b\uffff\uffff\uffff\252\0\uff85\uffff\246\0\247\0\245" +
		"\0\uffff\uffff\uffff\uffff\230\0\uffff\uffff\0\0\uffff\uffff\243\0\uffff\uffff\uffff" +
		"\uffff\uff7f\uffff\uffff\uffff\uffff\uffff\uff4f\uffff\244\0\10\0\uffff\uffff\2\0" +
		"\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\uffff\4\0\14\0\12\0\uffff" +
		"\uffff\uffff\uffff\uffff\uffff\11\0\uffff\uffff\uff1f\uffff\uff17\uffff\uff11\uffff" +
		"\41\0\45\0\46\0\44\0\13\0\15\0\ufedf\uffff\73\0\uffff\uffff\uffff\uffff\uffff\uffff" +
		"\50\0\uffff\uffff\uffff\uffff\42\0\43\0\uffff\uffff\uffff\uffff\74\0\40\0\47\0\uffff" +
		"\uffff\31\0\32\0\25\0\26\0\uffff\uffff\23\0\24\0\30\0\33\0\35\0\34\0\27\0\uffff\uffff" +
		"\22\0\ufed7\uffff\uffff\uffff\75\0\76\0\72\0\17\0\37\0\uffff\uffff\20\0\21\0\ufea3" +
		"\uffff\uffff\uffff\uffff\uffff\uffff\uffff\ufe69\uffff\100\0\103\0\104\0\uffff\uffff" +
		"\207\0\ufe37\uffff\36\0\uffff\uffff\52\0\ufe09\uffff\uffff\uffff\121\0\122\0\123" +
		"\0\uffff\uffff\ufdd1\uffff\220\0\ufd9d\uffff\uffff\uffff\uffff\uffff\ufd61\uffff" +
		"\ufd35\uffff\117\0\120\0\uffff\uffff\101\0\102\0\uffff\uffff\206\0\64\0\54\0\ufd09" +
		"\uffff\ufcd3\uffff\126\0\uffff\uffff\132\0\uffff\uffff\uffff\uffff\uffff\uffff\uffff" +
		"\uffff\ufccb\uffff\uffff\uffff\ufc8f\uffff\253\0\uffff\uffff\200\0\uffff\uffff\137" +
		"\0\ufc41\uffff\141\0\ufc07\uffff\ufbcb\uffff\160\0\163\0\165\0\ufb8b\uffff\161\0" +
		"\ufb49\uffff\ufb05\uffff\uffff\uffff\ufabd\uffff\162\0\147\0\ufa8f\uffff\146\0\ufa87" +
		"\uffff\ufa59\uffff\111\0\112\0\115\0\116\0\ufa2d\uffff\uf9f1\uffff\uffff\uffff\uffff" +
		"\uffff\56\0\uf9b5\uffff\130\0\127\0\uffff\uffff\124\0\133\0\215\0\uffff\uffff\uffff" +
		"\uffff\154\0\uffff\uffff\uffff\uffff\uffff\uffff\uf981\uffff\222\0\uffff\uffff\uffff" +
		"\uffff\uffff\uffff\uffff\uffff\110\0\uf945\uffff\144\0\uf909\uffff\157\0\145\0\uffff" +
		"\uffff\171\0\uffff\uffff\204\0\205\0\164\0\uf8c9\uffff\uf89b\uffff\114\0\uffff\uffff" +
		"\uffff\uffff\uf85d\uffff\66\0\67\0\70\0\71\0\uffff\uffff\60\0\62\0\125\0\uffff\uffff" +
		"\216\0\153\0\152\0\150\0\uffff\uffff\201\0\uffff\uffff\uffff\uffff\223\0\uffff\uffff" +
		"\166\0\uf821\uffff\167\0\143\0\uf7d9\uffff\173\0\174\0\136\0\107\0\106\0\uffff\uffff" +
		"\65\0\214\0\151\0\uffff\uffff\221\0\105\0\uffff\uffff\203\0\202\0\uffff\uffff\uffff" +
		"\uffff\ufffe\uffff\ufffe\uffff");

	private static final short[] tmLalr = TMLexer.unpack_short(2148,
		"\2\uffff\3\uffff\20\uffff\36\uffff\37\uffff\70\uffff\67\uffff\66\uffff\65\uffff\64" +
		"\uffff\63\uffff\62\uffff\61\uffff\60\uffff\57\uffff\56\uffff\55\uffff\54\uffff\53" +
		"\uffff\52\uffff\51\uffff\50\uffff\47\uffff\46\uffff\40\uffff\41\uffff\42\uffff\21" +
		"\234\uffff\ufffe\16\uffff\21\233\uffff\ufffe\15\uffff\22\250\uffff\ufffe\37\uffff" +
		"\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff\63\uffff\62\uffff\61\uffff\60\uffff" +
		"\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff\52\uffff\51\uffff\50\uffff\47\uffff" +
		"\46\uffff\24\227\uffff\ufffe\16\uffff\24\226\uffff\ufffe\66\uffff\14\1\uffff\ufffe" +
		"\10\3\36\3\37\3\45\3\46\3\47\3\50\3\51\3\52\3\53\3\54\3\55\3\56\3\57\3\60\3\61\3" +
		"\62\3\63\3\64\3\65\3\66\3\67\3\70\3\uffff\ufffe\45\uffff\10\5\36\5\37\5\46\5\47\5" +
		"\50\5\51\5\52\5\53\5\54\5\55\5\56\5\57\5\60\5\61\5\62\5\63\5\64\5\65\5\66\5\67\5" +
		"\70\5\uffff\ufffe\12\uffff\17\15\22\15\uffff\ufffe\22\uffff\17\51\uffff\ufffe\10" +
		"\uffff\20\uffff\36\uffff\37\uffff\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff\63" +
		"\uffff\62\uffff\61\uffff\60\uffff\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff\52" +
		"\uffff\51\uffff\50\uffff\47\uffff\46\uffff\0\7\uffff\ufffe\13\uffff\16\77\21\77\uffff" +
		"\ufffe\1\uffff\0\63\10\63\20\63\36\63\37\63\46\63\47\63\50\63\51\63\52\63\53\63\54" +
		"\63\55\63\56\63\57\63\60\63\61\63\62\63\63\63\64\63\65\63\66\63\67\63\70\63\uffff" +
		"\ufffe\13\uffff\0\53\3\53\10\53\20\53\22\53\36\53\37\53\46\53\47\53\50\53\51\53\52" +
		"\53\53\53\54\53\55\53\56\53\57\53\60\53\61\53\62\53\63\53\64\53\65\53\66\53\67\53" +
		"\70\53\72\53\uffff\ufffe\6\uffff\35\uffff\36\uffff\37\uffff\70\uffff\67\uffff\66" +
		"\uffff\65\uffff\64\uffff\63\uffff\62\uffff\61\uffff\60\uffff\57\uffff\56\uffff\55" +
		"\uffff\54\uffff\53\uffff\52\uffff\51\uffff\50\uffff\47\uffff\46\uffff\0\6\uffff\ufffe" +
		"\35\uffff\21\213\37\213\46\213\47\213\50\213\51\213\52\213\53\213\54\213\55\213\56" +
		"\213\57\213\60\213\61\213\62\213\63\213\64\213\65\213\66\213\67\213\70\213\uffff" +
		"\ufffe\3\uffff\0\55\10\55\20\55\22\55\36\55\37\55\46\55\47\55\50\55\51\55\52\55\53" +
		"\55\54\55\55\55\56\55\57\55\60\55\61\55\62\55\63\55\64\55\65\55\66\55\67\55\70\55" +
		"\72\55\uffff\ufffe\15\uffff\73\uffff\21\217\22\217\35\217\37\217\46\217\47\217\50" +
		"\217\51\217\52\217\53\217\54\217\55\217\56\217\57\217\60\217\61\217\62\217\63\217" +
		"\64\217\65\217\66\217\67\217\70\217\uffff\ufffe\6\uffff\20\uffff\22\uffff\23\uffff" +
		"\35\uffff\36\uffff\37\uffff\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff\63\uffff" +
		"\62\uffff\61\uffff\60\uffff\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff\52\uffff" +
		"\51\uffff\50\uffff\47\uffff\46\uffff\72\uffff\11\142\14\142\uffff\ufffe\37\uffff" +
		"\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff\63\uffff\62\uffff\61\uffff\60\uffff" +
		"\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff\52\uffff\51\uffff\50\uffff\47\uffff" +
		"\46\uffff\7\113\uffff\ufffe\37\uffff\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff" +
		"\63\uffff\62\uffff\61\uffff\60\uffff\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff" +
		"\52\uffff\51\uffff\50\uffff\47\uffff\46\uffff\7\113\uffff\ufffe\22\uffff\0\57\10" +
		"\57\20\57\36\57\37\57\46\57\47\57\50\57\51\57\52\57\53\57\54\57\55\57\56\57\57\57" +
		"\60\57\61\57\62\57\63\57\64\57\65\57\66\57\67\57\70\57\72\57\uffff\ufffe\56\uffff" +
		"\14\131\16\131\uffff\ufffe\6\uffff\20\uffff\22\uffff\23\uffff\35\uffff\36\uffff\37" +
		"\uffff\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff\63\uffff\62\uffff\61\uffff\60" +
		"\uffff\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff\52\uffff\51\uffff\50\uffff\47" +
		"\uffff\46\uffff\72\uffff\11\142\24\142\uffff\ufffe\12\15\17\15\32\15\6\16\11\16\14" +
		"\16\22\16\23\16\24\16\30\16\31\16\33\16\34\16\35\16\36\16\37\16\43\16\44\16\46\16" +
		"\47\16\50\16\51\16\52\16\53\16\54\16\55\16\56\16\57\16\60\16\61\16\62\16\63\16\64" +
		"\16\65\16\66\16\67\16\70\16\72\16\uffff\ufffe\6\uffff\22\uffff\23\uffff\35\uffff" +
		"\37\uffff\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff\63\uffff\62\uffff\61\uffff" +
		"\60\uffff\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff\52\uffff\51\uffff\50\uffff" +
		"\47\uffff\46\uffff\72\uffff\11\142\14\142\24\142\uffff\ufffe\6\uffff\22\uffff\23" +
		"\uffff\35\uffff\36\uffff\37\uffff\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff\63" +
		"\uffff\62\uffff\61\uffff\60\uffff\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff\52" +
		"\uffff\51\uffff\50\uffff\47\uffff\46\uffff\72\uffff\11\142\14\142\24\142\uffff\ufffe" +
		"\34\uffff\6\155\11\155\14\155\22\155\23\155\24\155\35\155\36\155\37\155\43\155\46" +
		"\155\47\155\50\155\51\155\52\155\53\155\54\155\55\155\56\155\57\155\60\155\61\155" +
		"\62\155\63\155\64\155\65\155\66\155\67\155\70\155\72\155\uffff\ufffe\33\uffff\6\170" +
		"\11\170\14\170\22\170\23\170\24\170\34\170\35\170\36\170\37\170\43\170\46\170\47" +
		"\170\50\170\51\170\52\170\53\170\54\170\55\170\56\170\57\170\60\170\61\170\62\170" +
		"\63\170\64\170\65\170\66\170\67\170\70\170\72\170\uffff\ufffe\44\uffff\6\172\11\172" +
		"\14\172\22\172\23\172\24\172\33\172\34\172\35\172\36\172\37\172\43\172\46\172\47" +
		"\172\50\172\51\172\52\172\53\172\54\172\55\172\56\172\57\172\60\172\61\172\62\172" +
		"\63\172\64\172\65\172\66\172\67\172\70\172\72\172\uffff\ufffe\30\uffff\31\uffff\6" +
		"\176\11\176\14\176\22\176\23\176\24\176\33\176\34\176\35\176\36\176\37\176\43\176" +
		"\44\176\46\176\47\176\50\176\51\176\52\176\53\176\54\176\55\176\56\176\57\176\60" +
		"\176\61\176\62\176\63\176\64\176\65\176\66\176\67\176\70\176\72\176\uffff\ufffe\35" +
		"\uffff\22\212\37\212\46\212\47\212\50\212\51\212\52\212\53\212\54\212\55\212\56\212" +
		"\57\212\60\212\61\212\62\212\63\212\64\212\65\212\66\212\67\212\70\212\uffff\ufffe" +
		"\11\uffff\14\140\24\140\uffff\ufffe\35\uffff\22\210\37\210\46\210\47\210\50\210\51" +
		"\210\52\210\53\210\54\210\55\210\56\210\57\210\60\210\61\210\62\210\63\210\64\210" +
		"\65\210\66\210\67\210\70\210\uffff\ufffe\37\uffff\70\uffff\67\uffff\66\uffff\65\uffff" +
		"\64\uffff\63\uffff\62\uffff\61\uffff\60\uffff\57\uffff\56\uffff\55\uffff\54\uffff" +
		"\53\uffff\52\uffff\51\uffff\50\uffff\47\uffff\46\uffff\7\113\uffff\ufffe\6\uffff" +
		"\20\uffff\22\uffff\23\uffff\35\uffff\36\uffff\37\uffff\70\uffff\67\uffff\66\uffff" +
		"\65\uffff\64\uffff\63\uffff\62\uffff\61\uffff\60\uffff\57\uffff\56\uffff\55\uffff" +
		"\54\uffff\53\uffff\52\uffff\51\uffff\50\uffff\47\uffff\46\uffff\72\uffff\11\142\14" +
		"\142\uffff\ufffe\6\uffff\20\uffff\22\uffff\23\uffff\35\uffff\36\uffff\37\uffff\70" +
		"\uffff\67\uffff\66\uffff\65\uffff\64\uffff\63\uffff\62\uffff\61\uffff\60\uffff\57" +
		"\uffff\56\uffff\55\uffff\54\uffff\53\uffff\52\uffff\51\uffff\50\uffff\47\uffff\46" +
		"\uffff\72\uffff\11\142\14\142\uffff\ufffe\72\uffff\0\61\10\61\20\61\36\61\37\61\46" +
		"\61\47\61\50\61\51\61\52\61\53\61\54\61\55\61\56\61\57\61\60\61\61\61\62\61\63\61" +
		"\64\61\65\61\66\61\67\61\70\61\uffff\ufffe\6\uffff\22\uffff\23\uffff\35\uffff\36" +
		"\uffff\37\uffff\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff\63\uffff\62\uffff\61" +
		"\uffff\60\uffff\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff\52\uffff\51\uffff\50" +
		"\uffff\47\uffff\46\uffff\43\uffff\72\uffff\11\142\24\142\uffff\ufffe\6\uffff\22\uffff" +
		"\23\uffff\35\uffff\36\uffff\37\uffff\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff" +
		"\63\uffff\62\uffff\61\uffff\60\uffff\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff" +
		"\52\uffff\51\uffff\50\uffff\47\uffff\46\uffff\72\uffff\11\142\14\142\24\142\uffff" +
		"\ufffe\34\uffff\6\156\11\156\14\156\22\156\23\156\24\156\35\156\36\156\37\156\43" +
		"\156\46\156\47\156\50\156\51\156\52\156\53\156\54\156\55\156\56\156\57\156\60\156" +
		"\61\156\62\156\63\156\64\156\65\156\66\156\67\156\70\156\72\156\uffff\ufffe\35\uffff" +
		"\22\211\37\211\46\211\47\211\50\211\51\211\52\211\53\211\54\211\55\211\56\211\57" +
		"\211\60\211\61\211\62\211\63\211\64\211\65\211\66\211\67\211\70\211\uffff\ufffe\6" +
		"\uffff\20\uffff\22\uffff\23\uffff\35\uffff\36\uffff\37\uffff\70\uffff\67\uffff\66" +
		"\uffff\65\uffff\64\uffff\63\uffff\62\uffff\61\uffff\60\uffff\57\uffff\56\uffff\55" +
		"\uffff\54\uffff\53\uffff\52\uffff\51\uffff\50\uffff\47\uffff\46\uffff\72\uffff\11" +
		"\142\14\142\24\142\uffff\ufffe\6\uffff\20\uffff\22\uffff\23\uffff\35\uffff\36\uffff" +
		"\37\uffff\70\uffff\67\uffff\66\uffff\65\uffff\64\uffff\63\uffff\62\uffff\61\uffff" +
		"\60\uffff\57\uffff\56\uffff\55\uffff\54\uffff\53\uffff\52\uffff\51\uffff\50\uffff" +
		"\47\uffff\46\uffff\72\uffff\11\142\14\142\uffff\ufffe\30\uffff\31\uffff\6\177\11" +
		"\177\14\177\22\177\23\177\24\177\33\177\34\177\35\177\36\177\37\177\43\177\44\177" +
		"\46\177\47\177\50\177\51\177\52\177\53\177\54\177\55\177\56\177\57\177\60\177\61" +
		"\177\62\177\63\177\64\177\65\177\66\177\67\177\70\177\72\177\uffff\ufffe\34\175\6" +
		"\175\11\175\14\175\22\175\23\175\24\175\35\175\36\175\37\175\43\175\46\175\47\175" +
		"\50\175\51\175\52\175\53\175\54\175\55\175\56\175\57\175\60\175\61\175\62\175\63" +
		"\175\64\175\65\175\66\175\67\175\70\175\72\175\uffff\ufffe");

	private static final short[] lapg_sym_goto = TMLexer.unpack_short(134,
		"\0\2\4\20\33\33\33\47\53\55\57\64\70\101\107\120\125\151\162\214\227\242\243\247" +
		"\253\262\265\266\273\302\330\355\u012a\u0133\u013c\u0144\u0145\u0146\u0147\u0186" +
		"\u01c4\u0202\u0241\u027f\u02bd\u02fb\u0339\u0377\u03b5\u03f6\u0435\u0474\u04b2\u04f0" +
		"\u052e\u056c\u05aa\u05e8\u05e8\u05f4\u05f5\u05f6\u05f7\u05f8\u05f9\u0613\u0635\u0638" +
		"\u063a\u063e\u0640\u0641\u0643\u0645\u0647\u0648\u0649\u064a\u064c\u064e\u0650\u0651" +
		"\u0653\u0655\u0657\u0658\u065a\u065c\u065e\u065e\u0663\u0669\u066f\u0679\u0680\u068b" +
		"\u0696\u06a2\u06b0\u06be\u06c9\u06d7\u06e6\u06f1\u06f4\u0706\u0711\u0719\u0722\u0723" +
		"\u0725\u0726\u0728\u0734\u0749\u074a\u074b\u074c\u074d\u074e\u074f\u0750\u0751\u0752" +
		"\u0755\u0756\u075b\u0765\u0774\u0775\u0776\u0777\u0778\u0779");

	private static final short[] lapg_sym_from = TMLexer.unpack_short(1913,
		"\u0110\u0111\116\151\1\5\26\44\56\63\67\77\117\235\337\361\1\5\26\44\54\56\77\201" +
		"\235\337\361\152\167\211\240\250\252\277\300\321\330\344\350\166\220\223\301\65\104" +
		"\267\323\34\53\71\102\244\34\53\113\163\51\66\75\232\234\246\346\347\u0106\24\117" +
		"\137\147\160\207\20\35\115\117\137\147\160\232\313\34\53\121\244\373\1\5\26\44\56" +
		"\76\77\104\117\137\147\160\211\235\240\277\300\344\350\361\21\115\117\137\147\160" +
		"\316\317\366\16\23\47\103\117\137\147\160\166\211\223\227\240\250\252\263\277\300" +
		"\321\324\325\326\330\335\344\350\211\240\250\252\277\300\321\330\335\344\350\32\36" +
		"\57\132\137\147\160\320\323\355\u010a\313\117\137\147\160\117\137\147\160\117\137" +
		"\147\160\262\375\u010d\262\375\u010d\244\117\137\147\160\257\117\137\147\160\253" +
		"\332\u0100\117\137\147\152\160\167\175\211\237\240\250\252\264\271\277\300\321\330" +
		"\335\343\344\350\1\5\26\44\56\65\77\104\165\167\211\235\240\252\277\300\321\330\344" +
		"\350\361\1\2\5\12\25\26\30\31\44\45\56\63\65\76\77\101\104\117\125\126\137\147\152" +
		"\160\165\167\173\177\202\206\211\213\214\215\234\235\237\240\241\250\252\263\272" +
		"\277\300\307\314\317\321\324\325\326\330\335\337\344\350\361\370\371\u010a\1\5\26" +
		"\44\56\77\235\337\361\1\5\26\44\56\77\235\337\361\1\5\26\44\56\77\235\361\321\261" +
		"\60\1\2\5\12\25\26\30\31\44\45\56\63\65\76\77\101\104\117\125\126\137\147\152\160" +
		"\165\166\167\173\177\202\206\211\213\214\215\223\234\235\237\240\241\250\252\263" +
		"\272\277\300\307\314\317\321\324\325\326\330\335\337\344\350\361\370\371\u010a\1" +
		"\2\5\12\25\26\30\31\44\45\56\63\65\76\77\101\104\117\125\126\137\147\152\160\165" +
		"\167\173\177\202\206\211\213\214\215\234\235\236\237\240\241\250\252\263\272\277" +
		"\300\307\314\317\321\324\325\326\330\335\337\344\350\361\370\371\u010a\1\2\5\12\25" +
		"\26\30\31\44\45\56\63\65\76\77\101\104\117\125\126\137\147\152\160\165\167\173\177" +
		"\202\206\211\213\214\215\234\235\236\237\240\241\250\252\263\272\277\300\307\314" +
		"\317\321\324\325\326\330\335\337\344\350\361\370\371\u010a\1\2\5\12\25\26\30\31\44" +
		"\45\56\63\65\76\77\101\104\117\125\126\137\147\152\160\165\166\167\173\177\202\206" +
		"\211\213\214\215\223\234\235\237\240\241\250\252\263\272\277\300\307\314\317\321" +
		"\324\325\326\330\335\337\344\350\361\370\371\u010a\1\2\5\12\25\26\30\31\44\45\56" +
		"\63\65\76\77\101\104\117\125\126\137\147\152\160\164\165\167\173\177\202\206\211" +
		"\213\214\215\234\235\237\240\241\250\252\263\272\277\300\307\314\317\321\324\325" +
		"\326\330\335\337\344\350\361\370\371\u010a\1\2\5\12\25\26\30\31\44\45\56\63\65\76" +
		"\77\101\104\117\125\126\137\147\152\160\164\165\167\173\177\202\206\211\213\214\215" +
		"\234\235\237\240\241\250\252\263\272\277\300\307\314\317\321\324\325\326\330\335" +
		"\337\344\350\361\370\371\u010a\1\2\5\12\25\26\30\31\44\45\56\63\65\76\77\101\104" +
		"\117\125\126\137\147\152\160\164\165\167\173\177\202\206\211\213\214\215\234\235" +
		"\237\240\241\250\252\263\272\277\300\307\314\317\321\324\325\326\330\335\337\344" +
		"\350\361\370\371\u010a\1\2\5\12\25\26\30\31\44\45\56\63\65\76\77\101\104\117\125" +
		"\126\137\147\152\160\164\165\167\173\177\202\206\211\213\214\215\234\235\237\240" +
		"\241\250\252\263\272\277\300\307\314\317\321\324\325\326\330\335\337\344\350\361" +
		"\370\371\u010a\1\2\5\12\25\26\30\31\44\45\56\63\65\76\77\101\104\117\125\126\137" +
		"\147\152\160\165\167\173\177\202\206\211\213\214\215\230\234\235\237\240\241\250" +
		"\252\263\272\277\300\307\314\317\321\324\325\326\330\335\337\344\350\361\370\371" +
		"\u010a\1\2\5\12\25\26\30\31\44\45\56\63\65\76\77\101\104\117\125\126\137\147\152" +
		"\160\165\167\173\177\202\206\211\213\214\215\234\235\237\240\241\250\252\263\272" +
		"\277\300\302\307\314\317\321\324\325\326\330\335\337\344\350\361\370\371\u010a\1" +
		"\2\5\12\25\26\30\31\44\45\56\63\65\76\77\101\104\117\125\126\137\147\152\160\165" +
		"\166\167\173\177\202\206\211\212\213\214\215\223\234\235\237\240\241\250\252\263" +
		"\272\277\300\302\307\314\317\321\324\325\326\330\335\337\344\350\361\370\371\u010a" +
		"\1\2\5\12\25\26\30\31\44\45\56\63\65\76\77\101\104\117\125\126\137\147\152\160\165" +
		"\166\167\173\177\202\206\211\213\214\215\223\234\235\237\240\241\250\252\263\272" +
		"\277\300\307\314\317\321\324\325\326\330\335\337\344\350\361\370\371\u010a\1\2\5" +
		"\12\25\26\30\31\44\45\56\63\65\76\77\101\104\117\125\126\137\147\152\160\165\166" +
		"\167\173\177\202\206\211\213\214\215\223\234\235\237\240\241\250\252\263\272\277" +
		"\300\307\314\317\321\324\325\326\330\335\337\344\350\361\370\371\u010a\1\2\5\12\25" +
		"\26\30\31\44\45\56\63\65\76\77\101\104\117\125\126\137\147\152\160\165\167\173\177" +
		"\202\206\211\213\214\215\234\235\237\240\241\250\252\263\272\277\300\302\307\314" +
		"\317\321\324\325\326\330\335\337\344\350\361\370\371\u010a\1\2\5\12\25\26\30\31\44" +
		"\45\56\63\65\76\77\101\104\117\125\126\137\147\152\160\165\167\173\177\202\206\211" +
		"\213\214\215\234\235\237\240\241\250\252\263\272\277\300\302\307\314\317\321\324" +
		"\325\326\330\335\337\344\350\361\370\371\u010a\0\1\2\5\12\25\26\30\31\44\45\56\63" +
		"\65\76\77\101\104\117\125\126\137\147\152\160\165\167\173\177\202\206\211\213\214" +
		"\215\234\235\237\240\241\250\252\263\272\277\300\307\314\317\321\324\325\326\330" +
		"\335\337\344\350\361\370\371\u010a\1\2\5\12\25\26\30\31\40\44\45\56\63\65\76\77\101" +
		"\104\117\125\126\137\147\152\160\165\167\173\177\202\206\211\213\214\215\234\235" +
		"\237\240\241\250\252\263\272\277\300\307\314\317\321\324\325\326\330\335\337\344" +
		"\350\361\370\371\u010a\1\2\5\12\25\26\30\31\44\45\56\63\65\70\76\77\101\104\117\125" +
		"\126\137\147\152\160\165\167\173\177\202\206\211\213\214\215\234\235\237\240\241" +
		"\250\252\263\272\277\300\307\314\317\321\324\325\326\330\335\337\344\350\361\370" +
		"\371\u010a\1\2\5\12\25\26\30\31\44\45\56\63\65\76\77\101\104\117\122\125\126\137" +
		"\147\152\160\165\167\173\177\202\206\211\213\214\215\234\235\237\240\241\250\252" +
		"\263\272\277\300\307\314\317\321\324\325\326\330\335\337\344\350\361\370\371\u010a" +
		"\211\240\250\252\277\300\304\321\330\335\344\350\207\0\40\60\65\76\101\104\126\152" +
		"\167\173\211\214\215\237\240\250\252\263\272\277\300\317\321\324\326\330\335\344" +
		"\350\1\5\26\44\56\77\202\206\211\213\234\235\240\241\250\252\263\277\300\307\314" +
		"\321\324\325\326\330\335\337\344\350\361\370\371\u010a\103\166\223\117\137\117\137" +
		"\147\160\116\151\76\76\104\76\104\76\104\163\227\302\76\104\125\177\101\126\152\152" +
		"\167\152\167\166\223\164\152\167\202\307\206\370\211\240\277\300\350\211\240\277" +
		"\300\344\350\211\240\277\300\344\350\211\240\250\252\277\300\321\330\344\350\211" +
		"\240\250\277\300\344\350\211\240\250\252\277\300\321\330\335\344\350\211\240\250" +
		"\252\277\300\321\330\335\344\350\211\240\250\252\263\277\300\321\330\335\344\350" +
		"\211\240\250\252\263\277\300\321\324\326\330\335\344\350\211\240\250\252\263\277" +
		"\300\321\324\326\330\335\344\350\211\240\250\252\277\300\321\330\335\344\350\211" +
		"\240\250\252\263\277\300\321\324\326\330\335\344\350\211\240\250\252\263\277\300" +
		"\321\324\325\326\330\335\344\350\211\240\250\252\277\300\321\330\335\344\350\152" +
		"\167\237\152\167\175\211\237\240\250\252\264\271\277\300\321\330\335\343\344\350" +
		"\211\240\250\252\277\300\321\330\335\344\350\1\5\26\44\56\77\235\361\1\5\26\44\56" +
		"\77\235\337\361\30\34\53\12\12\165\211\240\250\252\277\300\304\321\330\335\344\350" +
		"\1\5\26\44\56\65\77\104\165\167\211\235\240\252\277\300\321\330\344\350\361\40\55" +
		"\60\103\163\201\227\304\101\214\215\272\202\211\240\277\300\350\211\240\250\252\277" +
		"\300\321\330\344\350\152\167\211\237\240\250\252\264\277\300\321\330\335\344\350" +
		"\235\241\30\5\5");

	private static final short[] lapg_sym_to = TMLexer.unpack_short(1913,
		"\u0112\u0113\130\130\3\3\3\3\3\66\75\3\132\3\3\3\4\4\4\4\57\4\4\226\4\4\4\164\164" +
		"\236\236\236\236\236\236\236\236\236\236\211\277\300\350\70\122\344\371\41\41\77" +
		"\116\324\42\42\125\177\55\74\100\306\310\327\u0104\u0105\u010c\31\133\133\133\133" +
		"\31\26\45\126\134\134\134\134\307\361\43\43\151\325\325\5\5\5\5\5\101\5\101\135\135" +
		"\135\135\237\5\237\237\237\237\237\5\27\127\136\136\136\136\364\365\u0109\25\30\54" +
		"\117\137\137\137\137\117\240\117\302\240\240\240\240\240\240\240\240\240\240\240" +
		"\240\240\240\241\241\241\241\241\241\241\241\241\241\241\40\46\62\156\157\161\176" +
		"\367\372\u0107\u010d\362\140\140\140\140\141\141\141\141\142\142\142\142\340\340" +
		"\u010e\341\341\u010f\326\143\143\143\143\336\144\144\144\144\335\335\335\145\145" +
		"\145\165\145\165\165\165\165\165\165\165\165\165\165\165\165\165\165\165\165\165" +
		"\6\6\6\6\6\6\6\6\6\6\6\6\6\6\6\6\6\6\6\6\6\7\16\7\22\32\7\34\37\7\53\7\67\71\102" +
		"\7\112\102\146\153\112\146\146\112\146\22\112\112\153\7\7\242\7\112\112\7\7\112\242" +
		"\7\242\242\242\112\242\242\7\7\112\242\242\7\242\242\242\7\242\242\7\7\7\7\10\10" +
		"\10\10\10\10\10\10\10\11\11\11\11\11\11\11\11\11\12\12\12\12\12\12\12\12\370\337" +
		"\63\7\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112\102\146\153\112\146\146\112\146\22" +
		"\212\112\112\153\7\7\242\7\112\112\212\7\7\112\242\7\242\242\242\112\242\242\7\7" +
		"\112\242\242\7\242\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37\7\53\7\67\71\102" +
		"\7\112\102\146\153\112\146\146\112\146\22\112\112\153\7\7\242\7\112\112\7\7\314\112" +
		"\242\7\242\242\242\112\242\242\7\7\112\242\242\7\242\242\242\7\242\242\7\7\7\7\7" +
		"\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112\102\146\153\112\146\146\112\146\22\112" +
		"\112\153\7\7\242\7\112\112\7\7\315\112\242\7\242\242\242\112\242\242\7\7\112\242" +
		"\242\7\242\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112" +
		"\102\146\153\112\146\146\112\146\22\213\112\112\153\7\7\242\7\112\112\213\7\7\112" +
		"\242\7\242\242\242\112\242\242\7\7\112\242\242\7\242\242\242\7\242\242\7\7\7\7\7" +
		"\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112\102\146\153\112\146\146\112\146\202\22" +
		"\112\112\153\7\7\242\7\112\112\7\7\112\242\7\242\242\242\112\242\242\7\7\112\242" +
		"\242\7\242\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112" +
		"\102\146\153\112\146\146\112\146\203\22\112\112\153\7\7\242\7\112\112\7\7\112\242" +
		"\7\242\242\242\112\242\242\7\7\112\242\242\7\242\242\242\7\242\242\7\7\7\7\7\16\7" +
		"\22\32\7\34\37\7\53\7\67\71\102\7\112\102\146\153\112\146\146\112\146\204\22\112" +
		"\112\153\7\7\242\7\112\112\7\7\112\242\7\242\242\242\112\242\242\7\7\112\242\242" +
		"\7\242\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112\102" +
		"\146\153\112\146\146\112\146\205\22\112\112\153\7\7\242\7\112\112\7\7\112\242\7\242" +
		"\242\242\112\242\242\7\7\112\242\242\7\242\242\242\7\242\242\7\7\7\7\7\16\7\22\32" +
		"\7\34\37\7\53\7\67\71\102\7\112\102\146\153\112\146\146\112\146\22\112\112\153\7" +
		"\7\242\7\112\112\305\7\7\112\242\7\242\242\242\112\242\242\7\7\112\242\242\7\242" +
		"\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112\102\146\153" +
		"\112\146\146\112\146\22\112\112\153\7\7\242\7\112\112\7\7\112\242\7\242\242\242\112" +
		"\242\242\351\7\7\112\242\242\7\242\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37" +
		"\7\53\7\67\71\102\7\112\102\146\153\112\146\146\112\146\22\214\112\112\153\7\7\242" +
		"\272\7\112\112\214\7\7\112\242\7\242\242\242\112\242\242\352\7\7\112\242\242\7\242" +
		"\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112\102\146\153" +
		"\112\146\146\112\146\22\215\112\112\153\7\7\242\7\112\112\215\7\7\112\242\7\242\242" +
		"\242\112\242\242\7\7\112\242\242\7\242\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34" +
		"\37\7\53\7\67\71\102\7\112\102\146\153\112\146\146\112\146\22\216\112\112\153\7\7" +
		"\242\7\112\112\216\7\7\112\242\7\242\242\242\112\242\242\7\7\112\242\242\7\242\242" +
		"\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112\102\146\153\112" +
		"\146\146\112\146\22\112\112\153\7\7\242\7\112\112\7\7\112\242\7\242\242\242\112\242" +
		"\242\353\7\7\112\242\242\7\242\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37\7\53" +
		"\7\67\71\102\7\112\102\146\153\112\146\146\112\146\22\112\112\153\7\7\242\7\112\112" +
		"\7\7\112\242\7\242\242\242\112\242\242\354\7\7\112\242\242\7\242\242\242\7\242\242" +
		"\7\7\7\7\2\7\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112\102\146\153\112\146\146\112" +
		"\146\22\112\112\153\7\7\242\7\112\112\7\7\112\242\7\242\242\242\112\242\242\7\7\112" +
		"\242\242\7\242\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37\47\7\53\7\67\71\102" +
		"\7\112\102\146\153\112\146\146\112\146\22\112\112\153\7\7\242\7\112\112\7\7\112\242" +
		"\7\242\242\242\112\242\242\7\7\112\242\242\7\242\242\242\7\242\242\7\7\7\7\7\16\7" +
		"\22\32\7\34\37\7\53\7\67\71\76\102\7\112\102\146\153\112\146\146\112\146\22\112\112" +
		"\153\7\7\242\7\112\112\7\7\112\242\7\242\242\242\112\242\242\7\7\112\242\242\7\242" +
		"\242\242\7\242\242\7\7\7\7\7\16\7\22\32\7\34\37\7\53\7\67\71\102\7\112\102\146\152" +
		"\153\112\146\146\112\146\22\112\112\153\7\7\242\7\112\112\7\7\112\242\7\242\242\242" +
		"\112\242\242\7\7\112\242\242\7\242\242\242\7\242\242\7\7\7\7\243\243\243\243\243" +
		"\243\243\243\243\243\243\243\235\u0110\50\64\72\103\113\103\113\166\166\223\244\274" +
		"\274\316\244\244\244\244\274\244\244\366\244\373\373\244\244\244\244\13\13\13\13" +
		"\13\13\230\233\245\273\311\13\245\322\245\245\245\245\245\230\363\245\245\245\245" +
		"\245\245\u0101\245\245\13\233\u010b\311\120\217\217\147\160\150\150\162\162\131\163" +
		"\104\105\123\106\106\107\107\200\303\355\110\110\154\225\114\155\167\170\221\171" +
		"\171\220\301\206\172\172\231\360\234\u010a\246\320\346\347\u0106\247\247\247\247" +
		"\u0103\247\250\250\250\250\250\250\251\251\251\251\251\251\251\251\251\251\252\321" +
		"\330\252\252\252\252\253\253\253\332\253\253\332\332\u0100\253\253\254\254\254\254" +
		"\254\254\254\254\254\254\254\255\255\255\255\342\255\255\255\255\255\255\255\256" +
		"\256\256\256\256\256\256\256\374\376\256\256\256\256\257\257\257\257\257\257\257" +
		"\257\257\257\257\257\257\257\260\260\260\260\260\260\260\260\260\260\260\261\261" +
		"\261\261\261\261\261\261\261\261\261\261\261\261\262\262\262\262\262\262\262\262" +
		"\262\375\262\262\262\262\262\263\263\263\263\263\263\263\263\263\263\263\173\173" +
		"\317\174\174\224\174\174\174\174\174\174\224\174\174\174\174\174\224\174\174\264" +
		"\264\264\264\264\264\264\264\264\264\264\u0111\17\33\52\61\111\312\u0108\14\14\14" +
		"\14\14\14\14\u0102\14\35\44\56\23\24\207\265\265\265\265\265\265\356\265\265\265" +
		"\265\265\15\15\15\15\15\73\15\124\210\222\266\15\266\333\266\266\333\333\266\266" +
		"\15\51\60\65\121\201\227\304\357\115\275\276\345\232\267\267\267\267\267\270\270" +
		"\331\334\270\270\334\377\270\270\175\175\271\175\271\271\271\343\271\271\271\271" +
		"\271\271\271\313\323\36\20\21");

	private static final short[] lapg_rlen = TMLexer.unpack_short(173,
		"\1\0\2\0\2\0\17\14\4\4\3\3\1\1\1\3\3\2\1\1\1\1\1\1\1\1\1\1\1\1\3\2\1\1\2\2\1\1\1" +
		"\3\1\0\1\0\1\0\1\0\1\0\10\3\2\3\1\1\1\1\3\1\3\1\3\1\1\2\2\1\1\6\5\5\4\2\1\0\3\2\2" +
		"\1\1\1\1\1\4\3\1\4\2\1\1\2\1\3\3\1\1\1\0\3\2\2\1\1\3\4\3\3\2\1\2\2\1\1\1\1\2\1\3" +
		"\3\1\2\1\3\3\3\1\3\1\3\6\6\2\2\2\1\1\2\1\1\3\1\5\2\2\3\1\3\1\1\1\0\5\3\1\1\0\3\1" +
		"\1\1\1\1\3\5\1\1\1\1\1\3\1\1");

	private static final short[] lapg_rlex = TMLexer.unpack_short(173,
		"\162\162\163\163\164\164\74\74\75\76\76\77\77\100\101\102\102\103\103\104\104\104" +
		"\104\104\104\104\104\104\104\104\104\104\105\106\106\106\107\107\107\110\165\165" +
		"\166\166\167\167\170\170\171\171\111\111\112\113\114\114\114\114\172\172\115\116" +
		"\117\117\120\120\120\121\121\122\122\122\122\123\173\173\123\123\123\123\123\124" +
		"\124\124\125\174\174\125\126\126\127\127\130\130\175\175\131\176\176\132\132\132" +
		"\132\132\133\133\133\134\134\135\135\135\136\136\136\137\137\140\140\140\141\141" +
		"\142\142\142\143\144\144\145\145\145\145\145\145\177\177\146\146\146\147\200\200" +
		"\150\150\150\201\201\151\152\152\202\202\152\203\203\204\204\152\152\153\153\153" +
		"\153\154\154\155\155\155\156\157\157\160\161");

	protected static final String[] lapg_syms = new String[] {
		"eoi",
		"regexp",
		"scon",
		"icon",
		"_skip",
		"_skip_comment",
		"'%'",
		"'::='",
		"'::'",
		"'|'",
		"'='",
		"'=>'",
		"';'",
		"'.'",
		"','",
		"':'",
		"'['",
		"']'",
		"'('",
		"'(?!'",
		"')'",
		"'}'",
		"'<'",
		"'>'",
		"'*'",
		"'+'",
		"'+='",
		"'?'",
		"'&'",
		"'@'",
		"error",
		"ID",
		"Ltrue",
		"Lfalse",
		"Lnew",
		"Lseparator",
		"Las",
		"Limport",
		"Linline",
		"Lprio",
		"Lshift",
		"Lreturns",
		"Linput",
		"Lleft",
		"Lright",
		"Lnonassoc",
		"Lnoeoi",
		"Lsoft",
		"Lclass",
		"Linterface",
		"Lvoid",
		"Lspace",
		"Llayout",
		"Llanguage",
		"Llalr",
		"Llexer",
		"Lparser",
		"Lreduce",
		"code",
		"'{'",
		"input",
		"parsing_algorithm",
		"import_",
		"option",
		"identifier",
		"symref",
		"type",
		"type_part_list",
		"type_part",
		"pattern",
		"lexer_parts",
		"lexer_part",
		"named_pattern",
		"lexeme",
		"lexem_transition",
		"lexem_attrs",
		"lexem_attribute",
		"state_selector",
		"stateref",
		"lexer_state",
		"grammar_parts",
		"grammar_part",
		"nonterm",
		"nonterm_type",
		"priority_kw",
		"directive",
		"inputref",
		"references",
		"references_cs",
		"rules",
		"rule0",
		"rhsPrefix",
		"rhsSuffix",
		"rhsParts",
		"rhsPart",
		"rhsAnnotated",
		"rhsAssignment",
		"rhsOptional",
		"rhsCast",
		"rhsUnordered",
		"rhsClass",
		"rhsPrimary",
		"rhsAnnotations",
		"annotations",
		"annotation",
		"negative_la",
		"expression",
		"literal",
		"map_entries",
		"map_separator",
		"name",
		"qualified_id",
		"command",
		"syntax_problem",
		"parsing_algorithmopt",
		"import__optlist",
		"option_optlist",
		"typeopt",
		"lexem_transitionopt",
		"iconopt",
		"lexem_attrsopt",
		"commandopt",
		"lexer_state_list",
		"identifieropt",
		"inputref_list",
		"rule0_list",
		"rhsSuffixopt",
		"annotation_list",
		"expression_list",
		"symref_list",
		"map_entriesopt",
		"expression_list1",
		"expression_list1_opt",
	};

	public interface Tokens extends Lexems {
		// non-terminals
		public static final int input = 60;
		public static final int parsing_algorithm = 61;
		public static final int import_ = 62;
		public static final int option = 63;
		public static final int identifier = 64;
		public static final int symref = 65;
		public static final int type = 66;
		public static final int type_part_list = 67;
		public static final int type_part = 68;
		public static final int pattern = 69;
		public static final int lexer_parts = 70;
		public static final int lexer_part = 71;
		public static final int named_pattern = 72;
		public static final int lexeme = 73;
		public static final int lexem_transition = 74;
		public static final int lexem_attrs = 75;
		public static final int lexem_attribute = 76;
		public static final int state_selector = 77;
		public static final int stateref = 78;
		public static final int lexer_state = 79;
		public static final int grammar_parts = 80;
		public static final int grammar_part = 81;
		public static final int nonterm = 82;
		public static final int nonterm_type = 83;
		public static final int priority_kw = 84;
		public static final int directive = 85;
		public static final int inputref = 86;
		public static final int references = 87;
		public static final int references_cs = 88;
		public static final int rules = 89;
		public static final int rule0 = 90;
		public static final int rhsPrefix = 91;
		public static final int rhsSuffix = 92;
		public static final int rhsParts = 93;
		public static final int rhsPart = 94;
		public static final int rhsAnnotated = 95;
		public static final int rhsAssignment = 96;
		public static final int rhsOptional = 97;
		public static final int rhsCast = 98;
		public static final int rhsUnordered = 99;
		public static final int rhsClass = 100;
		public static final int rhsPrimary = 101;
		public static final int rhsAnnotations = 102;
		public static final int annotations = 103;
		public static final int annotation = 104;
		public static final int negative_la = 105;
		public static final int expression = 106;
		public static final int literal = 107;
		public static final int map_entries = 108;
		public static final int map_separator = 109;
		public static final int name = 110;
		public static final int qualified_id = 111;
		public static final int command = 112;
		public static final int syntax_problem = 113;
		public static final int parsing_algorithmopt = 114;
		public static final int import__optlist = 115;
		public static final int option_optlist = 116;
		public static final int typeopt = 117;
		public static final int lexem_transitionopt = 118;
		public static final int iconopt = 119;
		public static final int lexem_attrsopt = 120;
		public static final int commandopt = 121;
		public static final int lexer_state_list = 122;
		public static final int identifieropt = 123;
		public static final int inputref_list = 124;
		public static final int rule0_list = 125;
		public static final int rhsSuffixopt = 126;
		public static final int annotation_list = 127;
		public static final int expression_list = 128;
		public static final int symref_list = 129;
		public static final int map_entriesopt = 130;
		public static final int expression_list1 = 131;
		public static final int expression_list1_opt = 132;
	}

	public interface Rules {
		public static final int directive_input = 87;  // directive ::= '%' Linput inputref_list ';'
		public static final int rhsPrimary_symbol = 128;  // rhsPrimary ::= symref
		public static final int rhsPrimary_group = 129;  // rhsPrimary ::= '(' rules ')'
		public static final int rhsPrimary_list = 130;  // rhsPrimary ::= '(' rhsParts Lseparator references ')' '+'
		public static final int rhsPrimary_list2 = 131;  // rhsPrimary ::= '(' rhsParts Lseparator references ')' '*'
		public static final int rhsPrimary_list3 = 132;  // rhsPrimary ::= rhsPrimary '*'
		public static final int rhsPrimary_list4 = 133;  // rhsPrimary ::= rhsPrimary '+'
		public static final int expression_instance = 152;  // expression ::= Lnew name '(' map_entriesopt ')'
		public static final int expression_array = 157;  // expression ::= '[' expression_list1_opt ']'
		public static final int literal_literal = 159;  // literal ::= scon
		public static final int literal_literal2 = 160;  // literal ::= icon
		public static final int literal_literal3 = 161;  // literal ::= Ltrue
		public static final int literal_literal4 = 162;  // literal ::= Lfalse
	}

	/**
	 * -3-n   Lookahead (state id)
	 * -2     Error
	 * -1     Shift
	 * 0..n   Reduce (rule index)
	 */
	protected static int tmAction(int state, int symbol) {
		int p;
		if (tmAction[state] < -2) {
			for (p = -tmAction[state] - 3; tmLalr[p] >= 0; p += 2) {
				if (tmLalr[p] == symbol) {
					break;
				}
			}
			return tmLalr[p + 1];
		}
		return tmAction[state];
	}

	protected static int tmGoto(int state, int symbol) {
		int min = lapg_sym_goto[symbol], max = lapg_sym_goto[symbol + 1] - 1;
		int i, e;

		while (min <= max) {
			e = (min + max) >> 1;
			i = lapg_sym_from[e];
			if (i == state) {
				return lapg_sym_to[e];
			} else if (i < state) {
				min = e + 1;
			} else {
				max = e - 1;
			}
		}
		return -1;
	}

	protected int tmHead;
	protected LapgSymbol[] tmStack;
	protected LapgSymbol tmNext;
	protected TMLexer tmLexer;

	private Object parse(TMLexer lexer, int initialState, int finalState) throws IOException, ParseException {

		tmLexer = lexer;
		tmStack = new LapgSymbol[1024];
		tmHead = 0;
		int lapg_symbols_ok = 4;

		tmStack[0] = new LapgSymbol();
		tmStack[0].state = initialState;
		tmNext = tmLexer.next();

		while (tmStack[tmHead].state != finalState) {
			int action = tmAction(tmStack[tmHead].state, tmNext.symbol);

			if (action >= 0) {
				reduce(action);
			} else if (action == -1) {
				shift();
				lapg_symbols_ok++;
			}

			if (action == -2 || tmStack[tmHead].state == -1) {
				if (restore()) {
					if (lapg_symbols_ok >= 4) {
						reporter.error(tmNext.offset, tmNext.endoffset, tmNext.line,
								MessageFormat.format("syntax error before line {0}", tmLexer.getTokenLine()));
					}
					if (lapg_symbols_ok <= 1) {
						tmNext = tmLexer.next();
					}
					lapg_symbols_ok = 0;
					continue;
				}
				if (tmHead < 0) {
					tmHead = 0;
					tmStack[0] = new LapgSymbol();
					tmStack[0].state = initialState;
				}
				break;
			}
		}

		if (tmStack[tmHead].state != finalState) {
			if (lapg_symbols_ok >= 4) {
				reporter.error(tmNext.offset, tmNext.endoffset, tmNext.line,
						MessageFormat.format("syntax error before line {0}",
								tmLexer.getTokenLine()));
			}
			throw new ParseException();
		}
		return tmStack[tmHead - 1].value;
	}

	protected boolean restore() {
		if (tmNext.symbol == 0) {
			return false;
		}
		while (tmHead >= 0 && tmGoto(tmStack[tmHead].state, 30) == -1) {
			dispose(tmStack[tmHead]);
			tmStack[tmHead] = null;
			tmHead--;
		}
		if (tmHead >= 0) {
			tmStack[++tmHead] = new LapgSymbol();
			tmStack[tmHead].symbol = 30;
			tmStack[tmHead].value = null;
			tmStack[tmHead].state = tmGoto(tmStack[tmHead - 1].state, 30);
			tmStack[tmHead].line = tmNext.line;
			tmStack[tmHead].offset = tmNext.offset;
			tmStack[tmHead].endoffset = tmNext.endoffset;
			return true;
		}
		return false;
	}

	protected void shift() throws IOException {
		tmStack[++tmHead] = tmNext;
		tmStack[tmHead].state = tmGoto(tmStack[tmHead - 1].state, tmNext.symbol);
		if (DEBUG_SYNTAX) {
			System.out.println(MessageFormat.format("shift: {0} ({1})", lapg_syms[tmNext.symbol], tmLexer.current()));
		}
		if (tmStack[tmHead].state != -1 && tmNext.symbol != 0) {
			tmNext = tmLexer.next();
		}
	}

	protected void reduce(int rule) {
		LapgSymbol lapg_gg = new LapgSymbol();
		lapg_gg.value = (lapg_rlen[rule] != 0) ? tmStack[tmHead + 1 - lapg_rlen[rule]].value : null;
		lapg_gg.symbol = lapg_rlex[rule];
		lapg_gg.state = 0;
		if (DEBUG_SYNTAX) {
			System.out.println("reduce to " + lapg_syms[lapg_rlex[rule]]);
		}
		LapgSymbol startsym = (lapg_rlen[rule] != 0) ? tmStack[tmHead + 1 - lapg_rlen[rule]] : tmNext;
		lapg_gg.line = startsym.line;
		lapg_gg.offset = startsym.offset;
		lapg_gg.endoffset = (lapg_rlen[rule] != 0) ? tmStack[tmHead].endoffset : tmNext.offset;
		applyRule(lapg_gg, rule, lapg_rlen[rule]);
		for (int e = lapg_rlen[rule]; e > 0; e--) {
			cleanup(tmStack[tmHead]);
			tmStack[tmHead--] = null;
		}
		tmStack[++tmHead] = lapg_gg;
		tmStack[tmHead].state = tmGoto(tmStack[tmHead - 1].state, lapg_gg.symbol);
	}

	@SuppressWarnings("unchecked")
	protected void applyRule(LapgSymbol lapg_gg, int rule, int ruleLength) {
		switch (rule) {
			case 2:  // import__optlist ::= import__optlist import_
				((List<TmaImport>)lapg_gg.value).add(((TmaImport)tmStack[tmHead].value));
				break;
			case 3:  // import__optlist ::=
				lapg_gg.value = new ArrayList();
				break;
			case 4:  // option_optlist ::= option_optlist option
				((List<TmaOption>)lapg_gg.value).add(((TmaOption)tmStack[tmHead].value));
				break;
			case 5:  // option_optlist ::=
				lapg_gg.value = new ArrayList();
				break;
			case 6:  // input ::= Llanguage ID '(' ID ')' parsing_algorithmopt ';' import__optlist option_optlist '::' Llexer lexer_parts '::' Lparser grammar_parts
				lapg_gg.value = new TmaInput(
						((String)tmStack[tmHead - 13].value) /* name */,
						((String)tmStack[tmHead - 11].value) /* target */,
						((TmaParsingAlgorithm)tmStack[tmHead - 9].value) /* parsingAlgorithm */,
						((List<TmaImport>)tmStack[tmHead - 7].value) /* importOptlist */,
						((List<TmaOption>)tmStack[tmHead - 6].value) /* optionOptlist */,
						((List<TmaLexerPartsItem>)tmStack[tmHead - 3].value) /* lexerParts */,
						((List<TmaGrammarPartsItem>)tmStack[tmHead].value) /* grammarParts */,
						null /* input */, tmStack[tmHead - 14].offset, tmStack[tmHead].endoffset);
				break;
			case 7:  // input ::= Llanguage ID '(' ID ')' parsing_algorithmopt ';' import__optlist option_optlist '::' Llexer lexer_parts
				lapg_gg.value = new TmaInput(
						((String)tmStack[tmHead - 10].value) /* name */,
						((String)tmStack[tmHead - 8].value) /* target */,
						((TmaParsingAlgorithm)tmStack[tmHead - 6].value) /* parsingAlgorithm */,
						((List<TmaImport>)tmStack[tmHead - 4].value) /* importOptlist */,
						((List<TmaOption>)tmStack[tmHead - 3].value) /* optionOptlist */,
						((List<TmaLexerPartsItem>)tmStack[tmHead].value) /* lexerParts */,
						null /* grammarParts */,
						null /* input */, tmStack[tmHead - 11].offset, tmStack[tmHead].endoffset);
				break;
			case 8:  // parsing_algorithm ::= Llalr '(' icon ')'
				lapg_gg.value = new TmaParsingAlgorithm(
						((Integer)tmStack[tmHead - 1].value) /* la */,
						null /* input */, tmStack[tmHead - 3].offset, tmStack[tmHead].endoffset);
				break;
			case 9:  // import_ ::= Limport ID scon ';'
				lapg_gg.value = new TmaImport(
						((String)tmStack[tmHead - 2].value) /* alias */,
						((String)tmStack[tmHead - 1].value) /* file */,
						null /* input */, tmStack[tmHead - 3].offset, tmStack[tmHead].endoffset);
				break;
			case 10:  // import_ ::= Limport scon ';'
				lapg_gg.value = new TmaImport(
						null /* alias */,
						((String)tmStack[tmHead - 1].value) /* file */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 11:  // option ::= ID '=' expression
				lapg_gg.value = new TmaOption(
						((String)tmStack[tmHead - 2].value) /* ID */,
						((ITmaExpression)tmStack[tmHead].value) /* expression */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 12:  // option ::= syntax_problem
				lapg_gg.value = new TmaOption(
						null /* ID */,
						null /* expression */,
						((TmaSyntaxProblem)tmStack[tmHead].value) /* syntaxProblem */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 13:  // identifier ::= ID
				lapg_gg.value = new TmaIdentifier(
						((String)tmStack[tmHead].value) /* ID */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 14:  // symref ::= ID
				lapg_gg.value = new TmaSymref(
						((String)tmStack[tmHead].value) /* ID */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 15:  // type ::= '(' scon ')'
				 lapg_gg.value = ((String)tmStack[tmHead - 1].value); 
				break;
			case 16:  // type ::= '(' type_part_list ')'
				 lapg_gg.value = source.getText(tmStack[tmHead - 2].offset+1, tmStack[tmHead].endoffset-1); 
				break;
			case 32:  // pattern ::= regexp
				lapg_gg.value = new TmaPattern(
						((String)tmStack[tmHead].value) /* regexp */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 33:  // lexer_parts ::= lexer_part
				lapg_gg.value = new ArrayList();
				((List<TmaLexerPartsItem>)lapg_gg.value).add(new TmaLexerPartsItem(
						((ITmaLexerPart)tmStack[tmHead].value) /* lexerPart */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset));
				break;
			case 34:  // lexer_parts ::= lexer_parts lexer_part
				((List<TmaLexerPartsItem>)lapg_gg.value).add(new TmaLexerPartsItem(
						((ITmaLexerPart)tmStack[tmHead].value) /* lexerPart */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset));
				break;
			case 35:  // lexer_parts ::= lexer_parts syntax_problem
				((List<TmaLexerPartsItem>)lapg_gg.value).add(new TmaLexerPartsItem(
						null /* lexerPart */,
						((TmaSyntaxProblem)tmStack[tmHead].value) /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset));
				break;
			case 39:  // named_pattern ::= ID '=' pattern
				lapg_gg.value = new TmaNamedPattern(
						((String)tmStack[tmHead - 2].value) /* name */,
						((TmaPattern)tmStack[tmHead].value) /* pattern */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 50:  // lexeme ::= identifier typeopt ':' pattern lexem_transitionopt iconopt lexem_attrsopt commandopt
				lapg_gg.value = new TmaLexeme(
						((TmaIdentifier)tmStack[tmHead - 7].value) /* name */,
						((Integer)tmStack[tmHead - 2].value) /* priority */,
						((String)tmStack[tmHead - 6].value) /* type */,
						((TmaPattern)tmStack[tmHead - 4].value) /* pattern */,
						((TmaStateref)tmStack[tmHead - 3].value) /* lexemTransition */,
						((TmaLexemAttrs)tmStack[tmHead - 1].value) /* lexemAttrs */,
						((TmaCommand)tmStack[tmHead].value) /* command */,
						null /* input */, tmStack[tmHead - 7].offset, tmStack[tmHead].endoffset);
				break;
			case 51:  // lexeme ::= identifier typeopt ':'
				lapg_gg.value = new TmaLexeme(
						((TmaIdentifier)tmStack[tmHead - 2].value) /* name */,
						null /* priority */,
						((String)tmStack[tmHead - 1].value) /* type */,
						null /* pattern */,
						null /* lexemTransition */,
						null /* lexemAttrs */,
						null /* command */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 52:  // lexem_transition ::= '=>' stateref
				lapg_gg.value = ((TmaStateref)tmStack[tmHead].value);
				break;
			case 53:  // lexem_attrs ::= '(' lexem_attribute ')'
				lapg_gg.value = new TmaLexemAttrs(
						((TmaLexemAttribute)tmStack[tmHead - 1].value) /* lexemAttribute */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 54:  // lexem_attribute ::= Lsoft
				lapg_gg.value = TmaLexemAttribute.LSOFT;
				break;
			case 55:  // lexem_attribute ::= Lclass
				lapg_gg.value = TmaLexemAttribute.LCLASS;
				break;
			case 56:  // lexem_attribute ::= Lspace
				lapg_gg.value = TmaLexemAttribute.LSPACE;
				break;
			case 57:  // lexem_attribute ::= Llayout
				lapg_gg.value = TmaLexemAttribute.LLAYOUT;
				break;
			case 58:  // lexer_state_list ::= lexer_state_list ',' lexer_state
				((List<TmaLexerState>)lapg_gg.value).add(((TmaLexerState)tmStack[tmHead].value));
				break;
			case 59:  // lexer_state_list ::= lexer_state
				lapg_gg.value = new ArrayList();
				((List<TmaLexerState>)lapg_gg.value).add(((TmaLexerState)tmStack[tmHead].value));
				break;
			case 60:  // state_selector ::= '[' lexer_state_list ']'
				lapg_gg.value = new TmaStateSelector(
						((List<TmaLexerState>)tmStack[tmHead - 1].value) /* states */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 61:  // stateref ::= ID
				lapg_gg.value = new TmaStateref(
						((String)tmStack[tmHead].value) /* ID */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 62:  // lexer_state ::= identifier '=>' stateref
				lapg_gg.value = new TmaLexerState(
						((TmaIdentifier)tmStack[tmHead - 2].value) /* name */,
						((TmaStateref)tmStack[tmHead].value) /* defaultTransition */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 63:  // lexer_state ::= identifier
				lapg_gg.value = new TmaLexerState(
						((TmaIdentifier)tmStack[tmHead].value) /* name */,
						null /* defaultTransition */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 64:  // grammar_parts ::= grammar_part
				lapg_gg.value = new ArrayList();
				((List<TmaGrammarPartsItem>)lapg_gg.value).add(new TmaGrammarPartsItem(
						((ITmaGrammarPart)tmStack[tmHead].value) /* grammarPart */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset));
				break;
			case 65:  // grammar_parts ::= grammar_parts grammar_part
				((List<TmaGrammarPartsItem>)lapg_gg.value).add(new TmaGrammarPartsItem(
						((ITmaGrammarPart)tmStack[tmHead].value) /* grammarPart */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset));
				break;
			case 66:  // grammar_parts ::= grammar_parts syntax_problem
				((List<TmaGrammarPartsItem>)lapg_gg.value).add(new TmaGrammarPartsItem(
						null /* grammarPart */,
						((TmaSyntaxProblem)tmStack[tmHead].value) /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset));
				break;
			case 69:  // nonterm ::= annotations identifier nonterm_type '::=' rules ';'
				lapg_gg.value = new TmaNonterm(
						((TmaIdentifier)tmStack[tmHead - 4].value) /* name */,
						((TmaNontermType)tmStack[tmHead - 3].value) /* type */,
						((TmaAnnotations)tmStack[tmHead - 5].value) /* annotations */,
						((List<TmaRule0>)tmStack[tmHead - 1].value) /* rules */,
						null /* input */, tmStack[tmHead - 5].offset, tmStack[tmHead].endoffset);
				break;
			case 70:  // nonterm ::= annotations identifier '::=' rules ';'
				lapg_gg.value = new TmaNonterm(
						((TmaIdentifier)tmStack[tmHead - 3].value) /* name */,
						null /* type */,
						((TmaAnnotations)tmStack[tmHead - 4].value) /* annotations */,
						((List<TmaRule0>)tmStack[tmHead - 1].value) /* rules */,
						null /* input */, tmStack[tmHead - 4].offset, tmStack[tmHead].endoffset);
				break;
			case 71:  // nonterm ::= identifier nonterm_type '::=' rules ';'
				lapg_gg.value = new TmaNonterm(
						((TmaIdentifier)tmStack[tmHead - 4].value) /* name */,
						((TmaNontermType)tmStack[tmHead - 3].value) /* type */,
						null /* annotations */,
						((List<TmaRule0>)tmStack[tmHead - 1].value) /* rules */,
						null /* input */, tmStack[tmHead - 4].offset, tmStack[tmHead].endoffset);
				break;
			case 72:  // nonterm ::= identifier '::=' rules ';'
				lapg_gg.value = new TmaNonterm(
						((TmaIdentifier)tmStack[tmHead - 3].value) /* name */,
						null /* type */,
						null /* annotations */,
						((List<TmaRule0>)tmStack[tmHead - 1].value) /* rules */,
						null /* input */, tmStack[tmHead - 3].offset, tmStack[tmHead].endoffset);
				break;
			case 73:  // nonterm_type ::= Lreturns symref
				lapg_gg.value = new TmaNontermType(
						null /* name */,
						((TmaSymref)tmStack[tmHead].value) /* symref */,
						null /* type */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 76:  // nonterm_type ::= Linline Lclass identifieropt
				lapg_gg.value = new TmaNontermType(
						((TmaIdentifier)tmStack[tmHead].value) /* name */,
						null /* symref */,
						null /* type */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 77:  // nonterm_type ::= Lclass identifieropt
				lapg_gg.value = new TmaNontermType(
						((TmaIdentifier)tmStack[tmHead].value) /* name */,
						null /* symref */,
						null /* type */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 78:  // nonterm_type ::= Linterface identifieropt
				lapg_gg.value = new TmaNontermType(
						((TmaIdentifier)tmStack[tmHead].value) /* name */,
						null /* symref */,
						null /* type */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 79:  // nonterm_type ::= Lvoid
				lapg_gg.value = new TmaNontermType(
						null /* name */,
						null /* symref */,
						null /* type */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 80:  // nonterm_type ::= type
				lapg_gg.value = new TmaNontermType(
						null /* name */,
						null /* symref */,
						((String)tmStack[tmHead].value) /* type */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 81:  // priority_kw ::= Lleft
				lapg_gg.value = TmaPriorityKw.LLEFT;
				break;
			case 82:  // priority_kw ::= Lright
				lapg_gg.value = TmaPriorityKw.LRIGHT;
				break;
			case 83:  // priority_kw ::= Lnonassoc
				lapg_gg.value = TmaPriorityKw.LNONASSOC;
				break;
			case 84:  // directive ::= '%' priority_kw references ';'
				lapg_gg.value = new TmaDirectiveImpl(
						((TmaPriorityKw)tmStack[tmHead - 2].value) /* priorityKw */,
						((List<TmaSymref>)tmStack[tmHead - 1].value) /* references */,
						null /* input */, tmStack[tmHead - 3].offset, tmStack[tmHead].endoffset);
				break;
			case 85:  // inputref_list ::= inputref_list ',' inputref
				((List<TmaInputref>)lapg_gg.value).add(((TmaInputref)tmStack[tmHead].value));
				break;
			case 86:  // inputref_list ::= inputref
				lapg_gg.value = new ArrayList();
				((List<TmaInputref>)lapg_gg.value).add(((TmaInputref)tmStack[tmHead].value));
				break;
			case 87:  // directive ::= '%' Linput inputref_list ';'
				lapg_gg.value = new TmaDirectiveInput(
						((List<TmaInputref>)tmStack[tmHead - 1].value) /* inputrefList */,
						null /* input */, tmStack[tmHead - 3].offset, tmStack[tmHead].endoffset);
				break;
			case 88:  // inputref ::= symref Lnoeoi
				lapg_gg.value = new TmaInputref(
						true /* noeoi */,
						((TmaSymref)tmStack[tmHead - 1].value) /* symref */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 89:  // inputref ::= symref
				lapg_gg.value = new TmaInputref(
						false /* noeoi */,
						((TmaSymref)tmStack[tmHead].value) /* symref */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 90:  // references ::= symref
				lapg_gg.value = new ArrayList();
				((List<TmaSymref>)lapg_gg.value).add(((TmaSymref)tmStack[tmHead].value));
				break;
			case 91:  // references ::= references symref
				((List<TmaSymref>)lapg_gg.value).add(((TmaSymref)tmStack[tmHead].value));
				break;
			case 92:  // references_cs ::= symref
				lapg_gg.value = new ArrayList();
				((List<TmaSymref>)lapg_gg.value).add(((TmaSymref)tmStack[tmHead].value));
				break;
			case 93:  // references_cs ::= references_cs ',' symref
				((List<TmaSymref>)lapg_gg.value).add(((TmaSymref)tmStack[tmHead].value));
				break;
			case 94:  // rule0_list ::= rule0_list '|' rule0
				((List<TmaRule0>)lapg_gg.value).add(((TmaRule0)tmStack[tmHead].value));
				break;
			case 95:  // rule0_list ::= rule0
				lapg_gg.value = new ArrayList();
				((List<TmaRule0>)lapg_gg.value).add(((TmaRule0)tmStack[tmHead].value));
				break;
			case 99:  // rule0 ::= rhsPrefix rhsParts rhsSuffixopt
				lapg_gg.value = new TmaRule0(
						((TmaRhsPrefix)tmStack[tmHead - 2].value) /* rhsPrefix */,
						((List<TmaRhsPartsItem>)tmStack[tmHead - 1].value) /* rhsParts */,
						((TmaRhsSuffix)tmStack[tmHead].value) /* rhsSuffix */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 100:  // rule0 ::= rhsPrefix rhsSuffixopt
				lapg_gg.value = new TmaRule0(
						((TmaRhsPrefix)tmStack[tmHead - 1].value) /* rhsPrefix */,
						null /* rhsParts */,
						((TmaRhsSuffix)tmStack[tmHead].value) /* rhsSuffix */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 101:  // rule0 ::= rhsParts rhsSuffixopt
				lapg_gg.value = new TmaRule0(
						null /* rhsPrefix */,
						((List<TmaRhsPartsItem>)tmStack[tmHead - 1].value) /* rhsParts */,
						((TmaRhsSuffix)tmStack[tmHead].value) /* rhsSuffix */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 102:  // rule0 ::= rhsSuffixopt
				lapg_gg.value = new TmaRule0(
						null /* rhsPrefix */,
						null /* rhsParts */,
						((TmaRhsSuffix)tmStack[tmHead].value) /* rhsSuffix */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 103:  // rule0 ::= syntax_problem
				lapg_gg.value = new TmaRule0(
						null /* rhsPrefix */,
						null /* rhsParts */,
						null /* rhsSuffix */,
						((TmaSyntaxProblem)tmStack[tmHead].value) /* syntaxProblem */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 104:  // rhsPrefix ::= '[' annotations ']'
				lapg_gg.value = new TmaRhsPrefix(
						((TmaAnnotations)tmStack[tmHead - 1].value) /* annotations */,
						null /* alias */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 105:  // rhsPrefix ::= '[' annotations identifier ']'
				lapg_gg.value = new TmaRhsPrefix(
						((TmaAnnotations)tmStack[tmHead - 2].value) /* annotations */,
						((TmaIdentifier)tmStack[tmHead - 1].value) /* alias */,
						null /* input */, tmStack[tmHead - 3].offset, tmStack[tmHead].endoffset);
				break;
			case 106:  // rhsPrefix ::= '[' identifier ']'
				lapg_gg.value = new TmaRhsPrefix(
						null /* annotations */,
						((TmaIdentifier)tmStack[tmHead - 1].value) /* alias */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 107:  // rhsSuffix ::= '%' Lprio symref
				lapg_gg.value = new TmaRhsSuffix(
						TmaRhsSuffix.TmaKindKind.LPRIO /* kind */,
						((TmaSymref)tmStack[tmHead].value) /* symref */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 108:  // rhsSuffix ::= '%' Lshift
				lapg_gg.value = new TmaRhsSuffix(
						TmaRhsSuffix.TmaKindKind.LSHIFT /* kind */,
						null /* symref */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 109:  // rhsParts ::= rhsPart
				lapg_gg.value = new ArrayList();
				((List<TmaRhsPartsItem>)lapg_gg.value).add(new TmaRhsPartsItem(
						((ITmaRhsPart)tmStack[tmHead].value) /* rhsPart */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset));
				break;
			case 110:  // rhsParts ::= rhsParts rhsPart
				((List<TmaRhsPartsItem>)lapg_gg.value).add(new TmaRhsPartsItem(
						((ITmaRhsPart)tmStack[tmHead].value) /* rhsPart */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset));
				break;
			case 111:  // rhsParts ::= rhsParts syntax_problem
				((List<TmaRhsPartsItem>)lapg_gg.value).add(new TmaRhsPartsItem(
						null /* rhsPart */,
						((TmaSyntaxProblem)tmStack[tmHead].value) /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset));
				break;
			case 116:  // rhsAnnotated ::= rhsAnnotations rhsAssignment
				lapg_gg.value = new TmaRhsAnnotated(
						((TmaRhsAnnotations)tmStack[tmHead - 1].value) /* rhsAnnotations */,
						((ITmaRhsPart)tmStack[tmHead].value) /* rhsAssignment */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 118:  // rhsAssignment ::= identifier '=' rhsOptional
				lapg_gg.value = new TmaRhsAssignment(
						((TmaIdentifier)tmStack[tmHead - 2].value) /* id */,
						false /* addition */,
						((ITmaRhsPart)tmStack[tmHead].value) /* inner */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 119:  // rhsAssignment ::= identifier '+=' rhsOptional
				lapg_gg.value = new TmaRhsAssignment(
						((TmaIdentifier)tmStack[tmHead - 2].value) /* id */,
						true /* addition */,
						((ITmaRhsPart)tmStack[tmHead].value) /* inner */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 121:  // rhsOptional ::= rhsCast '?'
				lapg_gg.value = new TmaRhsOptional(
						((ITmaRhsPart)tmStack[tmHead - 1].value) /* rhsCast */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 123:  // rhsCast ::= rhsClass Las symref
				lapg_gg.value = new TmaRhsCast(
						((ITmaRhsPart)tmStack[tmHead - 2].value) /* rhsClass */,
						((TmaSymref)tmStack[tmHead].value) /* symref */,
						null /* literal */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 124:  // rhsCast ::= rhsClass Las literal
				lapg_gg.value = new TmaRhsCast(
						((ITmaRhsPart)tmStack[tmHead - 2].value) /* rhsClass */,
						null /* symref */,
						((TmaLiteral)tmStack[tmHead].value) /* literal */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 125:  // rhsUnordered ::= rhsPart '&' rhsPart
				lapg_gg.value = new TmaRhsUnordered(
						((ITmaRhsPart)tmStack[tmHead - 2].value) /* left */,
						((ITmaRhsPart)tmStack[tmHead].value) /* right */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 127:  // rhsClass ::= identifier ':' rhsPrimary
				lapg_gg.value = new TmaRhsClass(
						((TmaIdentifier)tmStack[tmHead - 2].value) /* identifier */,
						((ITmaRhsPart)tmStack[tmHead].value) /* rhsPrimary */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 128:  // rhsPrimary ::= symref
				lapg_gg.value = new TmaRhsPrimarySymbol(
						((TmaSymref)tmStack[tmHead].value) /* symref */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 129:  // rhsPrimary ::= '(' rules ')'
				lapg_gg.value = new TmaRhsPrimaryGroup(
						((List<TmaRule0>)tmStack[tmHead - 1].value) /* rules */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 130:  // rhsPrimary ::= '(' rhsParts Lseparator references ')' '+'
				lapg_gg.value = new TmaRhsPrimaryList(
						TmaRhsPrimaryList.TmaQuantifierKind.PLUS /* quantifier */,
						((List<TmaRhsPartsItem>)tmStack[tmHead - 4].value) /* rhsParts */,
						((List<TmaSymref>)tmStack[tmHead - 2].value) /* references */,
						null /* rhsPrimary */,
						null /* input */, tmStack[tmHead - 5].offset, tmStack[tmHead].endoffset);
				break;
			case 131:  // rhsPrimary ::= '(' rhsParts Lseparator references ')' '*'
				lapg_gg.value = new TmaRhsPrimaryList(
						TmaRhsPrimaryList.TmaQuantifierKind.MULT /* quantifier */,
						((List<TmaRhsPartsItem>)tmStack[tmHead - 4].value) /* rhsParts */,
						((List<TmaSymref>)tmStack[tmHead - 2].value) /* references */,
						null /* rhsPrimary */,
						null /* input */, tmStack[tmHead - 5].offset, tmStack[tmHead].endoffset);
				break;
			case 132:  // rhsPrimary ::= rhsPrimary '*'
				lapg_gg.value = new TmaRhsPrimaryList(
						TmaRhsPrimaryList.TmaQuantifierKind.MULT /* quantifier */,
						null /* rhsParts */,
						null /* references */,
						((ITmaRhsPart)tmStack[tmHead - 1].value) /* rhsPrimary */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 133:  // rhsPrimary ::= rhsPrimary '+'
				lapg_gg.value = new TmaRhsPrimaryList(
						TmaRhsPrimaryList.TmaQuantifierKind.PLUS /* quantifier */,
						null /* rhsParts */,
						null /* references */,
						((ITmaRhsPart)tmStack[tmHead - 1].value) /* rhsPrimary */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 134:  // annotation_list ::= annotation_list annotation
				((List<TmaAnnotation>)lapg_gg.value).add(((TmaAnnotation)tmStack[tmHead].value));
				break;
			case 135:  // annotation_list ::= annotation
				lapg_gg.value = new ArrayList();
				((List<TmaAnnotation>)lapg_gg.value).add(((TmaAnnotation)tmStack[tmHead].value));
				break;
			case 136:  // rhsAnnotations ::= annotation_list
				lapg_gg.value = new TmaRhsAnnotations(
						((List<TmaAnnotation>)tmStack[tmHead].value) /* annotationList */,
						null /* negativeLa */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 137:  // rhsAnnotations ::= negative_la annotation_list
				lapg_gg.value = new TmaRhsAnnotations(
						((List<TmaAnnotation>)tmStack[tmHead].value) /* annotationList */,
						((TmaNegativeLa)tmStack[tmHead - 1].value) /* negativeLa */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 138:  // rhsAnnotations ::= negative_la
				lapg_gg.value = new TmaRhsAnnotations(
						null /* annotationList */,
						((TmaNegativeLa)tmStack[tmHead].value) /* negativeLa */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 139:  // annotations ::= annotation_list
				lapg_gg.value = new TmaAnnotations(
						((List<TmaAnnotation>)tmStack[tmHead].value) /* annotations */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 140:  // expression_list ::= expression_list ',' expression
				((List<ITmaExpression>)lapg_gg.value).add(((ITmaExpression)tmStack[tmHead].value));
				break;
			case 141:  // expression_list ::= expression
				lapg_gg.value = new ArrayList();
				((List<ITmaExpression>)lapg_gg.value).add(((ITmaExpression)tmStack[tmHead].value));
				break;
			case 142:  // annotation ::= '@' qualified_id '{' expression_list '}'
				lapg_gg.value = new TmaAnnotation(
						((List<ITmaExpression>)tmStack[tmHead - 1].value) /* arguments */,
						((String)tmStack[tmHead - 3].value) /* qualifiedId */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 4].offset, tmStack[tmHead].endoffset);
				break;
			case 143:  // annotation ::= '@' qualified_id
				lapg_gg.value = new TmaAnnotation(
						null /* arguments */,
						((String)tmStack[tmHead].value) /* qualifiedId */,
						null /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 144:  // annotation ::= '@' syntax_problem
				lapg_gg.value = new TmaAnnotation(
						null /* arguments */,
						null /* qualifiedId */,
						((TmaSyntaxProblem)tmStack[tmHead].value) /* syntaxProblem */,
						null /* input */, tmStack[tmHead - 1].offset, tmStack[tmHead].endoffset);
				break;
			case 145:  // symref_list ::= symref_list '|' symref
				((List<TmaSymref>)lapg_gg.value).add(((TmaSymref)tmStack[tmHead].value));
				break;
			case 146:  // symref_list ::= symref
				lapg_gg.value = new ArrayList();
				((List<TmaSymref>)lapg_gg.value).add(((TmaSymref)tmStack[tmHead].value));
				break;
			case 147:  // negative_la ::= '(?!' symref_list ')'
				lapg_gg.value = new TmaNegativeLa(
						((List<TmaSymref>)tmStack[tmHead - 1].value) /* unwantedSymbols */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 152:  // expression ::= Lnew name '(' map_entriesopt ')'
				lapg_gg.value = new TmaExpressionInstance(
						((TmaName)tmStack[tmHead - 3].value) /* name */,
						((List<TmaMapEntriesItem>)tmStack[tmHead - 1].value) /* mapEntries */,
						null /* input */, tmStack[tmHead - 4].offset, tmStack[tmHead].endoffset);
				break;
			case 153:  // expression_list1 ::= expression_list1 ',' expression
				((List<ITmaExpression>)lapg_gg.value).add(((ITmaExpression)tmStack[tmHead].value));
				break;
			case 154:  // expression_list1 ::= expression
				lapg_gg.value = new ArrayList();
				((List<ITmaExpression>)lapg_gg.value).add(((ITmaExpression)tmStack[tmHead].value));
				break;
			case 157:  // expression ::= '[' expression_list1_opt ']'
				lapg_gg.value = new TmaExpressionArray(
						((List<ITmaExpression>)tmStack[tmHead - 1].value) /* expressionList1 */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset);
				break;
			case 159:  // literal ::= scon
				lapg_gg.value = new TmaLiteral(
						((String)tmStack[tmHead].value) /* sval */,
						null /* ival */,
						false /* val */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 160:  // literal ::= icon
				lapg_gg.value = new TmaLiteral(
						null /* sval */,
						((Integer)tmStack[tmHead].value) /* ival */,
						false /* val */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 161:  // literal ::= Ltrue
				lapg_gg.value = new TmaLiteral(
						null /* sval */,
						null /* ival */,
						true /* val */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 162:  // literal ::= Lfalse
				lapg_gg.value = new TmaLiteral(
						null /* sval */,
						null /* ival */,
						false /* val */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 163:  // map_entries ::= ID map_separator expression
				lapg_gg.value = new ArrayList();
				((List<TmaMapEntriesItem>)lapg_gg.value).add(new TmaMapEntriesItem(
						((String)tmStack[tmHead - 2].value) /* ID */,
						((ITmaExpression)tmStack[tmHead].value) /* expression */,
						null /* input */, tmStack[tmHead - 2].offset, tmStack[tmHead].endoffset));
				break;
			case 164:  // map_entries ::= map_entries ',' ID map_separator expression
				((List<TmaMapEntriesItem>)lapg_gg.value).add(new TmaMapEntriesItem(
						((String)tmStack[tmHead - 2].value) /* ID */,
						((ITmaExpression)tmStack[tmHead].value) /* expression */,
						null /* input */, tmStack[tmHead - 4].offset, tmStack[tmHead].endoffset));
				break;
			case 168:  // name ::= qualified_id
				lapg_gg.value = new TmaName(
						((String)tmStack[tmHead].value) /* qualifiedId */,
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 170:  // qualified_id ::= qualified_id '.' ID
				 lapg_gg.value = ((String)tmStack[tmHead - 2].value) + "." + ((String)tmStack[tmHead].value); 
				break;
			case 171:  // command ::= code
				lapg_gg.value = new TmaCommand(
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
			case 172:  // syntax_problem ::= error
				lapg_gg.value = new TmaSyntaxProblem(
						null /* input */, tmStack[tmHead].offset, tmStack[tmHead].endoffset);
				break;
		}
	}

	/**
	 * disposes symbol dropped by error recovery mechanism
	 */
	protected void dispose(LapgSymbol value) {
	}

	/**
	 * cleans node removed from the stack
	 */
	protected void cleanup(LapgSymbol value) {
	}

	public TmaInput parseInput(TMLexer lexer) throws IOException, ParseException {
		return (TmaInput) parse(lexer, 0, 274);
	}

	public ITmaExpression parseExpression(TMLexer lexer) throws IOException, ParseException {
		return (ITmaExpression) parse(lexer, 1, 275);
	}
}