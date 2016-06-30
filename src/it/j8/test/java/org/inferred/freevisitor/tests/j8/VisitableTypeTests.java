package org.inferred.freevisitor.tests.j8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class VisitableTypeTests {

  @Test
  public void basicDispatch() {
    class FakeVisitor implements VisitableType.Visitor {
      private String visited;

      @Override
      public void visit(TypeA typeA) {
        assertNull(visited);
        visited = "A";
      }

      @Override
      public void visit(TypeB typeB) {
        assertNull(visited);
        visited = "B";
      }

      @Override
      public void visit(TypeC typeC) {
        assertNull(visited);
        visited = "C";
      }
    }

    VisitableType typeA = new TypeA();
    FakeVisitor visitor = new FakeVisitor();
    visitor.visit(typeA);
    assertEquals("A", visitor.visited);

    VisitableType typeC = new TypeC();
    visitor = new FakeVisitor();
    visitor.visit(typeC);
    assertEquals("C", visitor.visited);

    VisitableType typeB = new TypeB();
    visitor = new FakeVisitor();
    visitor.visit(typeB);
    assertEquals("B", visitor.visited);
  }

  @Test
  public void returningDispatch() {
    class FakeVisitor implements VisitableType.Visitor.Returning<String> {

      @Override
      public String visit(TypeA typeA) {
        return "A";
      }

      @Override
      public String visit(TypeB typeB) {
        return "B";
      }

      @Override
      public String visit(TypeC typeC) {
        return "C";
      }
    }

    FakeVisitor visitor = new FakeVisitor();
    assertEquals("A", visitor.visit(new TypeA()));
    assertEquals("C", visitor.visit(new TypeC()));
    assertEquals("B", visitor.visit(new TypeB()));
  }

  @Test
  public void fluentDispatch() {
    AtomicReference<String> result = new AtomicReference<>();
    VisitableType.Visitor visitor = VisitableType.Visitor.Builders.switching()
        .on((TypeA typeA) -> result.set("A"))
        .on((TypeB typeB) -> result.set("B"))
        .on((TypeC typeC) -> result.set("C"));
    visitor.visit(new TypeA());
    assertEquals("A", result.get());
    visitor.visit(new TypeC());
    assertEquals("C", result.get());
    visitor.visit(new TypeB());
    assertEquals("B", result.get());
  }

  @Test
  public void fluentReturns() {
    VisitableType.Visitor.Returning<String> visitor = VisitableType.Visitor.Builders
        .switching()
        .on((TypeA typeA) -> "A")
        .on((TypeB typeB) -> "B")
        .on((TypeC typeC) -> "C");
    assertEquals("A", visitor.visit(new TypeA()));
    assertEquals("C", visitor.visit(new TypeC()));
    assertEquals("B", visitor.visit(new TypeB()));
  }
}
