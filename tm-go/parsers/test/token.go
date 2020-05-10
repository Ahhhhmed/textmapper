// generated by Textmapper; DO NOT EDIT

package test

import (
	"fmt"
)

// Token is an enum of all terminal symbols of the test language.
type Token int

// Token values.
const (
	UNAVAILABLE Token = iota - 1
	EOI
	INVALID_TOKEN
	WHITESPACE
	SINGLELINECOMMENT
	IDENTIFIER
	INTEGERCONSTANT
	LASTINT
	TEST    // test
	DECL1   // decl1
	DECL2   // decl2
	LBRACE  // {
	RBRACE  // }
	LPAREN  // (
	RPAREN  // )
	LBRACK  // [
	RBRACK  // ]
	DOT     // .
	COMMA   // ,
	COLON   // :
	MINUS   // -
	MINUSGT // ->
	BACKTRACKINGTOKEN
	ERROR
	MULTILINECOMMENT

	NumTokens
)

var tokenStr = [...]string{
	"EOI",
	"INVALID_TOKEN",
	"WHITESPACE",
	"SINGLELINECOMMENT",
	"IDENTIFIER",
	"INTEGERCONSTANT",
	"LASTINT",
	"test",
	"decl1",
	"decl2",
	"{",
	"}",
	"(",
	")",
	"[",
	"]",
	".",
	",",
	":",
	"-",
	"->",
	"BACKTRACKINGTOKEN",
	"ERROR",
	"MULTILINECOMMENT",
}

func (tok Token) String() string {
	if tok >= 0 && int(tok) < len(tokenStr) {
		return tokenStr[tok]
	}
	return fmt.Sprintf("token(%d)", tok)
}
