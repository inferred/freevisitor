package org.inferred.freevisitor.processor;

import static org.inferred.internal.source.ModelUtils.maybeAsTypeElement;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

abstract class TypesImpl implements Types {

  @Override
  public boolean isSubtype(TypeMirror t1, TypeMirror t2) {
    return isAssignable(t1, t2);
  }

  @Override
  public boolean isAssignable(TypeMirror t1, TypeMirror t2) {
    TypeElement type2 = maybeAsTypeElement(t2).orNull();
    if (type2 == null || !type2.getTypeParameters().isEmpty()) {
      throw new UnsupportedOperationException("Only non-generic types supported; got " + t2);
    }
    TypeElement supertype = maybeAsTypeElement(t1).orNull();
    while (supertype != null) {
      if (type2.equals(supertype)) {
        return true;
      }
      if (type2.getKind() == ElementKind.INTERFACE) {
        for (TypeMirror iface : supertype.getInterfaces()) {
          if (type2.equals(maybeAsTypeElement(iface).orNull())) {
            return true;
          }
        }
      }
      supertype = maybeAsTypeElement(supertype.getSuperclass()).orNull();
    }
    return false;
  }
}