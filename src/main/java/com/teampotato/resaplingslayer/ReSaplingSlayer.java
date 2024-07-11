package com.teampotato.resaplingslayer;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@Mod(ReSaplingSlayer.ID)
@Mod.EventBusSubscriber(modid = ReSaplingSlayer.ID)
public class ReSaplingSlayer {
    public static final String ID = "resaplingslayer";
    public static final Logger LOGGER = LogManager.getLogger("ReSapingSlayer");
    public static ForgeConfigSpec configSpec;
    public static ForgeConfigSpec.BooleanValue isTradeable, isCurse, isTreasureOnly, isDiscoverable, isAllowedOnBooks;
    public static ForgeConfigSpec.ConfigValue<Double> damagePercent;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("ReSapling Slayer");
        isTradeable = builder.define("isTradeable", true);
        isCurse = builder.define("isCurse", false);
        isTreasureOnly = builder.define("isTreasure", false);
        isDiscoverable = builder.define("canBeFoundInLoot", true);
        isAllowedOnBooks = builder.define("isAllowedOnBooks", true);
        damagePercent = builder.comment("How many durability of the shears will be taken after harvesting sapling").define("harvestDamagePercent", 0.01);
        builder.pop();
        configSpec = builder.build();
    }
    public ReSaplingSlayer() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configSpec);
        ENCHANTMENT_DEFERRED_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final IForgeRegistry<Item> ITEMS = ForgeRegistries.ITEMS;
    public static final DeferredRegister<Enchantment> ENCHANTMENT_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ID);
    public static final RegistryObject<Enchantment> SAPLING_SLAYER = ENCHANTMENT_DEFERRED_REGISTER.register("resapling_slayer", ReSaplingSlayerEnchantment::new);
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static void dropSapling(Level world, BlockPos pos, LeavesBlock leaves) {
        List<Item> saplings = Objects.requireNonNull(ITEMS.tags()).getTag(ItemTags.SAPLINGS).stream().toList();
        ResourceLocation registryName = leaves.getLootTable();
        String nameSpace = Objects.requireNonNull(registryName).getNamespace();
        String replace = registryName.getPath().replace("_leaves", "_sapling").replace("blocks/", "");
        ResourceLocation registryNamee = new ResourceLocation(nameSpace, replace);
        int sapling = saplings.indexOf(ITEMS.getValue(registryNamee));
        if (sapling == -1) {
            LOGGER.error("ReSapingSlayer: Failed to find the corresponding sapling of the leaves");
            LOGGER.error(new ResourceLocation(Objects.requireNonNull(registryName).getNamespace(), registryName.getPath().replace("_leaves", "_sapling")));
            return;
        }
        world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), saplings.get(sapling).getDefaultInstance()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack item = player.getMainHandItem();
        BlockPos pos = event.getPos();
        Block block = player.level().getBlockState(event.getPos()).getBlock();
        if (!item.getEnchantmentTags().toString().contains("resapling_slayer") || !(item.getItem() instanceof ShearsItem) || !(block instanceof LeavesBlock) || event.isCanceled()) return;
        int reduceDamage = (int) (damagePercent.get() * item.getMaxDamage() + item.getDamageValue());
        if(reduceDamage + item.getDamageValue() <= item.getDamageValue()) {
            if (item.isDamageableItem() && !player.level().isClientSide) item.setDamageValue(reduceDamage);
            dropSapling(player.level(), pos, (LeavesBlock) block);
            player.level().setBlock(pos, AIR, 1);
        }
    }
}