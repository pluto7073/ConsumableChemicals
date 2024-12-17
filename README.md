Consumable Chemicals is a library mod that provides functionality for adding chemicals to the game that can be tracked in the player

## Additional Features
- `/chemicals` command to set, get, and change a player's chemical levels

## Forge Version?
No, I lack the time, attention span, and motivation to learn an entirely new modloader

## Modpacks?
Completely fine as long as you credit me and link back to the Modrinth or GitHub page for this mod.

# Usage

## Gradle Configuration

`build.gradle`
```groovy
repositories {
    maven { url = "https://pluto-mod-maven.web.app/maven" }
}

dependencies {
    modImplementation "ml.pluto7073:chemicals:${minecraft_version}+${chemicals_version}"
}
```

`gradle.properties`
```properties
chemicals_version=1.0.1
```

## Creating a ChemicalHandler

When creating a Chemical Handler, you can either create your own from scratch by extending `ConsumableChemicalHandler` or choose
from one of the three pre-made abstract handlers below:

#### HalfLifeChemicalHandler
The amount of the chemical is exponentially ticked down in the player using a half life in ticks, specified in the constructor

#### LinearChemicalHandler
A specific amount of the chemical is removed from the player each tick, until the amount reaches zero

#### StaticChemicalHandler
Any chemical consumed remains in the player, until a maximum amount is reached (if specified) or the player dies.
A custom action can be defined for when this maximum amount is hit

### Example

For this example we will use `HalfLifeChemicalHandler`

Let's say we want our chemical to have a half-life of 5 minutes, or 6000 ticks.  This would get us the below code

```java
public class ExampleHandler extends HalfLifeChemicalHandler {

    public ExampleHandler() {
        super(6000); // 5 minutes * 60 sec/min * 20 ticks/sec
    }

}
```

If you are using a pre-made abstract handler, then there's only one method to implement. `getEffectsForAmount()`

(If you are extending ConsumableChemicalHandler, then you will also need to implement `tickPlayer()`, which should process
the chemical each tick, most of the time removing a small amount until there's none left)

This takes in a `float` of the amount and the current `Level`.  This should return a list of Mob Effect Instances (e.g. Invisibility)
that a player with that amount of the chemical should have.

For this example let's say we want the player to have Haste I if they have over 10 of this chemical and Slowness III if
they have over 50.

```java
public class ExampleHandler extends HalfLifeChemicalHandler {

    [...]

    @Override
    public Collection<MobEffectInstance> getEffectsForAmount(float amount, Level level) {
        ArrayList<MobEffectInstance> effects = new ArrayList<>();
        if (amount >= 10) {
            effects.add(new MobEffectInstance(MobEffects.DIG_SPEED, 600));
        }
        if (amount >= 50) {
            effects.add(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 2));
        }
        return effects;
    }

}
```

## Registering the ChemicalHandler

Once your handler class is made, it needs to be registered.

This can be done using `Registry.register(Chemicals.REGISTRY, ID, HANDLER)` and should be done upon mod initialization, like any
other registration being done.

### Example

`ExampleHandler.java`
```java
public class ExampleHandler extends HalfLifeChemicalHandler {

    public static final ResourceLocation EXAMPLE_CHEMICAL = new ResourceLocation("modid", "example_chemicals");
    public static final ExampleHandler INSTANCE = new ExampleHandler();

    [...]

    public static void init() {
        Registry.register(Chemicals.REGISTRY, EXAMPLE_CHEMICAL, INSTANCE);
    }

}
```

`ExampleMod.java`
```java
public class ExampleMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ExampleHandler.init();
    }

}
```

## Adding Chemicals to Foods and Items

You have just added your chemical to the game! You can use the `/chemicals` command to edit a player's chemical levels to test it out,
but now you need an in-game way to consume your chemical.  The easiest way is to add it to an item using Item Components.  To add a
default amount of the chemical to an item, use `ChemicalMap.COMPONENT_TYPE` and `ChemicalMap.Builder` to add it to the item.  This can
then be added to a food item or some other consumable item.

`ExampleItems.java`
```java
public class ExampleItems {

    public static final FoodProperties CHEMICAL_FOOD = new FoodProperties.Builder()
            .alwaysEat().fast().build();

    public static final Item CHEMICAL_PILL = new Item(new Item.Properties().food(CHEMICAL_FOOD)
            .component(ChemicalMap.COMPONENT_TYPE, new ChemicalMap.Builder().add(EXAMPLE_CHEMICAL, 10)));

    public static void init() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("modid", "chemical_pill"), CHEMICAL_PILL);
    }

}
```

`ExampleMod.java`
```java
public class ExampleMod implements ModInitializer {

    @Override
    public void onInitialize() {
        [...]
        ExampleItems.init(); // Items should be registered after chemicals
    }

}
```

## Customizing Chemicals In Game

If you want to add Chemicals to an existing item, like say Steak, this can be done in game with item components.  You can specify
the amounts of each chemical using the `chemicals:chemicals` component, this is an object where the keys are the id of the chemical
and the elements are `float`s of the amount of the chemical.

````mcfunction
/give @s minecraft:steak[chemicals:chemicals={"modid:example_chemical":10}]
````

This command gives the player a steak with 10 units of our example chemical

## More In-Depth Chemical Items

If you have an item that can have a variable amount of a chemical in it, using NBT data for example, it is best
to implement `ChemicalContaining` in your Item Class.  This has one method, `getChemicalContent()`, which takes
the ResourceLocation of the chemical and the ItemStack.  This should calculate the amount of the specified chemical
in this stack and return it.

In this example, the item gets the amount of the chemical in the chemical map component type and returns it.  This
is unneeded because this mod does this already but this is just meant to show what should be done here.

`ExampleItem.java`
```java
public class ExampleItem extends Item implements ChemicalContaining {

    public ExampleItem(Properties properties) {
        super(properties);
    }

    @Override
    public float getChemicalContent(ResourceLocation id, ItemStack stack) {
        Map<ResourceLocation, Float> chemicals = stack.get(ChemicalMap.COMPONENT_TYPE).chemicals();
        if (chemicals.containsKey(id)) {
            return chemicals.get(id);
        }
        return 0;
    }

}
```
