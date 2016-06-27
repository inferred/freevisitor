package org.inferred.freevisitor.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.entry;
import static org.inferred.internal.testing.unit.ClassTypeImpl.iface;
import static org.inferred.internal.testing.unit.ClassTypeImpl.type;

import com.google.common.collect.ImmutableSet;

import org.inferred.internal.source.CannotGenerateCodeException;
import org.inferred.internal.source.QualifiedName;
import org.inferred.internal.testing.Partial;
import org.inferred.internal.testing.unit.FakeMessager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRule;

import java.util.Arrays;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@RunWith(JUnit4.class)
public class AnalyserTests {

  @Rule public final MockitoJUnitRule mocks = new MockitoJUnitRule(this);

  @Mock private Elements elements;
  private final FakeMessager messager = new FakeMessager();
  private final Types types = Partial.of(TypesImpl.class);
  private Analyser analyser;

  @Before
  public void setup() {
    analyser = new Analyser(elements, messager, types);
  }

  @Test
  public void dynamically_located_subtypes_ordered_alphabetically()
      throws CannotGenerateCodeException {
    TypeElement visitableType = iface("VisitableType").asElement();
    TypeElement visitorType = iface("Visitor").nestedIn(visitableType).asElement();
    TypeElement typeA = type("TypeA").implementing(visitableType).asElement();
    TypeElement typeB = type("TypeB").implementing(visitableType).asElement();
    TypeElement typeC = type("TypeC").implementing(visitableType).asElement();

    Visitor visitor = analyser.analyseVisitorType(
        visitorType, ImmutableSet.of(visitableType, typeA, typeB, typeC));

    assertThat(visitor).isEqualTo(new Visitor.Builder()
        .setGeneratedType(name("VisitableType_Visitor"))
        .setVisitorType(name("VisitableType.Visitor"))
        .setVisitedType(name("VisitableType"))
        .addVisitedSubtypes(name("TypeA"), name("TypeB"), name("TypeC"))
        .addNestedClasses(name("VisitableType_Visitor.Internal"))
        .build());
  }

  @Test
  public void top_level_types_not_supported() {
    assertThatThrownBy(() -> {

      analyser.analyseVisitorType(iface("Visitor").asElement(), ImmutableSet.of());

    }).isInstanceOf(CannotGenerateCodeException.class);
    assertThat(messager.getMessagesByElement())
        .contains(entry("Visitor", "[ERROR] @FreeVisitor not supported on top level types"));
  }

  private static QualifiedName name(String name) {
    String[] simpleNames = name.split("[.]");
    return QualifiedName.of(
        "org.example", simpleNames[0], Arrays.copyOfRange(simpleNames, 1, simpleNames.length));
  }
}
