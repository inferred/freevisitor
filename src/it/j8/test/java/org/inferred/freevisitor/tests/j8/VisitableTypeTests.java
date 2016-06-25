package org.inferred.freevisitor.tests.j8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
}
