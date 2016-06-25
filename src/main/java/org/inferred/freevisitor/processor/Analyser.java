package org.inferred.freevisitor.processor;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.util.ElementFilter.typesIn;
import static org.inferred.internal.source.ModelUtils.maybeType;

import com.google.common.base.Joiner;

import org.inferred.freevisitor.processor.Visitor.Builder;
import org.inferred.internal.source.CannotGenerateCodeException;
import org.inferred.internal.source.QualifiedName;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

class Analyser {

  private final Messager messager;
  private final Types types;

  Analyser(Messager messager, Types types) {
    this.messager = messager;
    this.types = types;
  }

  public Visitor analyseVisitorType(TypeElement visitorType, Set<? extends Element> allRootElements)
      throws CannotGenerateCodeException {
    TypeElement visitedType = visitedType(visitorType);
    QualifiedName generatedType = generatedVisitorType(QualifiedName.of(visitedType));
    Visitor metadata = new Builder()
        .setVisitorType(QualifiedName.of(visitorType))
        .setVisitedType(QualifiedName.of(visitedType))
        .setGeneratedType(generatedType)
        .addAllVisitedSubtypes(visitedSubtypes(visitedType, allRootElements))
        .addNestedClasses(generatedType.nestedType("Internal"))
        .build();
    return metadata;
  }

  private List<QualifiedName> visitedSubtypes(
      TypeElement visitedType, Set<? extends Element> allRootElements) {
    return typesIn(allRootElements).stream()
        .filter(root -> {
            boolean isConcrete = !root.getModifiers().contains(Modifier.ABSTRACT);
            boolean isSubtype = types.isSubtype(root.asType(), visitedType.asType());
            return isConcrete && isSubtype;
        })
        .map(QualifiedName::of)
        .sorted(comparing(QualifiedName::getSimpleName))
        .collect(toList());
  }

  private TypeElement visitedType(TypeElement visitorType)
      throws CannotGenerateCodeException {
    if (visitorType.getNestingKind() != NestingKind.MEMBER) {
      String nestingKind = visitorType.getNestingKind().toString().toLowerCase().replace('_',  ' ');
      messager.printMessage(
          Kind.ERROR, "@FreeVisitor not supported on " + nestingKind + " types", visitorType);
      throw new CannotGenerateCodeException();
    }
    return maybeType(visitorType.getEnclosingElement()).get();
  }

  static QualifiedName generatedVisitorType(QualifiedName visitedType) {
    return QualifiedName.of(
        visitedType.getPackage(),
        Joiner.on('_').join(visitedType.getSimpleNames()) + "_Visitor");
  }
}
