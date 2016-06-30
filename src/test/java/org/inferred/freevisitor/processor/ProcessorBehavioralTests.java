package org.inferred.freevisitor.processor;

import static org.junit.rules.ExpectedException.none;

import org.inferred.freevisitor.FreeVisitor;
import org.inferred.internal.testing.integration.BehaviorTester;
import org.inferred.internal.testing.integration.SourceBuilder;
import org.inferred.internal.testing.integration.TestBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.atomic.AtomicReference;

import javax.tools.JavaFileObject;

@RunWith(JUnit4.class)
public class ProcessorBehavioralTests {

  private static final JavaFileObject VISITABLE_TYPE = new SourceBuilder()
      .addLine("package org.example;")
      .addLine("public interface VisitableType {")
      .addLine("  @%s interface Visitor extends VisitableType_Visitor {}", FreeVisitor.class)
      .addLine("  default <V extends Visitor> V accept(V visitor) {")
      .addLine("    visitor.visit(this);")
      .addLine("    return visitor;")
      .addLine("  }")
      .addLine("}")
      .build();
  private static final JavaFileObject TYPE_A = new SourceBuilder()
      .addLine("package org.example;")
      .addLine("public class TypeA implements VisitableType {}")
      .build();
  private static final JavaFileObject TYPE_B = new SourceBuilder()
      .addLine("package org.example;")
      .addLine("public class TypeB implements VisitableType {}")
      .build();
  private static final JavaFileObject TYPE_C = new SourceBuilder()
      .addLine("package org.example;")
      .addLine("public class TypeC implements VisitableType {}")
      .build();

  @Rule public final ExpectedException thrown = none();

  @Test
  public void basicDispatch() {
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addLine("class FakeVisitor implements VisitableType.Visitor {")
            .addLine("  String visited;")
            .addLine("  @Override public void visit(TypeA typeA) {")
            .addLine("    assertNull(visited);")
            .addLine("    visited = \"A\";")
            .addLine("  }")
            .addLine("  @Override public void visit(TypeB typeB) {")
            .addLine("    assertNull(visited);")
            .addLine("    visited = \"B\";")
            .addLine("  }")
            .addLine("  @Override public void visit(TypeC typeC) {")
            .addLine("    assertNull(visited);")
            .addLine("    visited = \"C\";")
            .addLine("  }")
            .addLine("};")
            .addLine("FakeVisitor visitor = new FakeVisitor();")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("assertEquals(\"A\", a.accept(new FakeVisitor()).visited);")
            .addLine("assertEquals(\"C\", c.accept(new FakeVisitor()).visited);")
            .addLine("assertEquals(\"B\", b.accept(new FakeVisitor()).visited);")
            .addLine("assertEquals(\"C\", c.accept(new FakeVisitor()).visited);")
            .addLine("assertEquals(\"A\", a.accept(new FakeVisitor()).visited);")
            .addLine("assertEquals(\"B\", b.accept(new FakeVisitor()).visited);")
            .build())
        .runTest();
  }

  @Test
  public void returningDispatch() {
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addLine("class FakeVisitor implements VisitableType.Visitor.Returning<String> {")
            .addLine("  @Override public String visit(TypeA typeA) {")
            .addLine("    return \"A\";")
            .addLine("  }")
            .addLine("  @Override public String visit(TypeB typeB) {")
            .addLine("    return \"B\";")
            .addLine("  }")
            .addLine("  @Override public String visit(TypeC typeC) {")
            .addLine("    return \"C\";")
            .addLine("  }")
            .addLine("};")
            .addLine("FakeVisitor visitor = new FakeVisitor();")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("assertEquals(\"A\", visitor.visit(a));")
            .addLine("assertEquals(\"C\", visitor.visit(c));")
            .addLine("assertEquals(\"B\", visitor.visit(b));")
            .addLine("assertEquals(\"C\", visitor.visit(c));")
            .addLine("assertEquals(\"A\", visitor.visit(a));")
            .addLine("assertEquals(\"B\", visitor.visit(b));")
            .build())
        .runTest();
  }

  @Test
  public void fluentDispatch() {
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addLine("%1$s<String> visited = new %1$s<>();", AtomicReference.class)
            .addLine("VisitableType.Visitor visitor = VisitableType.Visitor.Builders.switching()")
            .addLine("    .on((TypeA typeA) -> visited.set(\"A\"))")
            .addLine("    .on((TypeB typeB) -> visited.set(\"B\"))")
            .addLine("    .on((TypeC typeC) -> visited.set(\"C\"));")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("a.accept(visitor);")
            .addLine("assertEquals(\"A\", visited.get());")
            .addLine("c.accept(visitor);")
            .addLine("assertEquals(\"C\", visited.get());")
            .addLine("b.accept(visitor);")
            .addLine("assertEquals(\"B\", visited.get());")
            .addLine("c.accept(visitor);")
            .addLine("assertEquals(\"C\", visited.get());")
            .addLine("a.accept(visitor);")
            .addLine("assertEquals(\"A\", visited.get());")
            .addLine("b.accept(visitor);")
            .addLine("assertEquals(\"B\", visited.get());")
            .build())
        .runTest();
  }

  @Test
  public void fluentReturns() {
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addImport("org.example.VisitableType.Visitor")
            .addLine("Visitor.Returning<String> visitor = Visitor.Builders.switching()")
            .addLine("    .on((TypeA typeA) -> \"A\")")
            .addLine("    .on((TypeB typeB) -> \"B\")")
            .addLine("    .on((TypeC typeC) -> \"C\");")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("assertEquals(\"A\", visitor.visit(a));")
            .addLine("assertEquals(\"C\", visitor.visit(c));")
            .addLine("assertEquals(\"B\", visitor.visit(b));")
            .addLine("assertEquals(\"C\", visitor.visit(c));")
            .addLine("assertEquals(\"A\", visitor.visit(a));")
            .addLine("assertEquals(\"B\", visitor.visit(b));")
            .build())
        .runTest();
  }

  @Test
  public void fluentReturnTypeCanBeExplicitlySpecified() {
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addImport("org.example.VisitableType.Visitor")
            .addLine("Visitor.Returning<Number> visitor = Visitor.Builders.switching()")
            .addLine("    .<Number>on((TypeA typeA) -> 3)")
            .addLine("    .on((TypeB typeB) -> 2.1)")
            .addLine("    .on((TypeC typeC) -> 5L);")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("assertEquals(3, visitor.visit(a));")
            .addLine("assertEquals(5L, visitor.visit(c));")
            .addLine("assertEquals(2.1, visitor.visit(b));")
            .addLine("assertEquals(5L, visitor.visit(c));")
            .addLine("assertEquals(3, visitor.visit(a));")
            .addLine("assertEquals(2.1, visitor.visit(b));")
            .build())
        .runTest();
  }

  @Test
  public void fluentReturnTypeCanBeCast() {
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addImport("org.example.VisitableType.Visitor")
            .addLine("Visitor.Returning<Number> visitor = Visitor.Builders.switching()")
            .addLine("    .on((TypeA typeA) -> (Number) 3)")
            .addLine("    .on((TypeB typeB) -> 2.1)")
            .addLine("    .on((TypeC typeC) -> 5L);")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("assertEquals(3, visitor.visit(a));")
            .addLine("assertEquals(5L, visitor.visit(c));")
            .addLine("assertEquals(2.1, visitor.visit(b));")
            .addLine("assertEquals(5L, visitor.visit(c));")
            .addLine("assertEquals(3, visitor.visit(a));")
            .addLine("assertEquals(2.1, visitor.visit(b));")
            .build())
        .runTest();
  }

  @Test
  public void buildDispatchStrict() {
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addLine("%1$s<String> visited = new %1$s<>();", AtomicReference.class)
            .addLine("VisitableType.Visitor visitor = new VisitableType.Visitor.Builder()")
            .addLine("    .onTypeA(typeA -> visited.set(\"A\"))")
            .addLine("    .onTypeB(typeB -> visited.set(\"B\"))")
            .addLine("    .onTypeC(typeC -> visited.set(\"C\"))")
            .addLine("    .build();")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("a.accept(visitor);")
            .addLine("assertEquals(\"A\", visited.get());")
            .addLine("c.accept(visitor);")
            .addLine("assertEquals(\"C\", visited.get());")
            .addLine("b.accept(visitor);")
            .addLine("assertEquals(\"B\", visited.get());")
            .addLine("c.accept(visitor);")
            .addLine("assertEquals(\"C\", visited.get());")
            .addLine("a.accept(visitor);")
            .addLine("assertEquals(\"A\", visited.get());")
            .addLine("b.accept(visitor);")
            .addLine("assertEquals(\"B\", visited.get());")
            .build())
        .runTest();
  }

  @Test
  public void builderFailsWithIllegalStateExceptionIfNotAllTypesGiven() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("no visitor provided for TypeC");
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addLine("%1$s<String> visited = new %1$s<>();", AtomicReference.class)
            .addLine("VisitableType.Visitor visitor = new VisitableType.Visitor.Builder()")
            .addLine("    .onTypeA(typeA -> visited.set(\"A\"))")
            .addLine("    .onTypeB(typeB -> visited.set(\"B\"))")
            .addLine("    .build();")
            .build())
        .runTest();
  }

  @Test
  public void buildDispatchWithFallback() {
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addLine("%1$s<String> visited = new %1$s<>();", AtomicReference.class)
            .addLine("VisitableType.Visitor visitor = new VisitableType.Visitor.Builder()")
            .addLine("    .onTypeA(typeA -> visited.set(\"A\"))")
            .addLine("    .onTypeB(typeB -> visited.set(\"B\"))")
            .addLine("    .otherwise(other -> visited.set(\"C\"));")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("a.accept(visitor);")
            .addLine("assertEquals(\"A\", visited.get());")
            .addLine("c.accept(visitor);")
            .addLine("assertEquals(\"C\", visited.get());")
            .addLine("b.accept(visitor);")
            .addLine("assertEquals(\"B\", visited.get());")
            .addLine("c.accept(visitor);")
            .addLine("assertEquals(\"C\", visited.get());")
            .addLine("a.accept(visitor);")
            .addLine("assertEquals(\"A\", visited.get());")
            .addLine("b.accept(visitor);")
            .addLine("assertEquals(\"B\", visited.get());")
            .build())
        .runTest();
  }

  @Test
  public void buildReturnsStrict() {
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addLine("VisitableType.Visitor.Returning<String> visitor =")
            .addLine("    new VisitableType.Visitor.Builder.Returning<String>()")
            .addLine("        .onTypeA(typeA -> \"A\")")
            .addLine("        .onTypeB(typeB -> \"B\")")
            .addLine("        .onTypeC(typeC -> \"C\")")
            .addLine("        .build();")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("assertEquals(\"A\", visitor.visit(a));")
            .addLine("assertEquals(\"C\", visitor.visit(c));")
            .addLine("assertEquals(\"B\", visitor.visit(b));")
            .addLine("assertEquals(\"C\", visitor.visit(c));")
            .addLine("assertEquals(\"A\", visitor.visit(a));")
            .addLine("assertEquals(\"B\", visitor.visit(b));")
            .build())
        .runTest();
  }

  @Test
  public void returningBuilderFailsWithIllegalStateExceptionIfNotAllTypesGiven() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("no visitor provided for TypeC");
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addLine("new VisitableType.Visitor.Builder.Returning<String>()")
            .addLine("    .onTypeA(typeA -> \"A\")")
            .addLine("    .onTypeB(typeB -> \"B\")")
            .addLine("    .build();")
            .build())
        .runTest();
  }

  @Test
  public void buildReturnsWithFallback() {
    new BehaviorTester()
        .with(new Processor())
        .with(VISITABLE_TYPE)
        .with(TYPE_A)
        .with(TYPE_B)
        .with(TYPE_C)
        .with(new TestBuilder()
            .addPackageImport("org.example")
            .addLine("VisitableType.Visitor.Returning<String> visitor =")
            .addLine("    new VisitableType.Visitor.Builder.Returning<String>()")
            .addLine("        .onTypeA(typeA -> \"A\")")
            .addLine("        .onTypeB(typeB -> \"B\")")
            .addLine("        .otherwise(other -> \"C\");")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("assertEquals(\"A\", visitor.visit(a));")
            .addLine("assertEquals(\"C\", visitor.visit(c));")
            .addLine("assertEquals(\"B\", visitor.visit(b));")
            .addLine("assertEquals(\"C\", visitor.visit(c));")
            .addLine("assertEquals(\"A\", visitor.visit(a));")
            .addLine("assertEquals(\"B\", visitor.visit(b));")
            .build())
        .runTest();
  }
}
