// generated by Textmapper; DO NOT EDIT

package test

import (
	"fmt"
)

var tmNonterminals = [...]string{
	"Declaration_list",
	"Test",
	"Declaration",
	"setof_not_((eoi | '.') | '}')",
	"setof_not_((eoi | '.') | '}')_optlist",
	"empty1",
	"QualifiedName",
	"Decl1",
	"Decl2",
}

func symbolName(sym int32) string {
	if sym < int32(NumTokens) {
		return Token(sym).String()
	}
	if i := int(sym) - int(NumTokens); i < len(tmNonterminals) {
		return tmNonterminals[i]
	}
	return fmt.Sprintf("nonterminal(%d)", sym)
}

var tmAction = []int32{
	-1, -1, -3, 11, -1, -1, 45, -1, -23, 1, 3, 4, -1, 40, 41, -1, 10, -1, -1, 0,
	12, -1, -1, 42, -1, 8, -1, -1, 9, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 14,
	26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 15, 44, -1, 6, -1, 7,
	43, 5, -1, -1, -2, -2,
}

var tmLalr = []int32{
	14, -1, 0, 13, 5, 13, 6, 13, 7, 13, 8, 13, 9, 13, 10, 13, 11, 13, -1, -2, 5,
	-1, 6, -1, 7, -1, 8, -1, 9, -1, 10, -1, 0, 2, -1, -2,
}

var tmGoto = []int32{
	0, 4, 6, 8, 10, 16, 34, 52, 70, 90, 108, 128, 142, 148, 154, 158, 162, 164,
	166, 168, 174, 176, 178, 180, 182, 184, 186, 194, 196, 212, 214, 216, 218,
	220, 238, 254,
}

var tmFromTo = []int8{
	62, 64, 63, 65, 21, 29, 21, 30, 21, 31, 15, 23, 21, 32, 56, 60, 0, 2, 7, 2,
	8, 2, 17, 2, 18, 2, 21, 33, 26, 2, 27, 2, 58, 2, 0, 3, 7, 3, 8, 3, 17, 3, 18,
	3, 21, 34, 26, 3, 27, 3, 58, 3, 0, 4, 7, 4, 8, 4, 17, 4, 18, 4, 21, 35, 26,
	4, 27, 4, 58, 4, 0, 5, 1, 5, 7, 5, 8, 5, 17, 5, 18, 5, 21, 36, 26, 5, 27, 5,
	58, 5, 0, 6, 7, 6, 8, 6, 17, 6, 18, 6, 21, 37, 26, 6, 27, 6, 58, 6, 0, 7, 4,
	13, 7, 7, 8, 7, 17, 7, 18, 7, 21, 38, 26, 7, 27, 7, 58, 7, 7, 16, 17, 25, 18,
	28, 21, 39, 26, 57, 27, 59, 58, 61, 4, 14, 5, 15, 21, 40, 21, 41, 22, 54, 24,
	55, 2, 12, 21, 42, 12, 20, 21, 43, 24, 56, 21, 44, 21, 45, 7, 17, 17, 26, 21,
	46, 21, 47, 21, 48, 21, 49, 21, 50, 21, 51, 21, 52, 0, 8, 7, 18, 17, 27, 26,
	58, 0, 62, 0, 9, 7, 9, 8, 19, 17, 9, 18, 19, 26, 9, 27, 19, 58, 19, 21, 53,
	13, 21, 14, 22, 15, 24, 0, 10, 1, 63, 7, 10, 8, 10, 17, 10, 18, 10, 26, 10,
	27, 10, 58, 10, 0, 11, 7, 11, 8, 11, 17, 11, 18, 11, 26, 11, 27, 11, 58, 11,
}

var tmRuleLen = []int8{
	2, 1, 1, 1, 1, 5, 4, 4, 3, 3, 2, 1, 3, 1, 4, 4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
	1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0, 1, 3, 4, 1,
}

var tmRuleSymbol = []int32{
	26, 26, 27, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 28, 29, 29, 29,
	29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29, 29,
	29, 30, 30, 31, 32, 32, 33, 34,
}

var tmRuleType = [...]NodeType{
	0,          // Declaration_list : Declaration_list Declaration
	0,          // Declaration_list : Declaration
	Test,       // Test : Declaration_list
	0,          // Declaration : Decl1
	0,          // Declaration : Decl2
	Block,      // Declaration : '{' '-' '-' Declaration_list '}'
	Block,      // Declaration : '{' '-' '-' '}'
	Block,      // Declaration : '{' '-' Declaration_list '}'
	Block,      // Declaration : '{' '-' '}'
	Block,      // Declaration : '{' Declaration_list '}'
	Block,      // Declaration : '{' '}'
	LastInt,    // Declaration : lastInt
	Int,        // Declaration : IntegerConstant '[' ']'
	Int,        // Declaration : IntegerConstant
	TestClause, // Declaration : 'test' '{' setof_not_((eoi | '.') | '}')_optlist '}'
	0,          // Declaration : 'test' '(' empty1 ')'
	0,          // setof_not_((eoi | '.') | '}') : invalid_token
	0,          // setof_not_((eoi | '.') | '}') : WhiteSpace
	0,          // setof_not_((eoi | '.') | '}') : SingleLineComment
	0,          // setof_not_((eoi | '.') | '}') : Identifier
	0,          // setof_not_((eoi | '.') | '}') : IntegerConstant
	0,          // setof_not_((eoi | '.') | '}') : lastInt
	0,          // setof_not_((eoi | '.') | '}') : 'test'
	0,          // setof_not_((eoi | '.') | '}') : 'decl1'
	0,          // setof_not_((eoi | '.') | '}') : 'decl2'
	0,          // setof_not_((eoi | '.') | '}') : '{'
	0,          // setof_not_((eoi | '.') | '}') : '('
	0,          // setof_not_((eoi | '.') | '}') : ')'
	0,          // setof_not_((eoi | '.') | '}') : '['
	0,          // setof_not_((eoi | '.') | '}') : ']'
	0,          // setof_not_((eoi | '.') | '}') : ','
	0,          // setof_not_((eoi | '.') | '}') : ':'
	0,          // setof_not_((eoi | '.') | '}') : '-'
	0,          // setof_not_((eoi | '.') | '}') : '->'
	0,          // setof_not_((eoi | '.') | '}') : SharpAtID
	0,          // setof_not_((eoi | '.') | '}') : 'Zfoo'
	0,          // setof_not_((eoi | '.') | '}') : backtrackingToken
	0,          // setof_not_((eoi | '.') | '}') : error
	0,          // setof_not_((eoi | '.') | '}') : MultiLineComment
	0,          // setof_not_((eoi | '.') | '}')_optlist : setof_not_((eoi | '.') | '}')_optlist setof_not_((eoi | '.') | '}')
	0,          // setof_not_((eoi | '.') | '}')_optlist :
	0,          // empty1 :
	0,          // QualifiedName : Identifier
	0,          // QualifiedName : QualifiedName '.' Identifier
	Decl1,      // Decl1 : 'decl1' '(' QualifiedName ')'
	Decl2,      // Decl2 : 'decl2'
}

// set(follow error) =
var afterErr = []int32{}
