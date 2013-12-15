package fr.minepod.bukkit.ItemsKeeper;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemsKeeper extends JavaPlugin implements Listener{
	
	public static Permission permission = null;
	private ItemStack[] keeps = null;
	
	public void onEnable() {
		getLogger().info("Enabling...");
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("Hooking into permissions...");
		setupPermissions();
		getLogger().info("Done!");
	}
	
	public void onDisable() {
		getLogger().info("Disabled!");
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeathEvent(final PlayerDeathEvent event) {
		if(permission.has(event.getEntity(), "itemskeeper.keep")) {
			int i = 0;
			for(ItemStack tmp : event.getDrops()) {
				if(RandomRange(0, 10) != 1 && tmp != null) {
					keeps[i] = tmp;
					event.getDrops().remove(tmp);
				}
			}
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			    @Override
			    public void run() {
			            event.getEntity().getInventory().setContents(keeps);
			    }
			});
			
			
			final ItemStack[] armor = event.getEntity().getInventory().getArmorContents();
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                        event.getEntity().getInventory().setArmorContents(armor);
                }
            });
			
			for(ItemStack tmp : armor) {
				event.getDrops().remove(tmp);
			}
			
			event.setDroppedExp((int) (event.getDroppedExp() * 0.1));
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                        event.getEntity().setExp((float) (event.getDroppedExp() * 0.9));
                }
            });
		}
	}
	
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
    
	public int RandomRange(int min, int max) {
		return min + (int)(Math.random() * ((max - min) + 1));
	}
}
