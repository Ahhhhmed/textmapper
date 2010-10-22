/*   defaults.cpp
 *
 *   Lapg (Lexical Analyzer and Parser Generator)
 *   Copyright (C) 2002-07  Evgeny Gryaznov (inspirer@inbox.ru)
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

const char *default_cpp =
	"#   Automatically generated grammar\n"
	"\n"
	".lang        \"c++\"\n"
	".getsym      \"chr = *l++;if( l == end ) fillb()\"\n"
	".positioning \"full\"\n"
	"\n"
	"# Vocabulary\n"
	"\n"
	"Lid:        /[a-zA-Z_][a-zA-Z_0-9]*/\n"
	"_skip:      /\\/\\/.*/\n"
	"_skip:      /[\\t\\r\\n ]+/    \\ continue;\n"
	"\n"
	"# Attributes\n"
	"\n"
	"[]\n"
	"\n"
	"# Grammar\n"
	"\n"
	"input ::= Lid ;\n"
	"\n"
	"%%\n"
	"\n"
	"#define DEBUG_syntax\n"
	"\n"
	"#include <stdlib.h>\n"
	"#include <stdio.h>\n"
	"#include <stdarg.h>\n"
	"\n"
	"class parser {\n"
	"private:\n"
	"\tchar b[1025], *l, *end;\n"
	"\tvoid error( char *r, ... );\n"
	"\n"
	"public:\n"
	"\tint parse();\n"
	"\tvoid fillb();\n"
	"};\n"
	"\n"
	"void parser::error( char *r, ... )\n"
	"{\n"
	"\tva_list arglist;\n"
	"\tva_start( arglist, r );\n"
	"\tvfprintf( stderr, r, arglist );\n"
	"}\n"
	"\n"
	"\n"
	"void parser::fillb()\n"
	"{\n"
	"\tint size = fread( b, 1, 1024, stdin );\n"
	"\tb[size] = 0; end = b + size; l = b;\n"
	"}\n"
	"\n"
	"\n"
	"int main( int argc, char *argv[] )\n"
	"{\n"
	"\tint  i;\n"
	"\tchar *input = \"-\";\n"
	"\tparser p;\n"
	"\t\n"
	"\tfor( i = 1; i < argc; i++ ) {\n"
	"\t\tif( argv[i][0]!='-' || argv[i][1]==0 )\n"
	"\t\t\tinput = argv[i];\n"
	"\t}\n"
	"\n"
	"\tif( input[0] != '-' || input[1] != 0 )\n"
	"\t\tif( !freopen( input, \"r\", stdin ) ) {\n"
	"\t\t\tperror( input );\n"
	"\t\t\treturn 1;\n"
	"\t\t}\n"
	"\n"
	"\tp.fillb();\n"
	"\tp.parse();\n"
	"\treturn 0;\n"
	"}\n"
;

const char *default_cs =
	"#   Automatically generated grammar\n"
	"\n"
	".lang        \"cs\" \n"
	".getsym      \"chr = buff[l++];if( l == end ) fillb()\"\n"
	".positioning \"full\"\n"
	"\n"
	"# Vocabulary\n"
	"\n"
	"Lid:        /[a-zA-Z_][a-zA-Z_0-9]*/\n"
	"_skip:      /\\/\\/.*/\n"
	"_skip:      /[\\t\\r\\n ]+/    \\ continue;\n"
	"\n"
	"# Attributes\n"
	"\n"
	"[]\n"
	"\n"
	"# Grammar\n"
	"\n"
	"input ::= Lid ;\n"
	"\n"
	"%%\n"
	"\n"
	"#define DEBUG_syntax\n"
	"\n"
	"using System.IO;\n"
	"\n"
	"%%\n"
	"\n"
	"byte[] buff = new byte[1025];\n"
	"int l, end;\n"
	"BinaryReader r;\n"
	"\n"
	"void fillb() {\n"
	"\tl = 0;\n"
	"\tend = r.Read( buff, 0, 1024 );\n"
	"\tbuff[end] = 0;\n"
	"}\n"
	"\n"
	"void error( string s ) {\n"
	"\tSystem.Console.WriteLine(s);\n"
	"}\n"
	"\n"
	"public static void Main(string[] args) {\n"
	"\tparser p = new parser();\n"
	"\t\n"
	"\tif( args.Length > 0 ) \n"
	"\t\tp.r = new BinaryReader( new FileStream(args[0],FileMode.Open) );\n"
	"\telse \n"
	"\t\tp.r = new BinaryReader( System.Console.OpenStandardInput() );\n"
	"\n"
	"\tp.fillb();\n"
	"\tp.parse();\n"
	"}\n"
;

const char *default_js =
	"#   Automatically generated grammar\n"
	"\n"
	".lang        \"js\" \n"
	".getsym      \"chr = this.buff.charAt(this.l++)\"\n"
	".positioning \"full\"\n"
	"\n"
	"# Vocabulary\n"
	"\n"
	"Lid:        /[a-zA-Z_][a-zA-Z_0-9]*/\t{ @ = token; }\n"
	"_skip:      /\\/\\/.*/\n"
	"_skip:      /[\\t\\r\\n ]+/    { continue; }\n"
	"\n"
	"# Attributes\n"
	"\n"
	"[]\n"
	"\n"
	"# Grammar\n"
	"\n"
	"input ::= Lid ;\n"
	"\n"
	"%%\n"
	"\n"
	"dump = alert;\n"
	"\n"
	"function error(s) {\n"
	"\tdump(s);\n"
	"}\n"
	"\n"
	"function parse(string) {\n"
	"\tvar p = new parser();\n"
	"\tp.buff = string;\n"
	"\tp.l = 0;\n"
	"//\tp.DEBUG_syntax = 1;\n"
	"\tp.parse();\n"
	"}\n"
;

const char *default_java =
	"#   Automatically generated grammar\n"
	"\n"
	".lang        \"java\" \n"
	".getsym      \"chr = buff[l++];if( l == end ) fillb()\"\n"
	".positioning \"full\"\n"
	".class       \"Parser\"\n"
	".namespace\t \"mypackage\"\n"
	".breaks\t\t \"on\"\n"
	"\n"
	"# Vocabulary\n"
	"\n"
	"Lid:        /[a-zA-Z_][a-zA-Z_0-9]*/\t{ @ = new String(token,0,lapg_size);break; }\n"
	"_skip:      /\\/\\/.*/\n"
	"_skip:      /[\\t\\r\\n ]+/    { continue; }\n"
	"'(':\t\t/\\(/\n"
	"')':\t\t/\\)/\n"
	"'[':\t\t/\\[/\n"
	"']':\t\t/\\]/\n"
	"\n"
	"# Attributes\n"
	"\n"
	"[]\n"
	"\n"
	"# Grammar\n"
	"\n"
	"input ::= \n"
	"\tparentheses { System.out.println(\"[good]\"); };\n"
	"\n"
	"parentheses ::= \n"
	"\tparentheses parenthesis | parenthesis ;\n"
	"\n"
	"parenthesis ::= \n"
	"\t  '(' Lid ')'\t\t\t\t{ System.out.println( \"in (): \" + $1 ); }\n"
	"\t| '(' parentheses ')'\n"
	"\t| '[' Lid ']'\t\t\t\t{ System.out.println( \"in []: \" + $1 ); }\n"
	"\t| '[' parentheses ']' ;\n"
	"\n"
	"%%\n"
	"import java.io.FileInputStream;\n"
	"import java.io.FileNotFoundException;\n"
	"import java.io.IOException;\n"
	"import java.io.InputStream;\n"
	"import java.text.MessageFormat;\n"
	"%%\n"
	"\n"
	"private static final boolean DEBUG_SYNTAX = true;\n"
	"byte[] buff = new byte[1025];\n"
	"int l, end;\n"
	"InputStream input;\n"
	"\n"
	"void fillb() {\n"
	"\tl = 0;\n"
	"\ttry {\n"
	"\t\tend = input.read( buff, 0, 1024 );\n"
	"\t\tif( end == -1 )\n"
	"\t\t\tend = 0;\n"
	"\t} catch( IOException ex ) {\n"
	"\t\tend = 0;\n"
	"\t}\n"
	"\tbuff[end] = 0;\n"
	"}\n"
	"\n"
	"void error( String s ) {\n"
	"\tSystem.err.println(s);\n"
	"}\n"
	"\n"
	"public static void main(String[] args) throws FileNotFoundException {\n"
	"\tParser p = new Parser();\n"
	"\t\n"
	"\tif( args.length > 0 ) \n"
	"\t\tp.input = new FileInputStream( args[0] );\n"
	"\telse \n"
	"\t\tp.input = System.in;\n"
	"\n"
	"\tp.fillb();\n"
	"\tp.parse();\n"
	"}\n"
;

const char *default_c =
	"#   Automatically generated grammar\n"
	"\n"
	".lang        \"c\"\n"
	".getsym      \"chr = *l++;if( l == end ) fillb()\"\n"
	".positioning \"full\"\n"
	"\n"
	"# Vocabulary\n"
	"\n"
	"Lid:        /[a-zA-Z_][a-zA-Z_0-9]*/\n"
	"_skip:      /\\/\\/.*/\n"
	"_skip:      /[\\t\\r\\n ]+/    \\ continue;\n"
	"\n"
	"# Attributes\n"
	"\n"
	"[]\n"
	"\n"
	"# Grammar\n"
	"\n"
	"input ::= Lid ;\n"
	"\n"
	"%%\n"
	"\n"
	"#define DEBUG_syntax\n"
	"\n"
	"#include <stdlib.h>\n"
	"#include <stdio.h>\n"
	"#include <stdarg.h>\n"
	"#include <string.h>\n"
	"\n"
	"\n"
	"static char b[1025], *l, *end;\n"
	"void error( char *r, ... );\n"
	"\n"
	"int parse( void );\n"
	"void fillb( void );\n"
	"\n"
	"void error( char *r, ... )\n"
	"{\n"
	"\tva_list arglist;\n"
	"\tva_start( arglist, r );\n"
	"\tvfprintf( stderr, r, arglist );\n"
	"}\n"
	"\n"
	"\n"
	"void fillb( void )\n"
	"{\n"
	"\tint size = fread( b, 1, 1024, stdin );\n"
	"\tb[size] = 0; end = b + size; l = b;\n"
	"}\n"
	"\n"
	"\n"
	"int main( int argc, char *argv[] )\n"
	"{\n"
	"\tint  i;\n"
	"\tchar *input = \"-\";\n"
	"\t\n"
	"\tfor( i = 1; i < argc; i++ ) {\n"
	"\t\tif( argv[i][0]!='-' || argv[i][1]==0 )\n"
	"\t\t\tinput = argv[i];\n"
	"\t}\n"
	"\n"
	"\tif( input[0] != '-' || input[1] != 0 )\n"
	"\t\tif( !freopen( input, \"r\", stdin ) ) {\n"
	"\t\t\tperror( input );\n"
	"\t\t\treturn 1;\n"
	"\t\t}\n"
	"\n"
	"\tfillb();\n"
	"\tparse();\n"
	"\treturn 0;\n"
	"}\n"
;

