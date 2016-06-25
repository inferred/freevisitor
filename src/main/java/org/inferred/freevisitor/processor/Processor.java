package org.inferred.freevisitor.processor;

import static javax.lang.model.SourceVersion.RELEASE_8;
import static javax.lang.model.util.ElementFilter.typesIn;
import static org.inferred.internal.source.FilerUtils.writeCompilationUnit;

import com.google.auto.service.AutoService;

import org.inferred.freevisitor.FreeVisitor;
import org.inferred.internal.source.CannotGenerateCodeException;
import org.inferred.internal.source.CompilationUnitBuilder;
import org.inferred.internal.source.SimpleTypeProcessor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends SimpleTypeProcessor {

  private Analyser analyser;
  private final Set<Element> allRootElements = new LinkedHashSet<>();

  @Override
  protected void init() {
    analyser = new Analyser(messager, types);
  }

  @Override
  protected SourceVersion minimumSupportedVersion() {
    return RELEASE_8;
  }

  @Override
  protected Class<? extends Annotation> annotation() {
    return FreeVisitor.class;
  }

  @Override
  protected void processAnnotatedElements(
      Set<? extends Element> annotatedElements, RoundEnvironment round) {
    allRootElements.addAll(round.getRootElements());
    process(typesIn(annotatedElements), this::processType);
  }

  private void processType(TypeElement visitorType)
      throws CannotGenerateCodeException, IOException {
    Visitor metadata = analyser.analyseVisitorType(visitorType, allRootElements);
    CompilationUnitBuilder code =
        new CompilationUnitBuilder(env, metadata.getGeneratedType(), metadata.getNestedClasses());
    code.add(new GeneratedVisitor(metadata));
    writeCompilationUnit(filer, metadata.getGeneratedType(), visitorType, code.toString());
  }
}
