package codesmell.reload;

import codesmell.config.FooConfig;
import codesmell.foo.Foo;
import codesmell.foo.FooMarkerMethod;
import codesmell.foo.Grok;
import codesmell.reload.annotation.ReloadableMethod;
import codesmell.reload.interfacemarker.ReloadableMarker;
import codesmell.reload.type.Reloadable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {
        FooConfig.class,
        ReloadableFinderTest.TestConfig.class })
@ActiveProfiles("test")
class ReloadableFinderTest {

    @Autowired
    ReloadableFinder finder;
    
    @Autowired
    Grok spyGrok;
    
    @Autowired
    Foo spyFoo;
    
    @Autowired
    FooMarkerMethod spyFooMarkerMethod;

    @AfterEach
    void reset() {
        Mockito.reset(spyFoo, spyFooMarkerMethod);
    }

    @Test
    void test_findReloadableClasses() throws Exception {
        List<Object> annotatedInstances = finder.findReloadableClasses();
        assertNotNull(annotatedInstances);
        assertEquals(2, annotatedInstances.size());

        Object instanceOne = annotatedInstances.get(0);
        assertNotNull(instanceOne);
        assertEquals("Foo", instanceOne.getClass().getSimpleName());
        
        // call the method
        // cheating we know this has param
        Method fooReloadMethod = ReloadableFinder.findReloadableMethod(instanceOne);
        assertNotNull(fooReloadMethod);
        assertEquals("doSomething", fooReloadMethod.getName());
        fooReloadMethod.invoke(instanceOne, new ReloadData());
        Mockito.verify(spyFoo, Mockito.times(1)).doSomething(Mockito.any());
        
        Object instanceTwo = annotatedInstances.get(1);
        assertNotNull(instanceTwo);
        assertEquals("BarWithMethod", instanceTwo.getClass().getSimpleName());
        
        // call the method
        // cheating we know this has no param and is static
        Method barReloadMethod = ReloadableFinder.findReloadableMethod(instanceTwo);
        assertNotNull(barReloadMethod);
        assertEquals("reload", barReloadMethod.getName());
        barReloadMethod.invoke(null);

    }
    
    @Test
    void test_findReloadableClassesWithMethodParam() throws Exception {
        List<Object> annotatedInstances = finder.findReloadableClassesWithMethodParam();
        assertNotNull(annotatedInstances);
        assertEquals(1, annotatedInstances.size());

        Object instanceOne = annotatedInstances.get(0);
        assertNotNull(instanceOne);
        assertEquals("Foo", instanceOne.getClass().getSimpleName());
        
        // call the method
        Method fooReloadMethod = ReloadableFinder.findReloadableMethod(instanceOne);
        assertNotNull(fooReloadMethod);
        assertEquals("doSomething", fooReloadMethod.getName());
        fooReloadMethod.invoke(instanceOne, new ReloadData());
        Mockito.verify(spyFoo, Mockito.times(1)).doSomething(Mockito.any());
    }

    @Test
    void test_findReloadableMethods() throws Exception {
        List<Method> annotatedMethods = finder.findReloadableMethods();
        assertNotNull(annotatedMethods);
        assertEquals(2, annotatedMethods.size());
        
        // can't call method unless it is static
        // or also have the instance
        Method methodTwo = annotatedMethods.get(1);
        assertNotNull(methodTwo);
        assertEquals("reload", methodTwo.getName());
        methodTwo.isAnnotationPresent(ReloadableMethod.class);
        methodTwo.invoke(null);
    }

    @Test
    void test_findReloadableMarkers() throws Exception {
        List<ReloadableMarker> markerInstances = finder.findReloadableMarkers();
        assertNotNull(markerInstances);
        assertEquals(1, markerInstances.size());

        Object instanceOne = markerInstances.get(0);
        assertNotNull(instanceOne);
        assertEquals("FooMarkerMethod", instanceOne.getClass().getSimpleName());
        
        // call the method
        Method fooReloadMethod = ReloadableFinder.findReloadableMethod(instanceOne);
        assertNotNull(fooReloadMethod);
        assertEquals("doSomethingElse", fooReloadMethod.getName());
        fooReloadMethod.invoke(instanceOne, null);
        Mockito.verify(spyFooMarkerMethod, Mockito.times(1)).doSomethingElse();
    }
    
    @Test
    void test_findReloadableTypes() throws Exception {
        List<Reloadable> reloadableInstances = finder.findReloadableTypes();
        assertNotNull(reloadableInstances);
        assertEquals(1, reloadableInstances.size());

        Reloadable instanceOne = reloadableInstances.get(0);
        assertNotNull(instanceOne);
        assertEquals("Grok", instanceOne.getClass().getSimpleName());
        
        // call the method
        instanceOne.reload();
        Mockito.verify(spyGrok, Mockito.times(1)).reload();
    }

    public static class TestConfig {
        @Bean
        ReloadableFinder reloadableClassFinder() {
            return new ReloadableFinder();
        }
    }

}
