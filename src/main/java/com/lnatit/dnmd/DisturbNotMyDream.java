package com.lnatit.dnmd;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DisturbNotMyDream.MODID)
public class DisturbNotMyDream
{
    public static final String MODID = "dnmd";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DisturbNotMyDream(IEventBus modEventBus, ModContainer modContainer)
    {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(Config::onLoad);
        NeoForge.EVENT_BUS.addListener(DisturbNotMyDream::onSleepDisturbed);
    }

    public static void addGrumpy(ServerPlayer player)
    {
        Config.GRUMPY.forEach(supplier -> player.addEffect(supplier.get()));
    }

    public static void onSleepDisturbed(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.isSleeping())
        {
            addGrumpy(player);
        }
    }

    public static class Config {
        public static List<Supplier<MobEffectInstance>> GRUMPY = new ArrayList<>();

        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        private static final ModConfigSpec.ConfigValue<String> GRUMPY_EFFECTS = BUILDER.define(
                "grumpyEffects",
                "minecraft:strength,600,2;"
        );

        static final ModConfigSpec SPEC = BUILDER.build();

        static void onLoad(final ModConfigEvent event) {
            String effects = GRUMPY_EFFECTS.get();
            for (String effectDef : effects.split(";")) {
                try {
                    String[] str = effectDef.split(",", 3);
                    str = Arrays.stream(str).map(String::trim).toArray(String[]::new);
                    Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(str[0]));
                    int duration = Integer.parseInt(str[1]);
                    int amplifier = Integer.parseInt(str[2]) - 1;
                    if (effect.isPresent()) {
                        GRUMPY.add(() -> new MobEffectInstance(effect.get(), duration, amplifier));
                    } else {
                        LOGGER.error("Invalid effect definition: [{}] effect does not exist!", effectDef);
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error("Invalid effect definition: [{}] duration or amplifier fail to parse", effectDef);
                }
            }
        }
    }
}
