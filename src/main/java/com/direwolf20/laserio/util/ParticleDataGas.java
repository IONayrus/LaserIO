package com.direwolf20.laserio.util;

import mekanism.api.chemical.gas.GasStack;
import net.minecraft.core.BlockPos;

public class ParticleDataGas {
    public record PositionData(BlockPos node, byte direction, byte position) {
    }

    public GasStack gasStack;
    public PositionData fromData;
    public PositionData toData;

    public ParticleDataGas(GasStack gasStack, BlockPos fromNode, byte fromDirection, BlockPos toNode, byte toDirection, byte extractPosition, byte insertPosition) {
        this.gasStack = gasStack;
        this.fromData = new PositionData(fromNode, fromDirection, extractPosition);
        this.toData = new PositionData(toNode, toDirection, insertPosition);
    }

}
