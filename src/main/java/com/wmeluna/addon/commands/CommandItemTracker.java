package com.wmeluna.addon.commands;

import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.*;
@SuppressWarnings("unused") // vscode doesnt realize that SINGLE_SUCCESS is from that


//!TODO make this edit the settings of ItemTracker, and also have infinite arguments, simular to how clientcommands does /cenchant
/**
 * The Meteor Client command API uses the <a href="https://github.com/Mojang/brigadier">same command system as Minecraft does</a>.
 */
public class CommandItemTracker extends Command {
    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public CommandItemTracker() {
        super("item-tracker", "Sets the items to show on the HUD element");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Usage: .itemtracker <item>");
            return SINGLE_SUCCESS;
        });

        builder.then(argument("item", ItemStackArgumentType.itemStack(REGISTRY_ACCESS)).executes(context -> {
            ItemStack argument = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false);
            info("Set to: " + argument.getItem().toString());
            return SINGLE_SUCCESS;
        }));
    }
}
