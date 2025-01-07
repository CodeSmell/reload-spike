package codesmell.foo;

import codesmell.reload.annotation.ReloadableMethod;
import codesmell.reload.interfacemarker.ReloadableMarker;

public class FooMarkerMethod implements ReloadableMarker {

    @ReloadableMethod
    public void doSomethingElse() {
    }
    
}
