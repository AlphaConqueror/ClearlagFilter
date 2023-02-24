package net.dirtcraft.clearlagfilter.events;

import net.dirtcraft.clearlagfilter.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class DirtListener implements Listener {

    private final JavaPlugin javaPlugin;
    private final String name;

    public DirtListener(final JavaPlugin javaPlugin, final String name) {
        this.javaPlugin = javaPlugin;
        this.name = name;
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, this.javaPlugin);
        Logger.info("Registered listener '%s'.", this.name);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        Logger.info("Unregistered listener '%s'.", this.name);
    }
}
