package com.direwolf20.laserio.util;

import mekanism.api.chemical.gas.GasStack;
import net.minecraft.core.BlockPos;

public class ParticleRenderDataGas {
    public GasStack gasStack;
    public BlockPos fromPos;
    public byte direction;
    public BlockPos toPos;
    public byte position;

    public ParticleRenderDataGas(GasStack gasStack, BlockPos fromPos, byte direction, BlockPos toPos, byte position) {
        this.gasStack = gasStack;
        this.fromPos = fromPos;
        this.direction = direction;
        this.toPos = toPos;
        this.position = position;
    }

}
