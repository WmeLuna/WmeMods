package com.wmeluna.addon;

import com.wmeluna.addon.commands.CommandExample;
import com.wmeluna.addon.hud.HudExample;
import com.wmeluna.addon.modules.FarmHelper;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class WmeMod extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("WmeMod");
    public static final HudGroup HUD_GROUP = new HudGroup("WmeMod");

    @Override
    public void onInitialize() {
        LOG.info("Initializing WmeMod");

        // Modules
        Modules.get().add(new FarmHelper());

        // Commands
        Commands.add(new CommandExample());

        // HUD
        Hud.get().register(HudExample.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.wmeluna.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("WmeLuna", "WmeMods");
    }
}
