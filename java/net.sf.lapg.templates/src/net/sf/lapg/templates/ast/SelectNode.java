package net.sf.lapg.templates.ast;

import net.sf.lapg.templates.api.EvaluationContext;
import net.sf.lapg.templates.api.EvaluationException;
import net.sf.lapg.templates.api.ITemplatesFacade;

public class SelectNode extends ExpressionNode {

	private ExpressionNode objectExpr;
	private String identifier;

	public SelectNode(ExpressionNode objectExpr, String identifier, String input, int line) {
		super(input, line);
		this.objectExpr = objectExpr;
		this.identifier = identifier;
	}

	@Override
	public Object evaluate(EvaluationContext context, ITemplatesFacade env) throws EvaluationException {
		Object object;
		if( objectExpr != null ) {
			object = env.evaluate(objectExpr, context, false);
		} else {
			Object value = context.getVariable(identifier);
			if( value != null ) {
				return value;
			}

			object = context.getThisObject();
		}

		return env.getProperty(object, identifier);
	}

	@Override
	public void toString(StringBuffer sb) {
		if( objectExpr != null ) {
			objectExpr.toString(sb);
			sb.append(".");
		}
		sb.append(identifier);
	}
}
