package org.inferred.freevisitor.processor;

import static com.google.common.collect.Iterables.skip;

import org.inferred.internal.source.Excerpt;
import org.inferred.internal.source.QualifiedName;
import org.inferred.internal.source.SourceBuilder;
import org.inferred.internal.source.SourceStringBuilder;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

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
        .addLine(" *")
        .addLine(" * @see %s.Returning<T>", visitor.getVisitorType())
        .addLine(" * @see %s.Builder", visitor.getVisitorType())
        .addLine(" * @see %s.Builder.Returning<T>", visitor.getVisitorType())
        .addLine(" * @see %s.Builders#switching()", visitor.getVisitorType())
        .addLine(" */")
        .addLine("@%s(\"%s\")", Generated.class, Processor.class.getName())
        .addLine("interface %s {", visitor.getGeneratedType().getSimpleName());
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  void visit(%s %s);", subtype, lowercased(subtype));
    }
    addVisitMethod(code);
    addReturningType(code);
    addBuilderType(code);
    addBuildersType(code);
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
        .addLine("  Builders.VISIT_METHOD.get(%1$s.getClass()).visit(%1$s, this);", variableName)
        .addLine("}");
  }

  private void addReturningType(SourceBuilder code) {
    code.addLine("")
        .addLine("/**")
        .addLine(" * Visitor interface for {@link %1$s}. Call {@link #visit(%1$s)} to perform",
            visitor.getVisitedType())
        .addLine(" * dynamic dispatch.")
        .addLine(" *")
        .addLine(" * @param <T> type returned from visit methods")
        .addLine(" */")
        .addLine("interface Returning<T> {");
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  T visit(%s %s);", subtype, lowercased(subtype));
    }
    String variableName = lowercased(visitor.getVisitedType());
    code.addLine("")
        .addLine("  /**")
        .addLine("   * Dynamically dispatches to another visit method based on the type of")
        .addLine("   * {@code %s}.", variableName)
        .addLine("   */")
        .addLine("  default T visit(%s %s) {", visitor.getVisitedType(), variableName)
        .addLine("    Builders.ReturningAdapter<T> adapter =")
        .addLine("        new Builders.ReturningAdapter<T>(this);")
        .addLine("    adapter.visit(%s);", variableName)
        .addLine("    return adapter.result;")
        .addLine("  }");
    code.addLine("}");
  }

  private void addBuilderType(SourceBuilder code) {
    code.addLine("")
        .addLine("/**")
        .addLine(" * Builder of {@link %s} instances.", visitor.getVisitorType())
        .addLine(" */")
        .addLine("public class Builder {");
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  private %s<? super %s> %sVisitor;",
          Consumer.class, subtype, lowercased(subtype));
    }
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("")
          .addLine("/**")
          .addLine(" * Sets the visitor for {@link %s} instances.", subtype)
          .addLine(" *")
          .addLine(" * @return this builder")
          .addLine(" */")
          .addLine("  public Builder on%s(%s<? super %s> visitor) {",
              subtype.getSimpleName(), Consumer.class, subtype)
          .addLine("    %sVisitor = visitor;", lowercased(subtype))
          .addLine("    return this;")
          .addLine("  }");
    }
    code.addLine("")
        .addLine("/**")
        .addLine(" * Returns a new {@link %s}.", visitor.getVisitorType())
        .addLine(" *")
        .addLine(" * @throws IllegalStateException if any types have not had visitors specified")
        .addLine(" */")
        .addLine("  public %s build() {", visitor.getVisitorType())
        .addLine("    return new Builders.BuiltVisitor(this);")
        .addLine("  }")
        .addLine("")
        .addLine("/**")
        .addLine(" * Returns a new {@link %s}. Any types that have not had a visitor specified",
            visitor.getVisitorType())
        .addLine(" * will be passed to {@code fallback}.")
        .addLine(" */")
        .addLine("  public %s otherwise(%s<? super %s> fallback) {",
            visitor.getVisitorType(), Consumer.class, visitor.getVisitedType())
        .addLine("    return new Builders.BuiltVisitor(this, fallback);")
        .addLine("  }");
    addReturningBuilderType(code);
    code.addLine("}");
  }

  private void addReturningBuilderType(SourceBuilder code) {
    code.addLine("")
        .addLine("/**")
        .addLine(" * Builder of {@link %s.Returning} instances.", visitor.getVisitorType())
        .addLine(" *")
        .addLine(" * @param <T> type returned from visit methods")
        .addLine(" */")
        .addLine("public static class Returning<T> {");
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  private %s<? super %s, ? extends T> %sVisitor;",
          Function.class, subtype, lowercased(subtype));
    }
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("")
          .addLine("/**")
          .addLine(" * Sets the visitor for {@link %s} instances.", subtype)
          .addLine(" *")
          .addLine(" * @return this builder")
          .addLine(" */")
          .addLine("  public Builder.Returning<T> on%s(%s<? super %s, ? extends T> visitor) {",
              subtype.getSimpleName(), Function.class, subtype)
          .addLine("    %sVisitor = visitor;", lowercased(subtype))
          .addLine("    return this;")
          .addLine("  }");
    }
    code.addLine("")
        .addLine("/**")
        .addLine(" * Returns a new {@link %s}.", visitor.getVisitorType())
        .addLine(" *")
        .addLine(" * @throws IllegalStateException if any types have not had visitors specified")
        .addLine(" */")
        .addLine("  public %s.Returning<T> build() {", visitor.getVisitorType())
        .addLine("    return new Builders.BuiltVisitorReturning<>(this);")
        .addLine("  }")
        .addLine("")
        .addLine("/**")
        .addLine(" * Returns a new {@link %s}. Any types that have not had a visitor specified",
            visitor.getVisitorType())
        .addLine(" * will be passed to {@code fallback}.")
        .addLine(" */")
        .addLine("  public %s.Returning<T> otherwise(%s<? super %s, ? extends T> fallback) {",
            visitor.getVisitorType(), Function.class, visitor.getVisitedType())
        .addLine("    return new Builders.BuiltVisitorReturning<>(this, fallback);")
        .addLine("  }")
        .addLine("}");
  }

  private void addBuildersType(SourceBuilder code) {
    code.addLine("public final class Builders {");
    if (!visitor.getVisitedSubtypes().isEmpty()) {
      addSwitchingBuilderType(code);
      addStrictBuilderType(code);
      addSwitchingMethod(code);
      addBuilderMethod(code);
    }
    addVisitMethodConstant(code);
    addReturningAdapterType(code);
    if (!visitor.getVisitedSubtypes().isEmpty()) {
      addSwitchingBuilderImplType(code);
      addSwitchingVisitorType(code);
      addSwitchingReturningVisitorType(code);
    }
    addBuiltVisitorType(code);
    addBuiltReturningVisitorType(code);
    code.addLine("  private Builders() {}")
        .addLine("}");
  }

  private void addSwitchingBuilderType(SourceBuilder code) {
    QualifiedName firstType = visitor.getVisitedSubtypes().get(0);
    code.addLine("")
        .addLine("/**")
        .addLine(" * Transient builder type.")
        .addLine(" *")
        .addLine(" * @see %s.Builders#switching", visitor.getVisitorType())
        .addLine(" */")
        .addLine("public interface SwitchingBuilder {")
        .addLine("  %s on(%s<? super %s> visitor);",
            strictBuilderType(code), Consumer.class, firstType)
        .addLine("  <T> %s on(%s<? super %s, ? extends T> visitor);",
            strictReturningBuilderType(code), Function.class, firstType)
        .addLine("}");
  }

  private String strictBuilderType(SourceBuilder code) {
    StringBuilder closing = new StringBuilder();
    SourceStringBuilder strictBuilderType = code.subBuilder();
    for (QualifiedName subtype : skip(visitor.getVisitedSubtypes(), 1)) {
      strictBuilderType.add("StrictBuilder<%s<? super %s>, ", Consumer.class, subtype);
      closing.append(">");
    }
    strictBuilderType.add("%s%s", visitor.getVisitorType(), closing.toString());
    return strictBuilderType.toString();
  }

  private String strictReturningBuilderType(SourceBuilder code) {
    StringBuilder closing = new StringBuilder();
    SourceStringBuilder strictBuilderType = code.subBuilder();
    for (QualifiedName subtype : skip(visitor.getVisitedSubtypes(), 1)) {
      strictBuilderType.add("StrictBuilder<%s<? super %s, ? extends T>, ", Function.class, subtype);
      closing.append(">");
    }
    strictBuilderType.add("%s.Returning<T>%s", visitor.getVisitorType(), closing.toString());
    return strictBuilderType.toString();
  }

  private void addStrictBuilderType(SourceBuilder code) {
    code.addLine("")
        .addLine("/**")
        .addLine(" * Transient builder type.")
        .addLine(" *")
        .addLine(" * @see %s.Builders#switching", visitor.getVisitorType())
        .addLine(" */")
        .addLine("public interface StrictBuilder<T, R> {")
        .addLine("  R on(T visitor);")
        .addLine("}");
  }

  private void addSwitchingMethod(SourceBuilder code) {
    code.addLine("")
        .addLine("/**")
        .addLine(" * Fluently builds an instance of {@link %s}", visitor.getVisitorType())
        .addLine(" * or {@link %s.Returning}.", visitor.getVisitorType())
        .addLine(" *")
        .add(" * <pre> %1$s visitor = %1$s.Builders.switching()", visitor.getVisitorType());
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.add("%n *     .on((%s %s) -> System.out.println(\"%s instance\"))",
              subtype, lowercased(subtype), subtype.getSimpleName());
    }
    code.add(";%n")
        .add(" * %1$s.Returning&lt;String&gt; visitor = %1$s.Builders.switching()",
            visitor.getVisitorType());
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.add("%n *     .on((%s %s) -> \"%s instance\")",
              subtype, lowercased(subtype), subtype.getSimpleName());
    }
    code.add(";</pre>%n")
        .addLine(" *")
        .addLine(" * <p>Type visitors must be provided in fixed order, and no type may be")
        .addLine(" * missed, or the compiler will issue an error. For a more flexible")
        .addLine(" * implementation of the builder pattern, use {@link %s.Builder} or",
            visitor.getVisitorType())
        .addLine(" * {@link %s.Builder.Returning}.",
            visitor.getVisitorType())
        .addLine(" */")
        .addLine("public static SwitchingBuilder switching() {")
        .addLine("  return SwitchingBuilderImpl.INSTANCE;")
        .addLine("}");
  }

  private static void addBuilderMethod(SourceBuilder code) {
    code.addLine("public static Builder builder() {")
        .addLine("  return new Builder();")
        .addLine("}");
  }

  private void addVisitMethodConstant(SourceBuilder code) {
    code.addLine("  private interface VisitMethod {")
        .addLine("    void visit(%s %s, %s visitor);",
            visitor.getVisitedType(),
            lowercased(visitor.getVisitedType()),
            visitor.getGeneratedType())
        .addLine("  }")
        .addLine("")
        .addLine("  private static final ClassValue<VisitMethod> VISIT_METHOD =")
        .addLine("      new ClassValue<VisitMethod>() {");
    addComputeValueMethod(code);
    code.addLine("      };");
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

  private void addReturningAdapterType(SourceBuilder code) {
    code.addLine("")
        .addLine("private static class ReturningAdapter<T> implements %s {",
            visitor.getGeneratedType())
        .addLine("")
        .addLine("  private final Returning<T> delegate;")
        .addLine("  private T result;")
        .addLine("")
        .addLine("  ReturningAdapter(Returning<T> delegate) {")
        .addLine("    this.delegate = delegate;")
        .addLine("  }");
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  @Override")
          .addLine("  public void visit(%s %s) {", subtype, lowercased(subtype))
          .addLine("    result = delegate.visit(%s);", lowercased(subtype))
          .addLine("  }");
    }
    code.addLine("}");
  }

  private void addSwitchingBuilderImplType(SourceBuilder code) {
    QualifiedName firstType = visitor.getVisitedSubtypes().get(0);
    code.addLine("@SuppressWarnings(\"unchecked\")")
        .addLine("private enum SwitchingBuilderImpl implements SwitchingBuilder {")
        .addLine("  INSTANCE;")
        .addLine("  @Override")
        .addLine("  public %s on(%s<? super %s> visitor) {",
            strictBuilderType(code), Consumer.class, firstType)
        .addLine("    return new SwitchingVisitor(visitor);")
        .addLine("  }")
        .addLine("  @Override")
        .addLine("  public <T> %s on(", strictReturningBuilderType(code))
        .addLine("      %s<? super %s, ? extends T> visitor) {", Function.class, firstType)
        .addLine("    return new SwitchingReturningVisitor<T>(visitor);")
        .addLine("  }")
        .addLine("}");
  }

  private void addSwitchingVisitorType(SourceBuilder code) {
    code.addLine("@SuppressWarnings({\"rawtypes\", \"unchecked\"})")
        .addLine("private static class SwitchingVisitor implements StrictBuilder, %s {",
            visitor.getVisitorType());
    String qualifier = "final";
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  private %s %s<? super %s> %sVisitor;",
          qualifier, Consumer.class, subtype, lowercased(subtype));
      qualifier = "";
    }
    QualifiedName firstType = visitor.getVisitedSubtypes().get(0);
    code.addLine("  private SwitchingVisitor(%s<? super %s> visitor) {",
            Consumer.class, firstType)
        .addLine("    %sVisitor = visitor;", lowercased(firstType))
        .addLine("  }")
        .addLine("  @Override")
        .addLine("  public Object on(Object visitor) {")
        .addLine("    %s.requireNonNull(visitor);", Objects.class);
    for (QualifiedName subtype : skip(visitor.getVisitedSubtypes(), 1)) {
      code.addLine("    if (%sVisitor == null) {", lowercased(subtype))
          .addLine("      %sVisitor = (%s<? super %s>) visitor;",
              lowercased(subtype), Consumer.class, subtype)
          .addLine("    } else");
    }
    code.addLine("    {")
        .addLine("      throw new IllegalStateException(")
        .addLine("          \"on called twice on transient builder\");")
        .addLine("    }")
        .addLine("    return this;")
        .addLine("  }");
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  @Override")
          .addLine("  public void visit(%s %s) {", subtype, lowercased(subtype))
          .addLine("    %1$sVisitor.accept(%1$s);", lowercased(subtype))
          .addLine("  }");
    }
    code.addLine("}");
  }

  private void addSwitchingReturningVisitorType(SourceBuilder code) {
    code.addLine("@SuppressWarnings({\"rawtypes\", \"unchecked\"})")
        .addLine("private static class SwitchingReturningVisitor<T>")
        .addLine("    implements StrictBuilder, %s.Returning<T> {", visitor.getVisitorType());
    String qualifier = "final";
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  private %s %s<? super %s, ? extends T> %sVisitor;",
          qualifier, Function.class, subtype, lowercased(subtype));
      qualifier = "";
    }
    QualifiedName firstType = visitor.getVisitedSubtypes().get(0);
    code.addLine("  private SwitchingReturningVisitor(%s<? super %s, ? extends T> visitor) {",
            Function.class, firstType)
        .addLine("    %sVisitor = visitor;", lowercased(firstType))
        .addLine("  }")
        .addLine("  @Override")
        .addLine("  public Object on(Object visitor) {")
        .addLine("    %s.requireNonNull(visitor);", Objects.class);
    for (QualifiedName subtype : skip(visitor.getVisitedSubtypes(), 1)) {
      code.addLine("    if (%sVisitor == null) {", lowercased(subtype))
          .addLine("      %sVisitor = (%s<? super %s, ? extends T>) visitor;",
              lowercased(subtype), Function.class, subtype)
          .addLine("    } else");
    }
    code.addLine("    {")
        .addLine("      throw new IllegalStateException(")
        .addLine("          \"on called twice on transient builder\");")
        .addLine("    }")
        .addLine("    return this;")
        .addLine("  }");
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  @Override")
          .addLine("  public T visit(%s %s) {", subtype, lowercased(subtype))
          .addLine("    return %1$sVisitor.apply(%1$s);", lowercased(subtype))
          .addLine("  }");
    }
    code.addLine("}");
  }

  private void addBuiltVisitorType(SourceBuilder code) {
    code.addLine("private static class BuiltVisitor implements %s {", visitor.getVisitorType());
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  private final %s<? super %s> %sVisitor;",
          Consumer.class, subtype, lowercased(subtype));
    }
    code.addLine("  private BuiltVisitor(Builder builder) {",
        Consumer.class, visitor.getVisitedType());
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  if (builder.%sVisitor == null) {", lowercased(subtype))
          .addLine("    throw new IllegalStateException(\"no visitor provided for %s\");",
              subtype.getSimpleName())
          .addLine("  }")
          .addLine("  %1$sVisitor = builder.%1$sVisitor;", lowercased(subtype));
    }
    code.addLine("  }")
        .addLine("  private BuiltVisitor(Builder builder, %s<? super %s> fallback) {",
            Consumer.class, visitor.getVisitedType());
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  %1$sVisitor = builder.%1$sVisitor == null ? fallback : builder.%1$sVisitor;",
          lowercased(subtype));
    }
    code.addLine("  }");
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  @Override")
          .addLine("  public void visit(%s %s) {", subtype, lowercased(subtype))
          .addLine("    %1$sVisitor.accept(%1$s);", lowercased(subtype))
          .addLine("  }");
    }
    code.addLine("}");
  }

  private void addBuiltReturningVisitorType(SourceBuilder code) {
    code.addLine("private static class BuiltVisitorReturning<T> implements %s.Returning<T> {",
            visitor.getVisitorType());
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  private final %s<? super %s, ? extends T> %sVisitor;",
          Function.class, subtype, lowercased(subtype));
    }
    code.addLine("  private BuiltVisitorReturning(Builder.Returning<T> builder) {",
        Consumer.class, visitor.getVisitedType());
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  if (builder.%sVisitor == null) {", lowercased(subtype))
          .addLine("    throw new IllegalStateException(\"no visitor provided for %s\");",
              subtype.getSimpleName())
          .addLine("  }")
          .addLine("  %1$sVisitor = builder.%1$sVisitor;", lowercased(subtype));
    }
    code.addLine("  }")
        .addLine("  private BuiltVisitorReturning(")
        .addLine("      Builder.Returning<T> builder, %s<? super %s, ? extends T> fallback) {",
            Function.class, visitor.getVisitedType());
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  %1$sVisitor = builder.%1$sVisitor == null ? fallback : builder.%1$sVisitor;",
          lowercased(subtype));
    }
    code.addLine("  }");
    for (QualifiedName subtype : visitor.getVisitedSubtypes()) {
      code.addLine("  @Override")
          .addLine("  public T visit(%s %s) {", subtype, lowercased(subtype))
          .addLine("    return %1$sVisitor.apply(%1$s);", lowercased(subtype))
          .addLine("  }");
    }
    code.addLine("}");
  }

  static String lowercased(QualifiedName type) {
    return type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1);
  }

}