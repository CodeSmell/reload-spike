package codesmell.foo;

import codesmell.reload.annotation.ReloadableClass;
import codesmell.reload.annotation.ReloadableMethod;

@ReloadableClass
public class Foo {

    @ReloadableMethod
    public void doSomething() {
    }
    
}
