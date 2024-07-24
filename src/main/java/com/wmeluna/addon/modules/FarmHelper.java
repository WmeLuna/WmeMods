package com.wmeluna.addon.modules;

import com.wmeluna.addon.WmeMod;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockIterator;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class FarmHelper extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final SettingGroup sgRender = this.settings.createGroup("Render");

    /**
     * Example setting.
     * The {@code name} parameter should be in kebab-case.
     * If you want to access the setting from another class, simply make the setting {@code public}, and use
     * {@link meteordevelopment.meteorclient.systems.modules.Modules#get(Class)} to access the {@link Module} object.
     */
    private final Setting<List<Block>> harvestBlocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("harvest-blocks")
        .description("Which crops to highlight")
        .defaultValue()
        .filter(this::harvestFilter)
        .build()
    );

    private final Setting<SettingColor> color = sgRender.add(new ColorSetting.Builder()
        .name("color")
        .description("The color of the marker.")
        .defaultValue(Color.MAGENTA)
        .build()
    );
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range")
        .description("Auto farm range.")
        .defaultValue(4)
        .min(1)
        .build()
    );

    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public FarmHelper() {
        super(WmeMod.CATEGORY, "farm-helper", "Helper for farmers");
    }


    private List<BlockPos> renderPos = new ArrayList<BlockPos>();
    private final Pool<BlockPos.Mutable> blockPosPool = new Pool<>(BlockPos.Mutable::new);
    private final List<BlockPos.Mutable> blocks = new ArrayList<>();

    // @EventHandler
    // private void onBlockUpdate(BlockUpdateEvent event) {
    //     // int bx = event.pos.getX();
    //     // int by = event.pos.getY();
    //     // int bz = event.pos.getZ();

    //     // if (harvestBlocks.get().contains(event.newState.getBlock()) && isMature(mc.world.getBlockState(event.pos), event.newState.getBlock()) && !renderPos.contains(event.pos)) {
    //     //     renderPos.add(event.pos);
    //     // }
    //     // if (mc.world.getBlockState(event.pos).isAir() && renderPos.contains(event.pos)) {
    //     //     renderPos.remove(event.pos);
    //     // }
    //     BlockPos pos = event.pos;
    //     BlockState state = event.newState;
    //     Block block = state.getBlock();
    //     shouldRender(block, state, pos);

    // }

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        // Render all items in list
        renderPos.forEach(pos -> event.renderer.box(new Box(pos), color.get(), color.get(), ShapeMode.Both, 0));
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        BlockIterator.register(range.get(), range.get(), (pos, state) -> {
            if (mc.player.getEyePos().distanceTo(Vec3d.ofCenter(pos)) <= range.get())
                blocks.add(blockPosPool.get().set(pos));
        });

        BlockIterator.after(() -> {
            blocks.sort(Comparator.comparingDouble(value -> mc.player.getEyePos().distanceTo(Vec3d.ofCenter(value))));

            for (BlockPos pos : blocks) {
                BlockState state = mc.world.getBlockState(pos);
                Block block = state.getBlock();
                shouldRender(block, state, pos);
            }

            for (BlockPos.Mutable blockPos : blocks) blockPosPool.free(blockPos);
            blocks.clear();

        });
    }

    private void shouldRender(Block block, BlockState state, BlockPos pos){
        if (harvestBlocks.get().contains(block) && isMature(state, block) && !renderPos.contains(pos)) {
            renderPos.add(pos);
            // info(pos.toString() + " " + block + " " + state);
        }
        else if (renderPos.contains(pos)) {
            renderPos.remove(pos);
        }
    }
    private boolean harvestFilter(Block block) {
        return block instanceof CropBlock ||
            block == Blocks.PUMPKIN ||
            block == Blocks.MELON ||
            block == Blocks.NETHER_WART ||
            block == Blocks.SWEET_BERRY_BUSH ||
            block == Blocks.COCOA ||
            block == Blocks.PITCHER_CROP ||
            block == Blocks.TORCHFLOWER;
    }
    @SuppressWarnings("static-access")
    private boolean isMature(BlockState state, Block block) {
        if (block instanceof CropBlock cropBlock) {
            return cropBlock.isMature(state);
        } else if (block instanceof CocoaBlock cocoaBlock) {
            return state.get(cocoaBlock.AGE) >= 2;
        } else if (block instanceof StemBlock) {
            return state.get(StemBlock.AGE) == StemBlock.MAX_AGE;
        } else if (block instanceof SweetBerryBushBlock sweetBerryBushBlock) {
            return state.get(sweetBerryBushBlock.AGE) >= 2;
        } else if (block instanceof NetherWartBlock netherWartBlock) {
            return state.get(netherWartBlock.AGE) >= 3;
        } else if (block instanceof PitcherCropBlock pitcherCropBlock) {
            return state.get(pitcherCropBlock.AGE) >= 4;
        }
        return true;
    }
}
