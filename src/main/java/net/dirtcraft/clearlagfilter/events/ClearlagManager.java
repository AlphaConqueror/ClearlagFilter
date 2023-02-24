package net.dirtcraft.clearlagfilter.events;

import me.minebuilders.clearlag.events.EntityRemoveEvent;
import net.dirtcraft.clearlagfilter.Config;
import net.dirtcraft.clearlagfilter.utils.Logger;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ClearlagManager extends DirtListener {

    public ClearlagManager(final JavaPlugin javaPlugin) {
        super(javaPlugin, "ClearlagManager");
    }

    @EventHandler
    public void onEntityRemove(final EntityRemoveEvent event) {
        final int beforeSize = event.getEntityList().size();

        for (final String filter : Config.getFilter()) {
            event.getEntityList().removeIf(entity -> isEntityFiltered(entity, filter));
        }

        final int filtered = beforeSize - event.getEntityList().size();

        if (filtered > 0) {
            Logger.info("Prevented %d filtered entities from being removed.",
                    filtered);
        }
    }

    private boolean isEntityFiltered(final Entity entity, final String filter) {
        final String entityType = entity.getType().name();

        return filter.equalsIgnoreCase(entityType)
                || filter.equalsIgnoreCase(entityType.replace(' ', '_'));
    }
}
