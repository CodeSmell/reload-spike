package codesmell.foo;

import codesmell.reload.type.Reloadable;

public class Grok implements Reloadable {

    @Override
    public void reload() {
        // note: when reloading 
        // care should be taken to make sure state
        // isn't in a messy state until this completes
    }
}
