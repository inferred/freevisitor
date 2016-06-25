package org.inferred.freevisitor.processor;

import org.inferred.freebuilder.FreeBuilder;
import org.inferred.internal.source.QualifiedName;

import java.util.List;
import java.util.Set;

@FreeBuilder
public interface Visitor {
  QualifiedName getVisitorType();
  QualifiedName getVisitedType();
  QualifiedName getGeneratedType();
  List<QualifiedName> getVisitedSubtypes();
  Set<QualifiedName> getNestedClasses();

  class Builder extends Visitor_Builder {}

}
