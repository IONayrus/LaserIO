package com.direwolf20.laserio.client.particles.gasparticle;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.direwolf20.laserio.client.particles.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import mekanism.api.chemical.ChemicalUtils;
import mekanism.api.chemical.gas.GasStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GasFlowParticleData implements ParticleOptions {
    private final GasStack gasStack;
    public final double targetX;
    public final double targetY;
    public final double targetZ;
    public final int ticksPerBlock;

    public GasFlowParticleData(GasStack gasStack, double tx, double ty, double tz, int ticks) {
        this.gasStack = gasStack.copy();
        targetX = tx;
        targetY = ty;
        targetZ = tz;
        ticksPerBlock = ticks;
    }

    @Nonnull
    @Override
    public ParticleType<GasFlowParticleData> getType() {
        return ModParticles.GASFLOWPARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        ChemicalUtils.writeChemicalStack(buffer, this.gasStack);
        buffer.writeDouble(this.targetX);
        buffer.writeDouble(this.targetY);
        buffer.writeDouble(this.targetZ);
        buffer.writeInt(this.ticksPerBlock);
    }

    @Nonnull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %s",
                this.getType(), this.targetX, this.targetY, this.targetZ, this.ticksPerBlock);
    }

    @OnlyIn(Dist.CLIENT)
    public GasStack getGasStack() {
        return this.gasStack;
    }

    public static final Deserializer<GasFlowParticleData> DESERIALIZER = new Deserializer<GasFlowParticleData>() {
        @Nonnull
        @Override
        public GasFlowParticleData fromCommand(ParticleType<GasFlowParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            ItemParser.ItemResult itemparser$itemresult = ItemParser.parseForItem(HolderLookup.forRegistry(Registry.ITEM), reader);
            ItemStack itemstack = (new ItemInput(itemparser$itemresult.item(), itemparser$itemresult.nbt())).createItemStack(1, false);

            reader.expect(' ');
            double tx = reader.readDouble();
            reader.expect(' ');
            double ty = reader.readDouble();
            reader.expect(' ');
            double tz = reader.readDouble();
            reader.expect(' ');
            int ticks = reader.readInt();
            return new GasFlowParticleData(GasStack.EMPTY, tx, ty, tz, ticks);
        }

        @Override
        public GasFlowParticleData fromNetwork(ParticleType<GasFlowParticleData> particleTypeIn, FriendlyByteBuf buffer) {
            return new GasFlowParticleData(ChemicalUtils.readGasStack(buffer), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readInt());
        }
    };
}
