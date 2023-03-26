package com.direwolf20.laserio.util;

import java.util.Objects;

import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.nbt.CompoundTag;

public class GasStackKey {
    public final Gas gas;
    public final CompoundTag nbt;
    private final int hash;

    public GasStackKey(GasStack stack, boolean compareNBT) {
      this.gas = stack.getType();
      this.nbt = compareNBT ? stack.getRaw().write(new CompoundTag()): new CompoundTag();
      this.hash = Objects.hash(gas, nbt);
    }

    public GasStack getStack() {
        return new GasStack(getStack(), hash);
    }

    public GasStack getStack(int amt) {
        return new GasStack(gas, amt);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GasStackKey) {
            return (((GasStackKey) obj).gas == this.gas) && Objects.equals(((GasStackKey) obj).nbt, this.nbt);
        }
        return false;
    }
}
