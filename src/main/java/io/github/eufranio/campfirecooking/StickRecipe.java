package io.github.eufranio.campfirecooking;

import com.google.common.collect.Lists;
import io.github.eufranio.campfirecooking.data.CookingData;
import io.github.eufranio.campfirecooking.data.FoodData;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.item.FoodRestorationProperty;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Frani on 17/11/2018.
 */
public class StickRecipe implements CraftingRecipe {

    @Override
    public boolean isValid(CraftingGridInventory grid, World world) {
        if (!grid.contains(ItemTypes.STICK)) return false;
        for (Inventory slot : grid.slots()) {
            Optional<ItemStack> stack = slot.peek();
            if (stack.isPresent()) {
                if (stack.get().getProperty(FoodRestorationProperty.class).isPresent()) {
                    if (stack.get().getQuantity() != 1) return false;
                    if (getSmeltResult(stack.get()) != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ItemStackSnapshot getResult(CraftingGridInventory grid) {
        for (Inventory slot : grid.slots()) {
            Optional<ItemStack> stack = slot.peek();
            if (stack.isPresent()) {
                if (stack.get().getProperty(FoodRestorationProperty.class).isPresent()) {
                    ItemStackSnapshot s = getSmeltResult(stack.get());
                    if (s != null) {
                        Inventory inv = grid.query(QueryOperationTypes.ITEM_STACK_EXACT.of(ItemStack.of(ItemTypes.STICK, 1)));
                        ItemStack stick = inv.poll().get();

                        stick.offer(new CookingData(CampfireCooking.getInstance().config.get().cookTime));
                        stick.offer(new FoodData(stack.get()));
                        stick.offer(Keys.ITEM_LORE, CampfireCooking.getInstance().config.get().stickLore.stream()
                            .map(st -> st.replace("%item%", stack.get().getType().getTranslation().get())
                                    .replace("%time%", ""+CampfireCooking.getInstance().config.get().cookTime))
                                .map(TextSerializers.FORMATTING_CODE::deserialize)
                                .collect(Collectors.toList()));
                        return stick.createSnapshot();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<ItemStackSnapshot> getRemainingItems(CraftingGridInventory grid) {
        List<ItemStackSnapshot> items = Lists.newArrayList();
        for (int i = 0; i < 9; i++) {
            items.add(ItemTypes.NONE.getTemplate());
        }
        return items;
    }

    @Override
    public Optional<String> getGroup() {
        return Optional.empty();
    }

    @Override
    public String getId() {
        return "campfirecooking:stickrecipe";
    }

    @Override
    public String getName() {
        return "StickRecipe";
    }

    @Override
    public ItemStackSnapshot getExemplaryResult() {
        return ItemTypes.STICK.getTemplate();
    }

    public static ItemStackSnapshot getSmeltResult(ItemStack item) {
        SmeltingResult result = Sponge.getRegistry().getSmeltingRecipeRegistry()
                .getResult(item.createSnapshot())
                .orElse(null);
        return result == null ? null : result.getResult();
    }
}
