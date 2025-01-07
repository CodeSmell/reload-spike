# Reloadable Classes Spike
This application is built as part of a spike to play with ways to find classes that can be "reloaded"

In this scenario the intent is to have some number of types that can have behavior triggered on 
all instances of that type. Perhaps to reload properties or a cache etc

## Annotation
Spring made it easy to identify Beans that have an annotation marker at the TYPE level


    @Autowired
    private ApplicationContext springContext;

	Map<String, Object> map = springContext.getBeansWithAnnotation(ReloadableClass.class);
     

That opens up the challenge about what method can be called to perform the reload

The quickest way to identify a method was with another annotation


    protected Method getReloadableMethod(Class<?> clazz) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ReloadableMethod.class)) {
                return method;
            }
        }
    }

This method can then be invoked as follows:

	method.invoke(instanceOfBean, null);

Basic pro/con
- The annotation eliminates the need for a naming pattern (Effective Java item 39)
- The annotation does not require a specific type to be "reloadable" (can be applied to any type)
- The annotation does not require a specific method to be "reloadable" (can be applied to any method)
- Lack of a type means some types of errors will be harder to catch until run-time

## Interface Marker
Effective Java item 41 suggests that marker interfaces and thus a specific type may be 
more desirable than an annotation marker. 

Spring also makes that really easy to do.

    @Autowired
    private ApplicationContext springContext;
    Map<String, ReloadableMarker> map = springContext.getBeansOfType(ReloadableMarker.class);


That still leaves the challenge identifying which method to call to perform the reload.
This can be marked with the annotation and invoked as above.

Basic pro/con
- The interface marker eliminates the need for a naming pattern (Effective Java item 39)
- The interface marker means classes of a certain type are targeted (Effective Java item 41)
- The annotation for the method does not require a specific method to be "reloadable" (can be applied to any method)
- Having a specific type allows some errors to be caught at compile-time
- This is probably a better approach when calling a method isn't needed but rather the type is passed around

## Functional Interface
Since we want to identify some small number of classes and call a method to trigger some behavior, 
it is reasonable to consider using an interface with a single method

	@FunctionalInterface
	public interface Reloadable {
    	void reload();
	}

We have already seen how to find the classes of a particular type with Spring.


    @Autowired
    private ApplicationContext springContext;
    Map<String, Reloadable> map = springContext.getBeansOfType(Reloadable.class);

Now we have a type that has a specific method (similar to Runnable) that we can call

	myReloadable.reload();

Basic pro/con
- The interface eliminates the need for a naming pattern (Effective Java item 39)
- The interface means classes of a certain type are targeted (Effective Java item 41)
- The interface method requires a specific name for the method but makes it easier to invoke
- Having a specific type and method allows some errors to be caught at compile-time
- If we want to add parameters they will be clearly defined, along with their types, in the interface

