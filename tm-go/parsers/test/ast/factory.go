// generated by Textmapper; DO NOT EDIT

package ast

import (
	"github.com/inspirer/textmapper/tm-go/parsers/test"
	"log"
)

func ToTestNode(node Node) TestNode {
	if node == nil {
		return nil
	}
	switch node.Type() {
	case test.Block:
		return &Block{node}
	case test.Decl1:
		return &Decl1{node}
	case test.Decl2:
		return &Decl2{node}
	case test.Test:
		return &Test{node}
	case test.MultiLineComment, test.SingleLineComment, test.InvalidToken, test.Identifier:
		return &Token{node}
	}
	log.Fatalf("unknown node type %v\n", node.Type())
	return nil
}
