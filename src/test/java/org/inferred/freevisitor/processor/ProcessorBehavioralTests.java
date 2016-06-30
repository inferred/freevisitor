package org.inferred.freevisitor.processor;

import org.inferred.freevisitor.FreeVisitor;
import org.inferred.internal.testing.integration.BehaviorTester;
import org.inferred.internal.testing.integration.SourceBuilder;
import org.inferred.internal.testing.integration.TestBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.tools.JavaFileObject;

@RunWith(JUnit4.class)
public class ProcessorBehavioralTests {

  private static final JavaFileObject TYPE_C = new SourceBuilder()
      .addLine("package org.example;")
      .addLine("public class TypeC implements VisitableType {}")
      .build();
  private static final JavaFileObject TYPE_B = new SourceBuilder()
      .addLine("package org.example;")
      .addLine("public class TypeB implements VisitableType {}")
      .build();
  private static final JavaFileObject TYPE_A = new SourceBuilder()
      .addLine("package org.example;")
      .addLine("public class TypeA implements VisitableType {}")
      .build();
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
}
