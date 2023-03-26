package com.direwolf20.laserio.common.events;

import java.util.ArrayList;
import java.util.List;

import com.direwolf20.laserio.common.network.PacketHandler;
import com.direwolf20.laserio.common.network.packets.PacketNodeParticles;
import com.direwolf20.laserio.common.network.packets.PacketNodeParticlesFluid;
import com.direwolf20.laserio.common.network.packets.PacketNodeParticlesGas;
import com.direwolf20.laserio.util.ParticleData;
import com.direwolf20.laserio.util.ParticleDataFluid;
import com.direwolf20.laserio.util.ParticleDataGas;

import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerTickHandler {
    private static List<ParticleData> particleList = new ArrayList<>();
    private static List<ParticleDataFluid> particleListFluid = new ArrayList<>();
    private static List<ParticleDataGas> particleListGas = new ArrayList<>();
    private static Level serverWorld;

    @SubscribeEvent
    public static void handleTickEndEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (!particleList.isEmpty()) {
                PacketHandler.sendToAll(new PacketNodeParticles(particleList), serverWorld);
                particleList.clear();
            }
            if (!particleListFluid.isEmpty()) {
                PacketHandler.sendToAll(new PacketNodeParticlesFluid(particleListFluid), serverWorld);
                particleListFluid.clear();
            }
            if (!particleListGas.isEmpty()) {
                PacketHandler.sendToAll(new PacketNodeParticlesGas(particleListGas), serverWorld);
                particleListGas.clear();
            }
        }
    }

    public static void addToList(ParticleData particleData, Level world) {
      particleList.add(particleData);
      serverWorld = world;
    }

    public static void addToListFluid(ParticleDataFluid particleData, Level world) {
      particleListFluid.add(particleData);
      serverWorld = world;
    }

    public static void addToListGas(ParticleDataGas particleData, Level world) {
      particleListGas.add(particleData);
      serverWorld = world;
  }
}
