package com.direwolf20.laserio.client.particles.gasparticle;

import com.mojang.serialization.Codec;

import net.minecraft.core.particles.ParticleType;

public class GasFlowParticleType extends ParticleType<GasFlowParticleData> {
    public GasFlowParticleType() {
        super(false, GasFlowParticleData.DESERIALIZER);
    }

    @Override
    public Codec<GasFlowParticleData> codec() {
        return null;
    }

}
