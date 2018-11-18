package io.github.eufranio.campfirecooking;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import io.github.eufranio.campfirecooking.config.Config;
import io.github.eufranio.campfirecooking.config.MainConfig;
import io.github.eufranio.campfirecooking.data.CookingData;
import io.github.eufranio.campfirecooking.data.FoodData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Plugin(
        id = "campfirecooking",
        name = "CampfireCooking",
        description = "Cooks food on custom campfires",
        authors = {
                "Eufranio"
        }
)
public class CampfireCooking {

    @Inject
    private PluginContainer container;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    public Config<MainConfig> config;

    public static Key<Value<ItemStack>> FOOD = Key.builder()
            .type(new TypeToken<Value<ItemStack>>(){})
            .query(DataQuery.of("food"))
            .id("food")
            .name("Food")
            .build();

    public static Key<Value<Integer>> COOKING_TIME = Key.builder()
            .type(new TypeToken<Value<Integer>>(){})
            .query(DataQuery.of("time"))
            .id("time")
            .name("Time")
            .build();

    @Listener
    public void onPreInit(GamePreInitializationEvent e) {
        Sponge.getRegistry().getCraftingRecipeRegistry().register(new StickRecipe());
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        this.config = new Config<>(MainConfig.class, "CampfireCooking.conf", this.configDir);
        Task.builder()
                .interval(1, TimeUnit.SECONDS)
                .execute(() -> {
                    Sponge.getServer().getOnlinePlayers().forEach(p -> {

                        Location<World> loc = p.getLocation();
                        boolean hasCampfire = false;
                        loop:
                        for (int x = -2; x < 2; x++) {
                            for (int y = -2; y < 2; y++) {
                                for (int z = -2; z < 2; z++) {
                                    Location<World> l = new Location<World>(loc.getExtent(), loc.getX() + x, loc.getY() + y, loc.getZ() + z);
                                    if (l.getBlockType() == BlockTypes.NETHERRACK && l.getRelative(Direction.UP).getBlockType() == BlockTypes.FIRE) {
                                        hasCampfire = true;
                                        break loop;
                                    }
                                }
                            }
                        }

                        if (hasCampfire) {
                            ItemStack stack = p.getItemInHand(HandTypes.MAIN_HAND)
                                    .orElse(p.getItemInHand(HandTypes.OFF_HAND)
                                            .orElse(null));
                            stack.get(CampfireCooking.COOKING_TIME).ifPresent(time -> {
                                Inventory slot = p.getInventory().query(QueryOperationTypes.ITEM_STACK_EXACT.of(stack));
                                if (time == 1) {
                                    slot.set(StickRecipe.getSmeltResult(stack.get(CampfireCooking.FOOD).get()).createStack());
                                } else {
                                    stack.offer(Keys.ITEM_LORE, this.config.get().stickLore.stream()
                                            .map(st ->
                                                    st.replace("%item%", stack.get(FOOD).get().getType().getTranslation().get())
                                                            .replace("%time%", "" + (time - 1)))
                                            .map(TextSerializers.FORMATTING_CODE::deserialize)
                                            .collect(Collectors.toList()));
                                    stack.transform(CampfireCooking.COOKING_TIME, t -> t - 1);
                                }
                            });
                        }
                    });
                })
                .submit(this);
    }

    @Listener
    public void onRegisterManipulator(GameRegistryEvent.Register<DataRegistration<?, ?>> event) {
        DataRegistration.builder()
                .dataClass(FoodData.class)
                .immutableClass(FoodData.Immutable.class)
                .builder(new FoodData.Builder())
                .manipulatorId("food")
                .dataName("Stick Food")
                .buildAndRegister(this.container);

        DataRegistration.builder()
                .dataClass(CookingData.class)
                .immutableClass(CookingData.Immutable.class)
                .builder(new CookingData.Builder())
                .manipulatorId("time")
                .dataName("Cooking Time")
                .buildAndRegister(this.container);
    }

    @Listener
    public void registerKeys(GameRegistryEvent.Register<Key<?>> event) {
        event.register(CampfireCooking.FOOD);
        event.register(CampfireCooking.COOKING_TIME);
    }

    public static CampfireCooking getInstance() {
        return (CampfireCooking) Sponge.getPluginManager().getPlugin("campfirecooking")
                .get()
                .getInstance()
                .get();
    }

}
