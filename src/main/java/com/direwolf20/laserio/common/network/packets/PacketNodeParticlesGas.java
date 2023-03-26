package com.direwolf20.laserio.common.network.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.direwolf20.laserio.common.blockentities.LaserNodeBE;
import com.direwolf20.laserio.util.ParticleDataGas;
import com.direwolf20.laserio.util.ParticleRenderDataGas;

import mekanism.api.chemical.ChemicalUtils;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class PacketNodeParticlesGas {
    private List<ParticleDataGas> particleList;

    public PacketNodeParticlesGas(List<ParticleDataGas> particleList) {
        this.particleList = particleList;
    }

    public static void encode(PacketNodeParticlesGas msg, FriendlyByteBuf buffer) {
        List<ParticleDataGas> tempList = msg.particleList;
        int size = tempList.size();
        buffer.writeInt(size);
        for (ParticleDataGas data : tempList) {
            ChemicalUtils.writeChemicalStack(buffer, data.gasStack);
            if (data.fromData != null) {
                buffer.writeBlockPos(data.fromData.node());
                buffer.writeByte(data.fromData.direction());
                buffer.writeByte(data.fromData.position());
            }
            if (data.toData != null) {
                buffer.writeBlockPos(data.toData.node());
                buffer.writeByte(data.toData.direction());
                buffer.writeByte(data.toData.position());
            }
        }
    }

    public static PacketNodeParticlesGas decode(FriendlyByteBuf buffer) {
        List<ParticleDataGas> thisList = new ArrayList<>();
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            GasStack gasStack = ChemicalUtils.readGasStack(buffer);
            BlockPos fromNode = buffer.readBlockPos();
            byte fromDirection = buffer.readByte();
            byte extractPosition = buffer.readByte();
            BlockPos toNode = buffer.readBlockPos();
            byte toDirection = buffer.readByte();
            byte insertPosition = buffer.readByte();
            ParticleDataGas data = new ParticleDataGas(gasStack, fromNode, fromDirection, toNode, toDirection, extractPosition, insertPosition);
            thisList.add(data);
        }
        return new PacketNodeParticlesGas(thisList);
    }

    public static class Handler {
        public static void handle(PacketNodeParticlesGas msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> clientPacketHandler(msg)));
            ctx.get().setPacketHandled(true);
        }
    }

    public static void clientPacketHandler(PacketNodeParticlesGas msg) {
        List<ParticleDataGas> tempList = msg.particleList;

        for (ParticleDataGas data : tempList) {
            //Extract
            if (data.fromData != null) {
                BlockPos fromPos = data.fromData.node();
                BlockEntity fromTE = Minecraft.getInstance().level.getBlockEntity(fromPos);
                if (!(fromTE instanceof LaserNodeBE)) {
                } else {
                    ((LaserNodeBE) fromTE).addParticleDataGas(new ParticleRenderDataGas(data.gasStack, fromPos.relative(Direction.values()[data.fromData.direction()]), data.fromData.direction(), data.fromData.node(), data.fromData.position()));
                }
            }
            if (data.toData != null) {
                //Insert
                BlockPos toPos = data.toData.node();
                BlockEntity toTE = Minecraft.getInstance().level.getBlockEntity(toPos);
                if (!(toTE instanceof LaserNodeBE)) {
                } else {
                    ((LaserNodeBE) toTE).addParticleDataGas(new ParticleRenderDataGas(data.gasStack, data.toData.node(), data.toData.direction(), toPos.relative(Direction.values()[data.toData.direction()]), data.toData.position()));
                }
            }
        }
    }
}
