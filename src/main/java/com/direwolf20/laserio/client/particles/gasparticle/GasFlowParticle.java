package com.direwolf20.laserio.client.particles.gasparticle;

import java.util.Random;

import mekanism.api.chemical.gas.GasStack;
import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class GasFlowParticle extends BreakingItemParticle {

    private double targetX, targetY, targetZ;
    Random random = new Random();

    public GasFlowParticle(ClientLevel world, double x, double y, double z, double targetX, double targetY, double targetZ, GasStack gasStack, int ticksPerBlock) {
        super(world, x, y, z, ItemStack.EMPTY);
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        Vec3 target = new Vec3(targetX, targetY, targetZ);
        Vec3 source = new Vec3(this.x, this.y, this.z);
        Vec3 path = target.subtract(source).normalize().multiply(1, 1, 1);
        this.gravity = 0.0f;
        double distance = target.distanceTo(source);
        this.hasPhysics = false;
        float minSize = 0.15f;
        float maxSize = 0.25f;
        float partSize = minSize + random.nextFloat() * (maxSize - minSize);
        float speedModifier = (1f - 0.5f) * (partSize - minSize) / (maxSize - minSize) + 0.25f;
        float speedAdjust = ticksPerBlock * (1 / speedModifier);
        this.xd += path.x / speedAdjust;
        this.yd += path.y / speedAdjust;
        this.zd += path.z / speedAdjust;
        this.lifetime = (int) (distance * speedAdjust);
        this.scale(partSize);
        this.setSprite(MekanismRenderer.getChemicalTexture(gasStack.getType()));        
        int i = gasStack.getChemicalTint();
        this.rCol *= (float) (i >> 16 & 255) / 255.0F;
        this.gCol *= (float) (i >> 8 & 255) / 255.0F;
        this.bCol *= (float) (i & 255) / 255.0F;
    }


    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.04D * (double) this.gravity;
            this.move(this.xd, this.yd, this.zd);
        }
    }

    public static ParticleProvider<GasFlowParticleData> FACTORY =
            (data, world, x, y, z, xSpeed, ySpeed, zSpeed) ->
                    new GasFlowParticle(world, x, y, z, data.targetX, data.targetY, data.targetZ, data.getGasStack(), data.ticksPerBlock);
}
