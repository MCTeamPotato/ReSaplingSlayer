package com.teampotato.resaplingslayer;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import static com.teampotato.resaplingslayer.ReSaplingSlayer.*;

@SuppressWarnings("NullableProblems")
public class ReSaplingSlayerEnchantment extends Enchantment {

    private static final EquipmentSlot[] MAIN_HAND = new EquipmentSlot[]{EquipmentSlot.MAINHAND};
    private static final EnchantmentCategory ENCHANTMENT_TYPE = EnchantmentCategory.create(ID + ":on_shear", item -> item instanceof ShearsItem);

    public ReSaplingSlayerEnchantment() {
        super(Rarity.COMMON, ENCHANTMENT_TYPE, MAIN_HAND);
    }

    public boolean canEnchant(ItemStack pStack) {
        return canApplyAtEnchantingTable(pStack) && pStack.getItem() instanceof ShearsItem;
    }

    public boolean isTreasureOnly() {
        return isTreasureOnly.get();
    }

    public boolean isCurse() {
        return isCurse.get();
    }

    public boolean isTradeable() {
        return isTradeable.get();
    }

    public boolean isDiscoverable() {
        return isDiscoverable.get();
    }

    public boolean isAllowedOnBooks() {
        return isAllowedOnBooks.get();
    }
}