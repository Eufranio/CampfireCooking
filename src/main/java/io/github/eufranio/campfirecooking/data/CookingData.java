package io.github.eufranio.campfirecooking.data;

import io.github.eufranio.campfirecooking.CampfireCooking;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractIntData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

/**
 * Created by Frani on 17/11/2018.
 */
public class CookingData extends AbstractIntData<CookingData, CookingData.Immutable> implements DataManipulator<CookingData, CookingData.Immutable> {

    public CookingData(int time) {
        super(time, CampfireCooking.COOKING_TIME);
    }

    @Override
    public Optional<CookingData> fill(DataHolder dataHolder, MergeFunction overlap) {
        final Optional<CookingData> data = dataHolder.get(CookingData.class);
        return data.isPresent() ? data.map(d -> this.setValue(d.getValue())) : Optional.of(this);
    }

    @Override
    public Optional<CookingData> from(DataContainer container) {
        if (!container.contains(CampfireCooking.COOKING_TIME.getQuery())) {
            return Optional.empty();
        }

        int time = container.getInt(CampfireCooking.COOKING_TIME.getQuery()).orElse(-1);
        this.setValue(time);
        return Optional.of(this);
    }

    @Override
    public CookingData copy() {
        return new CookingData(this.getValue());
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(this.getValue());
    }

    @Override
    protected Value<Integer> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(
                CampfireCooking.COOKING_TIME, getValue());
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(CampfireCooking.COOKING_TIME, this.getValue());
    }

    public static final class Immutable
            extends AbstractImmutableSingleData<Integer, Immutable, CookingData>
            implements ImmutableDataManipulator<Immutable, CookingData> {

        Immutable(int value) {
            super(value, CampfireCooking.COOKING_TIME);
        }

        @Override
        protected ImmutableValue<Integer> getValueGetter() {
            return Sponge.getRegistry().getValueFactory().createValue(
                    CampfireCooking.COOKING_TIME, getValue()).asImmutable();
        }

        @Override
        public CookingData asMutable() {
            return new CookingData(this.value);
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(CampfireCooking.COOKING_TIME, this.value);
        }

    }

    public static final class Builder extends AbstractDataBuilder<CookingData> implements DataManipulatorBuilder<CookingData, Immutable> {

        public Builder() {
            super(CookingData.class, 1);
        }

        @Override
        public CookingData create() {
            return new CookingData(-1);
        }

        @Override
        public Optional<CookingData> createFrom(DataHolder dataHolder) {
            final Optional<CookingData> data = dataHolder.get(CookingData.class);
            return data.isPresent() ? data : Optional.of(new CookingData(-1));
        }

        @Override
        protected Optional<CookingData> buildContent(DataView container) throws InvalidDataException {
            if (container.contains(CampfireCooking.COOKING_TIME.getQuery())) {
                int value = container.getInt(CampfireCooking.COOKING_TIME.getQuery()).orElse(-1);
                return Optional.of(new CookingData(value));
            }
            return Optional.empty();
        }
    }
}
