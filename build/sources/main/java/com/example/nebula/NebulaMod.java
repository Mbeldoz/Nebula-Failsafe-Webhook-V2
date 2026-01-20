package com.example.nebula;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

//1 line simpelr
@Mod(modid = "mbeldoz", name = "Mbeldoz", version = "1.1", clientSideOnly = true)
public class NebulaMod {

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        MbeldozConfig.load();
        MinecraftForge.EVENT_BUS.register(new ChatPoller());
        MinecraftForge.EVENT_BUS.register(new ConfigAutoReloader());
        ClientCommandHandler.instance.registerCommand(new MbeldozCommand());
        System.out.println("[Mbeldoz] Loaded (no mixins).");
    }
}
