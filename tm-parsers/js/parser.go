// generated by Textmapper; DO NOT EDIT

package js

import (
	"fmt"
)

// ErrorHandler is called every time a parser is unable to process some part of the input.
// This handler can return false to abort the parser.
type ErrorHandler func(err SyntaxError) bool

type SyntaxError struct {
	Line      int
	Offset    int
	Endoffset int
}

func (e SyntaxError) Error() string {
	return fmt.Sprintf("syntax error at line %v", e.Line)
}

func (p *Parser) Parse(lexer *Lexer) error {
	return p.parse(2, 4769, lexer)
}

func lookaheadNext(lexer *Lexer) int32 {
restart:
	tok := lexer.Next()
	switch tok {
	case MULTILINECOMMENT, SINGLELINECOMMENT, INVALID_TOKEN:
		goto restart
	}
	return int32(tok)
}

func (p *Parser) AtStartOfFunctionType() bool {
	return p.lookahead(0, 4766)
}

func (p *Parser) AtStartOfMappedType() bool {
	return p.lookahead(1, 4767)
}

func (p *Parser) lookahead(start, end int16) bool {
	var lexer Lexer = *p.lexer
	var alloc2, alloc3 [8]int
	lexer.Stack = alloc2[:0]
	lexer.Opened = alloc3[:0]

	var allocated [64]stackEntry
	state := start
	stack := append(allocated[:0], stackEntry{state: state})
	next := p.next.symbol

	for state != end {
		action := tmAction[state]
		if action < -2 {
			// Lookahead is needed.
			if next == noToken {
				next = lookaheadNext(&lexer)
			}
			action = lalr(action, next)
		}

		if action >= 0 {
			// Reduce.
			rule := action
			ln := int(tmRuleLen[rule])

			var entry stackEntry
			entry.sym.symbol = tmRuleSymbol[rule]
			stack = stack[:len(stack)-ln]
			state = gotoState(stack[len(stack)-1].state, entry.sym.symbol)
			entry.state = state
			stack = append(stack, entry)

		} else if action == -1 {
			// Shift.
			if next == noToken {
				next = lookaheadNext(&lexer)
			}
			state = gotoState(state, next)
			stack = append(stack, stackEntry{
				sym:   symbol{symbol: next},
				state: state,
			})
			if state != -1 && next != eoiToken {
				next = noToken
			}
		}

		if action == -2 || state == -1 {
			break
		}
	}

	return state == end
}

func lalr(action, next int32) int32 {
	a := -action - 3
	for ; tmLalr[a] >= 0; a += 2 {
		if tmLalr[a] == next {
			break
		}
	}
	return tmLalr[a+1]
}

func gotoState(state int16, symbol int32) int16 {
	min := tmGoto[symbol]
	max := tmGoto[symbol+1]

	if max-min < 32 {
		for i := min; i < max; i += 2 {
			if tmFromTo[i] == state {
				return tmFromTo[i+1]
			}
		}
	} else {
		for min < max {
			e := (min + max) >> 1 &^ int32(1)
			i := tmFromTo[e]
			if i == state {
				return tmFromTo[e+1]
			} else if i < state {
				min = e + 2
			} else {
				max = e
			}
		}
	}
	return -1
}

func (p *Parser) applyRule(rule int32, lhs *stackEntry, rhs []stackEntry) {
	switch rule {
	case 2585: // IterationStatement : 'for' '(' 'async' 'of' AssignmentExpression_In ')' Statement
		p.listener(IdentifierReference, rhs[2].sym.offset, rhs[2].sym.endoffset)
	case 2599: // IterationStatement_Await : 'for' '(' 'async' 'of' AssignmentExpression_Await_In ')' Statement_Await
		p.listener(IdentifierReference, rhs[2].sym.offset, rhs[2].sym.endoffset)
	case 2613: // IterationStatement_Yield : 'for' '(' 'async' 'of' AssignmentExpression_In_Yield ')' Statement_Yield
		p.listener(IdentifierReference, rhs[2].sym.offset, rhs[2].sym.endoffset)
	case 3303:
		if p.AtStartOfMappedType() {
			lhs.sym.symbol = 843 /* lookahead_StartOfMappedType */
		} else {
			lhs.sym.symbol = 835 /* lookahead_notStartOfMappedType */
		}
		return
	case 3304:
		if p.AtStartOfFunctionType() {
			lhs.sym.symbol = 849 /* lookahead_StartOfFunctionType */
		} else {
			lhs.sym.symbol = 828 /* lookahead_notStartOfFunctionType */
		}
		return
	}
	nt := ruleNodeType[rule]
	if nt == 0 {
		return
	}
	p.listener(nt, lhs.sym.offset, lhs.sym.endoffset)
}

const errSymbol = 2

func canRecoverOn(symbol int32) bool {
	for _, v := range afterErr {
		if v == symbol {
			return true
		}
	}
	return false
}

// willShift checks if "symbol" is going to be shifted in the given state.
// This function does not support empty productions and returns false if they occur before "symbol".
func (p *Parser) willShift(stackPos int, state int16, symbol int32) bool {
	if state == -1 {
		return false
	}

	for state != p.endState {
		action := tmAction[state]
		if action < -2 {
			action = lalr(action, symbol)
		}

		if action >= 0 {
			// Reduce.
			rule := action
			ln := int(tmRuleLen[rule])
			if ln == 0 {
				// we do not support empty productions
				return false
			}
			stackPos -= ln - 1
			state = gotoState(p.stack[stackPos-1].state, tmRuleSymbol[rule])
		} else {
			return action == -1 && gotoState(state, symbol) >= 0
		}
	}
	return symbol == eoiToken
}

func (p *Parser) reportIgnoredTokens() {
	for _, c := range p.ignoredTokens {
		var t NodeType
		switch Token(c.symbol) {
		case MULTILINECOMMENT:
			t = MultiLineComment
		case SINGLELINECOMMENT:
			t = SingleLineComment
		case INVALID_TOKEN:
			t = InvalidToken
		default:
			continue
		}
		p.listener(t, c.offset, c.endoffset)
	}
	p.ignoredTokens = p.ignoredTokens[:0]
}
