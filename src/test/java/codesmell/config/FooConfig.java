package codesmell.config;

import codesmell.foo.BarNoMethod;
import codesmell.foo.BarWithMethod;
import codesmell.foo.Foo;
import codesmell.foo.FooMarkerMethod;
import codesmell.foo.FooMarkerNoMethod;
import codesmell.foo.Grok;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FooConfig {

//    @Bean
//    ClassPathScanningCandidateComponentProvider buildAnnotationFinder() {
//        ClassPathScanningCandidateComponentProvider provider = 
//                new ClassPathScanningCandidateComponentProvider(false);
//        
//        provider.addIncludeFilter(new AnnotationTypeFilter(ReloadableClass.class));
//        
//        return provider;
//    }
    
    @Bean
    Foo spyFoo() {
        return Mockito.spy(new Foo());
    }
    
    @Bean
    FooMarkerMethod spyFooMarkerMethod() {
        return Mockito.spy(new FooMarkerMethod());
    }
    
    @Bean
    FooMarkerNoMethod buildFooMarkerNoMethod() {
        return new FooMarkerNoMethod();
    }
    
    @Bean
    BarNoMethod buildBarNoMethod() {
        return new BarNoMethod();
    }
    
    @Bean
    BarWithMethod buildBarWithMethod() {
        return new BarWithMethod();
    }

    @Bean
    Grok spyGrok() {
        return Mockito.spy(new Grok());
    }


}
