package ca.koda3.spigot.checkpoints;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EventHandlers implements Listener {
    private Main plugin;
    public EventHandlers(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRightClickMagmaCream(PlayerInteractEvent event) { // MagmaCreamを持ちながら右クリックときに
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && plugin.isWarpCream(event.getPlayer().getInventory().getItemInMainHand())) {
            ItemStack warpCream = event.getPlayer().getInventory().getItemInMainHand();
            ItemMeta im = warpCream.getItemMeta();
            String world = im.getLore().get(0).replace("ワールド：", "");
            String[] coords = im.getLore().get(1).replace("座標：", "").split(",");
            String[] angle = im.getLore().get(2).replace("角度：", "").split(",");
            try {
                Location loc = new Location(
                        plugin.getServer().getWorld(world),
                        Double.parseDouble(coords[0]),
                        Double.parseDouble(coords[1]),
                        Double.parseDouble(coords[2]),
                        Float.parseFloat(angle[0]),
                        Float.parseFloat(angle[1])
                );
                event.getPlayer().teleport(loc);
                event.getPlayer().sendMessage(ChatColor.GREEN + "［成功］ ワープされました");
            } catch (Exception e) {
                // アイテムが偽物なの可能性高い
            }
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDrop(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player
                && ((Player) event.getEntity()).getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }
}
