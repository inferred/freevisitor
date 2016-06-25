package org.inferred.freevisitor.processor;

import org.inferred.internal.source.Excerpt;
import org.inferred.internal.source.QualifiedName;
import org.inferred.internal.source.SourceBuilder;

import javax.annotation.Generated;

class GeneratedVisitor extends Excerpt {

  private final Visitor visitor;

  GeneratedVisitor(Visitor visitor) {
    this.visitor = visitor;
  }

  @Override
  protected void addFields(FieldReceiver fields) {
    fields.add("visitor", visitor);
  }

  @Override
  public void addTo(SourceBuilder code) {
    code.addLine("/**")
        .addLine(" * Visitor interface for {@link %1$s}. Call {@link #visit(%1$s)} to perform",
            visitor.getVisitedType())
        .addLine(" * dynamic dispatch.")
        .addLine(" */")
        .addLine("@%s(\"%s\")", Generated.class, Processor.class.getName())
        .addLine("interface %s {", visitor.getGeneratedType().getSimpleName());
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  void visit(%s %s);", subtype, lowercased(subtype));
    }
    addVisitMethod(code);
    addInternalType(code);
    code.addLine("}");
  }

  private void addVisitMethod(SourceBuilder code) {
    String variableName = lowercased(visitor.getVisitedType());
    code.addLine("")
        .addLine("/**")
        .addLine(" * Dynamically dispatches to another visit method based on the type of")
        .addLine(" * {@code %s}.", variableName)
        .addLine(" */")
        .addLine("default void visit(%s %s) {", visitor.getVisitedType(), variableName)
        .addLine("  Internal.VISIT_METHOD.get(%1$s.getClass()).visit(%1$s, this);", variableName)
        .addLine("}");
  }

  private void addInternalType(SourceBuilder code) {
    code.addLine("class Internal {")
        .addLine("  private interface VisitMethod {")
        .addLine("    void visit(%s %s, %s visitor);",
            visitor.getVisitedType(),
            lowercased(visitor.getVisitedType()),
            visitor.getGeneratedType())
        .addLine("  }")
        .addLine("")
        .addLine("  private static final ClassValue<VisitMethod> VISIT_METHOD =")
        .addLine("      new ClassValue<VisitMethod>() {");
    addComputeValueMethod(code);
    code.addLine("      };")
        .addLine("}");
  }

  private void addComputeValueMethod(SourceBuilder code) {
    code.addLine("@Override")
        .addLine("protected VisitMethod computeValue(Class<?> type) {");
    String prefix = "";
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  %s if (type.isAssignableFrom(%s.class)) {", prefix, subtype)
          .addLine("    return (%1$s, visitor) -> visitor.visit((%2$s) %1$s);",
              lowercased(visitor.getVisitedType()), subtype);
      prefix = "  } else";
    }
    if (!prefix.isEmpty()) {
      code.addLine("  %s {", prefix);
    }
    code.addLine("    throw new IllegalArgumentException(\"Visit not implemented for \" + type);");
    if (!prefix.isEmpty()) {
      code.addLine("  }");
    }
    code.addLine("}");
  }

  static String lowercased(QualifiedName type) {
    return type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1);
  }

}