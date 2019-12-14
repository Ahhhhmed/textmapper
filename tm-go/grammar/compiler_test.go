package grammar_test

import (
	"io/ioutil"
	"path/filepath"
	"testing"

	"github.com/inspirer/textmapper/tm-go/grammar"
	"github.com/inspirer/textmapper/tm-go/status"
	"github.com/inspirer/textmapper/tm-parsers/parsertest"
	"github.com/inspirer/textmapper/tm-parsers/tm"
	"github.com/inspirer/textmapper/tm-parsers/tm/ast"
	"strings"
)

var testFiles = []string{
	"lexer.tmerr",
	"opts.tmerr",
	"opts_ok.tmerr",
	"parser.tmerr",
	"noinput.tmerr",
	"badinput.tmerr",
}

func TestErrors(t *testing.T) {
	for _, file := range testFiles {
		content, err := ioutil.ReadFile(filepath.Join("testdata", file))
		if err != nil {
			t.Errorf("cannot read %v: %v", file, err)
			continue
		}

		inp := string(content)
		pt := parsertest.New(t, "error", inp)
		tree, err := ast.Parse(file, pt.Source(), tm.StopOnFirstError)
		if err != nil {
			t.Errorf("parsing failed with %v\n%v", err, inp)
			continue
		}

		var want []string
		for _, line := range strings.Split(inp, "\n") {
			const prefix = "# err: "
			if strings.HasPrefix(line, prefix) {
				want = append(want, line[len(prefix):])
			}
		}

		_, err = grammar.Compile(ast.File{Node: tree.Root()})
		if err != nil {
			s := status.FromError(err)
			s.Sort()
			for _, e := range s {
				pt.Consume(t, e.Origin.Offset, e.Origin.EndOffset)
				if len(want) == 0 {
					t.Errorf("%v: unexpected error at line %v: %v", file, e.Origin.Line, e.Msg)
					continue
				}
				if want[0] != e.Msg {
					t.Errorf("%v: unexpected error at line %v: %v, want: %v", file, e.Origin.Line, e.Msg, want[0])
				}
				want = want[1:]
			}
		}
		if len(want) != 0 {
			t.Errorf("%v: not reported errors:\n%v", file, want)
		}
		pt.Done(t, nil)
	}
}
