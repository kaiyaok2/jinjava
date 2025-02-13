package com.hubspot.jinjava.el.ext.eager;

import com.hubspot.jinjava.el.ext.DeferredParsingException;
import de.odysseus.el.tree.Bindings;
import de.odysseus.el.tree.impl.ast.AstNode;
import de.odysseus.el.tree.impl.ast.AstUnary;
import javax.el.ELContext;

public class EagerAstUnary extends AstUnary implements EvalResultHolder {
  protected Object evalResult;
  protected boolean hasEvalResult;
  protected final EvalResultHolder child;
  protected final Operator operator;

  public EagerAstUnary(AstNode child, Operator operator) {
    this(EagerAstNodeDecorator.getAsEvalResultHolder(child), operator);
  }

  private EagerAstUnary(EvalResultHolder child, Operator operator) {
    super((AstNode) child, operator);
    this.child = child;
    this.operator = operator;
  }

  @Override
  public Object eval(Bindings bindings, ELContext context) {
    return EvalResultHolder.super.eval(
      () -> super.eval(bindings, context),
      bindings,
      context
    );
  }

  @Override
  public String getPartiallyResolved(
    Bindings bindings,
    ELContext context,
    DeferredParsingException deferredParsingException,
    boolean preserveIdentifier
  ) {
    return (
      operator.toString() +
      EvalResultHolder.reconstructNode(
        bindings,
        context,
        child,
        deferredParsingException,
        false
      )
    );
  }

  @Override
  public Object getEvalResult() {
    return evalResult;
  }

  @Override
  public void setEvalResult(Object evalResult) {
    this.evalResult = evalResult;
    hasEvalResult = true;
  }

  @Override
  public void clearEvalResult() {
    evalResult = null;
    hasEvalResult = false;
    child.clearEvalResult();
  }

  @Override
  public boolean hasEvalResult() {
    return hasEvalResult;
  }
}
