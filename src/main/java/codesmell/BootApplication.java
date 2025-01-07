package codesmell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The @SpringBootApplication is equivalent
 * to: @SpringBootConfiguration, @EnableAutoConfiguration and @ComponentScan
 * <p>
 * This class should sit in the top-most/root package so that component scan
 * works reasonably and correctly
 * <p>
 * all classes with following annotations are loaded
 * (@Component, @Service, @Repository, @Controller, and others)
 */
@SpringBootApplication
public class BootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }
}
