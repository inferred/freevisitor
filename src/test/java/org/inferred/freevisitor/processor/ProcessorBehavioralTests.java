package org.inferred.freevisitor.processor;

import org.inferred.freevisitor.FreeVisitor;
import org.inferred.internal.testing.integration.BehaviorTester;
import org.inferred.internal.testing.integration.SourceBuilder;
import org.inferred.internal.testing.integration.TestBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ProcessorBehavioralTests {

  @Test
  public void basicDispatch() {
    new BehaviorTester()
        .with(new Processor())
        .with(new SourceBuilder()
            .addLine("package org.example;")
            .addLine("public interface VisitableType {")
            .addLine("  @%s interface Visitor extends VisitableType_Visitor {}", FreeVisitor.class)
            .addLine("}")
            .build())
        .with(new SourceBuilder()
            .addLine("package org.example;")
            .addLine("public class TypeA implements VisitableType {}")
            .build())
        .with(new SourceBuilder()
            .addLine("package org.example;")
            .addLine("public class TypeB implements VisitableType {}")
            .build())
        .with(new SourceBuilder()
            .addLine("package org.example;")
            .addLine("public class TypeC implements VisitableType {}")
            .build())
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
            .addLine("  String doVisit(VisitableType value) {")
            .addLine("    visited = null;")
            .addLine("    visit(value);")
            .addLine("    return visited;")
            .addLine("  }")
            .addLine("};")
            .addLine("FakeVisitor visitor = new FakeVisitor();")
            .addLine("VisitableType a = new TypeA();")
            .addLine("VisitableType b = new TypeB();")
            .addLine("VisitableType c = new TypeC();")
            .addLine("assertEquals(\"A\", visitor.doVisit(a));")
            .addLine("assertEquals(\"C\", visitor.doVisit(c));")
            .addLine("assertEquals(\"B\", visitor.doVisit(b));")
            .addLine("assertEquals(\"C\", visitor.doVisit(c));")
            .addLine("assertEquals(\"A\", visitor.doVisit(a));")
            .addLine("assertEquals(\"B\", visitor.doVisit(b));")
            .build())
        .runTest();
  }
}
