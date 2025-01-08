package codesmell.reload;

import codesmell.reload.annotation.ReloadableClass;
import codesmell.reload.annotation.ReloadableMethod;
import codesmell.reload.interfacemarker.ReloadableMarker;
import codesmell.reload.type.Reloadable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 
 */
public class ReloadableFinder {

    @Autowired
    private ApplicationContext springContext;
    
    /**
     * find and return all instances that are annotated as {@link ReloadableClass} 
     * and also have a method annotated as {@link ReloadableMethod}
     */
    public List<Object> findReloadableClassesWithMethodParam() {
        Map<String, Object> map = springContext.getBeansWithAnnotation(ReloadableClass.class);
        
        List<Object> values = 
            map.entrySet()
                .stream()
                // could check for an interface
                // so we can call a method to reload properties
                // .filter(e -> e.getValue() instanceof Reloadable)
                .map(Map.Entry::getValue)
                // could check for an annotated method to
                // call as well
                .filter(v -> this.hasReloadableMethodWithParameter(v.getClass()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return values;
    }

    /**
     * find and return all instances that are annotated as {@link ReloadableClass} 
     * and also have a method annotated as {@link ReloadableMethod}
     */
    public List<Object> findReloadableClasses() {
        Map<String, Object> map = springContext.getBeansWithAnnotation(ReloadableClass.class);

        List<Object> values = 
            map.entrySet()
                .stream()
                // could check for an interface
                // so we can call a method to reload properties
                // .filter(e -> e.getValue() instanceof Reloadable)
                .map(Map.Entry::getValue)
                // could check for an annotated method to
                // call as well
                .filter(v -> this.hasReloadableMethod(v.getClass()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return values;
    }
    
    /**
     * find and return all methods that are annotated as {@link ReloadableMethod}
     * Note: the class with the method must also be annotated as {@link ReloadableClass}
     * Note: the method returned can ONLY be invoked if it is static
     */
    public List<Method> findReloadableMethods() {
        Map<String, Object> map = springContext.getBeansWithAnnotation(ReloadableClass.class);

        List<Method> methods = 
            map.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .map(v -> this.getReloadableMethod(v.getClass()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return methods;
    }
    
    /**
     * find and return all classes that implement the interface marker {@link ReloadableMarker}
     * and also have a method annotated as {@link ReloadableMethod}
     */
    public List<ReloadableMarker> findReloadableMarkers() {
        Map<String, ReloadableMarker> map = springContext.getBeansOfType(ReloadableMarker.class);

        List<ReloadableMarker> values = 
            map.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(v -> this.hasReloadableMethod(v.getClass()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return values;
    }
    
    /**
     * find and return all classes that implement the interface {@link Reloadable}
     */
    public List<Reloadable> findReloadableTypes() {
        Map<String, Reloadable> map = springContext.getBeansOfType(Reloadable.class);

        List<Reloadable> values = 
            map.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        return values;
    }

    public static Method findReloadableMethod(Object obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ReloadableMethod.class)) {
                return method;
            }
        }
        return null;
    }
    
    protected boolean hasReloadableMethodWithParameter(Class<?> clazz) {
        boolean hasReloadMethodWithParam = false;
        
        Method method = this.getReloadableMethod(clazz);
        
        if (Objects.nonNull(method)) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (Objects.nonNull(paramTypes) && paramTypes.length > 0) {
                hasReloadMethodWithParam = this.checkParam(paramTypes[0]);
            }
        }
            
        return hasReloadMethodWithParam;
    }
    
    protected boolean hasReloadableMethod(Class<?> clazz) {
        return Objects.nonNull(this.getReloadableMethod(clazz));
    }
    
    protected Method getReloadableMethod(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ReloadableMethod.class)) {
                return method;
            }
        }
        return null;
    }
    
    private boolean checkParam(Class<?> paramClazz) {
        boolean isCorrectParamType = false;
        
        try {
            paramClazz.asSubclass(ReloadData.class);
            isCorrectParamType = true;
        } 
        catch (ClassCastException e) {
            isCorrectParamType = false;
        }
        
        return isCorrectParamType;
    }
    
}