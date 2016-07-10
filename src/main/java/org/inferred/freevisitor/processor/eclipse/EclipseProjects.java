package org.inferred.freevisitor.processor.eclipse;

import com.google.common.base.Throwables;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Utility class for working with the current Eclipse project in ECJ.
 */
public class EclipseProjects {

  /**
   * Returns all types in the current Eclipse project.
   *
   * <p>During incremental compilation, Eclipse may not supply the full set of types via
   * {@link javax.annotation.processing.RoundEnvironment#getRootElements() the round environment}.
   * This method allows that information to be recovered.
   *
   * @return all types in the current Eclipse project, or an empty stream if {@code elements}
   *     is not provided by Eclipse
   */
  public static Stream<TypeElement> eclipseProjectTypes(Elements elements) {
    Object /* IJavaProject */ env = getField(elements, "_env", "_javaProject");
    if (env == null) {
      return Stream.of();
    }
    Object[] /* IPackageFragment[] */ packageFragments = invokeGetter(env, "getPackageFragments");
    return Arrays.stream(packageFragments)
        .flatMap(fragment -> {
          Object[] /* ICompilationUnit[] */ compilationUnits =
              invokeGetter(fragment, "getCompilationUnits");
          return Arrays.stream(compilationUnits);
        })
        .map(EclipseProjects::getQualifiedName)
        .map(elements::getTypeElement)
        .filter(Objects::nonNull);
  }

  private static String getQualifiedName(Object compilationUnit) {
    StringBuilder qualifiedName = new StringBuilder();
    char[][] packageName = invokeGetter(compilationUnit, "getPackageName");
    for (char[] packageNameFragment : packageName) {
      qualifiedName.append(packageNameFragment).append('.');
    }
    char[] simpleName = invokeGetter(compilationUnit, "getMainTypeName");
    qualifiedName.append(simpleName);
    return qualifiedName.toString();
  }

  private static Object getField(Object obj, String... fieldNames) {
    Object result = obj;
    for (String fieldName : fieldNames) {
      Class<?> cls = result.getClass();
      Field field = null;
      do {
        try {
          field = cls.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
        }
      } while (field == null && (cls = cls.getSuperclass()) != null);
      if (field == null) {
        return null;
      }
      field.setAccessible(true);
      try {
        result = field.get(result);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return result;
  }

  private static <T> T invokeGetter(Object obj, String methodName) {
    try {
      Method method = obj.getClass().getMethod(methodName);
      method.setAccessible(true);
      @SuppressWarnings("unchecked")
      T result = (T) method.invoke(obj);
      return result;
    } catch (NoSuchMethodException e) {
      throw new LinkageError(e.getMessage());
    } catch (InvocationTargetException e) {
      throw Throwables.propagate(e.getCause());
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

}
