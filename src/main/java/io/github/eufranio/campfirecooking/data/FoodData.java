package io.github.eufranio.campfirecooking.data;

import io.github.eufranio.campfirecooking.CampfireCooking;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.*;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

/**
 * Created by Frani on 17/11/2018.
 */
public class FoodData extends AbstractSingleData<ItemStack, FoodData, FoodData.Immutable> implements DataManipulator<FoodData, FoodData.Immutable> {

    public FoodData(ItemStack value) {
        super(value, CampfireCooking.FOOD);
    }

    @Override
    public Optional<FoodData> fill(DataHolder dataHolder, MergeFunction overlap) {
        final Optional<FoodData> trailData = dataHolder.get(FoodData.class);
        return trailData.isPresent() ? trailData.map(data -> this.setValue(data.getValue())) : Optional.of(this);
    }

    @Override
    public Optional<FoodData> from(DataContainer container) {
        if (!container.contains(CampfireCooking.FOOD.getQuery())) {
            return Optional.empty();
        }

        ItemStack trail = container.getSerializable(CampfireCooking.FOOD.getQuery(), ItemStack.class)
                .orElse(ItemStack.of(ItemTypes.STONE, 1));
        this.setValue(trail);
        return Optional.of(this);
    }

    @Override
    public FoodData copy() {
        return new FoodData(this.getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(this.getValue());
    }

    @Override
    protected Value<ItemStack> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(
                CampfireCooking.FOOD, getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(CampfireCooking.FOOD, this.getValue());
    }

    public static final class Immutable
            extends AbstractImmutableSingleData<ItemStack, Immutable, FoodData>
            implements ImmutableDataManipulator<Immutable, FoodData> {

        Immutable(ItemStack value) {
            super(value, CampfireCooking.FOOD);
        }

        @Override
        protected ImmutableValue<ItemStack> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(
                    CampfireCooking.FOOD, getValue()).asImmutable();
        }

        @Override
        public FoodData asMutable() {
            return new FoodData(this.value);
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(CampfireCooking.FOOD, this.value);
        }

    }

    public static final class Builder extends AbstractDataBuilder<FoodData> implements DataManipulatorBuilder<FoodData, Immutable> {

        public Builder() {
            super(FoodData.class, 1);
        }

        @Override
        public FoodData create() {
            return new FoodData(ItemStack.of(ItemTypes.STONE, 1));
        }

        @Override
        public Optional<FoodData> createFrom(DataHolder dataHolder) {
            final Optional<FoodData> trailData = dataHolder.get(FoodData.class);
            return trailData.isPresent() ? trailData : Optional.of(new FoodData(ItemStack.of(ItemTypes.STONE, 1)));
        }

        @Override
        protected Optional<FoodData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(CampfireCooking.FOOD.getQuery())) {
                ItemStack item = container.getSerializable(CampfireCooking.FOOD.getQuery(), ItemStack.class)
                        .orElse(ItemStack.of(ItemTypes.STONE, 1));
                return Optional.of(new FoodData(item));
            }
            return Optional.empty();
        }
    }
}
