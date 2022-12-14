package com.github.audio;

import com.github.audio.client.audio.exec.ExecRegistryHandler;
import com.github.audio.client.config.Config;
import com.github.audio.item.ItemRegisterHandler;
import com.github.audio.keybind.KeyBinds;
import com.github.audio.sound.AudioSoundRegistryHandler;
import com.github.audio.util.Utils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Utils.MOD_ID)
public class Audio
{
    private static final Logger LOGGER = LogManager.getLogger();

    public Audio() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext context = ModLoadingContext.get();

        ItemRegisterHandler.register(eventBus);
        AudioSoundRegistryHandler.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);
        eventBus.addListener(this::doClientStuff);

        context.registerConfig(ModConfig.Type.COMMON, Config.AUDIO_CONFIG);
        context.registerExtensionPoint(
                ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY,
                        (a, b) -> true));

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        if (Env.getEnv().equals(Env.RUN_CLIENT)) {
            ExecRegistryHandler.registry(MinecraftForge.EVENT_BUS);
        }
    }

    public void setup(final FMLCommonSetupEvent event) {
    }

    public void doClientStuff(final FMLClientSetupEvent event) {
        KeyBinds.register();
    }

    public void enqueueIMC(final InterModEnqueueEvent event) {
    }

    private void processIMC(final InterModProcessEvent event) {
    }

    public void onServerStarting(FMLServerStartingEvent event) {
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static void info(String msg) {
        LOGGER.info(msg);
    }

    public static void warn(String msg) {
        LOGGER.warn(msg);
    }




}
