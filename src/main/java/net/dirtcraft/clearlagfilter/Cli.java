package net.dirtcraft.clearlagfilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.dirtcraft.clearlagfilter.events.ClearlagManager;
import net.dirtcraft.clearlagfilter.events.DirtListener;
import net.dirtcraft.clearlagfilter.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Cli extends JavaPlugin implements Listener {

    private final List<DirtListener> listeners = new ArrayList<>();

    @Override
    public void onEnable() {
        // init config
        try {
            Config.copyDataIfNotExistent();
            Config.read();
        } catch (final IOException e) {
            Logger.error("Caught exception: %s\nDisabling plugin.", e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.registerEvents();
    }

    @Override
    public void onDisable() {
        // disable listeners
        this.listeners.forEach(DirtListener::onDisable);
        this.listeners.forEach(DirtListener::unregister);
        this.listeners.clear();
    }

    private void registerEvents() {
        this.listeners.add(new ClearlagManager(this));

        // register all listeners
        this.listeners.forEach(DirtListener::register);
    }
}
