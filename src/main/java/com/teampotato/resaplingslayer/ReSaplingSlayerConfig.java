package com.teampotato.resaplingslayer;

import com.google.common.collect.Lists;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.List;

@Mod.EventBusSubscriber(modid = ReSaplingSlayer.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ReSaplingSlayerConfig {
    public static ForgeConfigSpec configSpec;
    public static ForgeConfigSpec.BooleanValue isTradeable, isCurse, isTreasureOnly, isDiscoverable, isAllowedOnBooks;
    public static ForgeConfigSpec.ConfigValue<String> rarity;
    public static ForgeConfigSpec.ConfigValue<Double> damagePercent;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("ReSapling Slayer");
        isTradeable = builder.define("isTradeable", true);
        isCurse = builder.define("isCurse", false);
        isTreasureOnly = builder.define("isTreasure", false);
        isDiscoverable = builder.define("canBeFoundInLoot", true);
        isAllowedOnBooks = builder.define("isAllowedOnBooks", true);
        rarity = builder
                .comment("Allowed value: COMMON, UNCOMMON, RARE, VERY_RARE")
                .define("rarity", "COMMON");
        damagePercent = builder.comment("How many durability of the shears will be taken after harvesting sapling").define("harvestDamagePercent", 0.1);
        builder.pop();
        configSpec = builder.build();
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == configSpec) {
            updateEnchantmentRarity();
        }
    }

    private static void updateEnchantmentRarity() {
        // 根据配置更新附魔品质
        Enchantment.Rarity Rarity = switch (rarity.get().toUpperCase()) {
            case "COMMON" -> Enchantment.Rarity.COMMON;
            case "RARE" -> Enchantment.Rarity.RARE;
            case "VERY_RARE" -> Enchantment.Rarity.VERY_RARE;
            default -> Enchantment.Rarity.UNCOMMON;
        };

        // 更新附魔实例的品质字段
        try {
            Field rarityField = ReSaplingSlayerEnchantment.class.getDeclaredField("rarity");
            rarityField.setAccessible(true);
            rarityField.set(null, Rarity);
            ReSaplingSlayer.LOGGER.info(ReSaplingSlayerEnchantment.rarity.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ReSaplingSlayer.LOGGER.error("Can't switch the rarity");
        }
    }
}
