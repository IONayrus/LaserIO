package com.direwolf20.laserio.common.addons.mekanism;

import mekanism.api.chemical.gas.IGasHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class MekanismCapabilities {
  public static final Capability<IGasHandler> GAS_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
}
