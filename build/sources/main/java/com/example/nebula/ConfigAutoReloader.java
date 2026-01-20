package com.example.nebula;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ConfigAutoReloader {
    @SubscribeEvent
    public void onJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        MbeldozConfig.load();
        System.out.println("[Mbeldoz] Config Reload !");
    }
}
