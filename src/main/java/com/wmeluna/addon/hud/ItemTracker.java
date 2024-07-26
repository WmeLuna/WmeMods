package com.wmeluna.addon.hud;

import com.wmeluna.addon.WmeMod;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
// import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ItemTracker extends HudElement {
    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public static final HudElementInfo<ItemTracker> INFO = new HudElementInfo<>(WmeMod.HUD_GROUP, "item-tracker", "Tracks Items in your inventory", ItemTracker::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Items to track")
        .defaultValue(Items.BEDROCK)
        .build()
    );
    private final Setting<Boolean> shortNumbers = sgGeneral.add(new BoolSetting.Builder()
        .name("shorten-numbers")
        .description("Make the item count shortened (e.g. 1000 -> 1.0k)")
        .defaultValue(true)
        .build()
    );
    public ItemTracker() {
        super(INFO);

        // calculateSize();
    }

    // private void calculateSize() {
    //     setSize(42.5, 42.5);
    // }
    @Override
    public void render(HudRenderer renderer) {
        setSize(34, 34*items.get().size());

        int i = 0;
        for (Item item : items.get()) {
            List<ItemStack> inventory = new ArrayList<>(mc.player.getInventory().main);
            inventory.add(mc.player.getOffHandStack());
            ItemStack itemStack = new ItemStack(item,getItemCount(item, inventory));

            String countOverride = null; 
            if (shortNumbers.get()) countOverride = WmeMod.abbreviateNumber(itemStack.getCount());
            else countOverride = Integer.toString(itemStack.getCount());

            itemStack.setCount(1);
            renderer.item(itemStack, x, y+(i*34), 2f, true, countOverride);
            i++;
        }
    }

    public int getItemCount (Item item, Iterable<ItemStack> itemList) {
        int count = 0;
        for (ItemStack itemStack : itemList) {
            if (itemStack.getItem() == item) count += itemStack.getCount();
            if (Utils.hasItems(itemStack)){
                ItemStack[] containerItems = new ItemStack[9*3];
                Utils.getItemsInContainerItem(itemStack, containerItems);
                count += getItemCount(item, containerItems);
            }
        }
        return count;
    }
    public int getItemCount(Item item, List<ItemStack> itemList){
        return getItemCount(item, (Iterable<ItemStack>) itemList);
    }
    public int getItemCount(Item item, ItemStack[] itemArray) {
        return getItemCount(item, Arrays.asList(itemArray));
    }
}
