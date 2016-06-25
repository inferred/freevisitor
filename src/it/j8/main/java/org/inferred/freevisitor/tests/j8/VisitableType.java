package org.inferred.freevisitor.tests.j8;

import org.inferred.freevisitor.FreeVisitor;

public abstract class VisitableType {
  VisitableType() {} // Subclasses must be in this package

  @FreeVisitor interface Visitor extends VisitableType_Visitor {}
}
