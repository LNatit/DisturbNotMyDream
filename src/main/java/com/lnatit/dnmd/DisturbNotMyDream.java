package com.lnatit.dnmd;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DisturbNotMyDream.MODID)
public class DisturbNotMyDream
{
    public static final String MODID = "dnmd";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DisturbNotMyDream(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(Config::onLoad);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @Mod(value = DisturbNotMyDream.MODID, dist = Dist.DEDICATED_SERVER)
    public static class Server
    {
        // TODO make it configurable
        public static final Supplier<MobEffectInstance> GRUMPY = () -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 1);

        public Server() {
            NeoForge.EVENT_BUS.addListener(Server::onSleepDisturbed);
        }

        public static void addGrumpy(ServerPlayer player)
        {
            player.addEffect(GRUMPY.get());
        }

        public static void onSleepDisturbed(LivingIncomingDamageEvent event) {
            if (event.getEntity() instanceof ServerPlayer player && player.isSleeping())
            {
                addGrumpy(player);
            }
        }
    }

}
