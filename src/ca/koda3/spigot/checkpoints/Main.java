package ca.koda3.spigot.checkpoints;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    private static Logger logger;

    public void onEnable() {
        this.logger = this.getLogger();
        this.getServer().getPluginManager().registerEvents(new EventHandlers(this), this);
        this.getServer().getLogger().info("チェックポイントプラグインロードしました");
        this.getCommand("chkpnt").setExecutor(this);
    }

    public void onDisable() {

    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("chkpnt")) {
            Player player = null;
            if(args.length == 1) {
                if (!(sender instanceof BlockCommandSender)) {
                    sender.sendMessage(ChatColor.RED + "［エラー］：このコマンドはコマンドブロックにしか使用できません");
                    return true;
                }
                player = getServer().getPlayer(args[0]);
            }
            if (player != null) {
                String message = setCheckpoint(player);
                sender.sendMessage(message);
                if (!sender.equals(player)) {
                    player.sendMessage(message);
                }
                return true;
            }
        }
        return false;
    }

    public static String setCheckpoint(Player player) {
        if (player.getInventory().firstEmpty() == -1) {
            return ChatColor.RED + "［失敗］ あなたのインベントリには余裕がありません";
        }

        ItemStack magmaCream = null;
        int slot = 0;
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null
                    && is.getType() == Material.MAGMA_CREAM
                    && isWarpCream(is)) {
                magmaCream = is;
                break;
            }
            slot++;
        }

        if (magmaCream == null) {
            slot = 0;
            for (ItemStack is : player.getInventory().getContents()) {
                if (is != null
                        && is.getType() == Material.MAGMA_CREAM) {
                    magmaCream = is;
                    break;
                }
                slot++;
            }
        }

        if (magmaCream == null) {
            return ChatColor.RED + "［失敗］ MagmaCreamを持ってコマンドを再送信してください";
        }

        ItemStack warpCream = magmaCream.clone();
        magmaCream.setAmount(magmaCream.getAmount() - 1);
        player.getInventory().setItem(slot, magmaCream.clone());

        warpCream.setAmount(1);
        ItemMeta im = warpCream.getItemMeta();
        im.setDisplayName("チェックポイントへ戻る");
        Location loc = player.getLocation();
        im.setLore(Arrays.asList(new String[]{
                String.format("ワールド：%s", loc.getWorld().getName()),
                String.format("座標：%.2f,%.2f,%.2f", loc.getX(), loc.getY(), loc.getZ()),
                String.format("角度：%.2f,%.2f", loc.getYaw(), loc.getPitch())
        }));

        warpCream.setItemMeta(im);
        player.getInventory().addItem(warpCream);
        return ChatColor.GREEN + "［成功］ チェックポイント作成しました";
    }

    public static boolean isWarpCream(ItemStack is) {
        if ((is == null)
                || (is.getType() != Material.MAGMA_CREAM)
                || (!is.hasItemMeta())
                || (!is.getItemMeta().getDisplayName().equals("チェックポイントへ戻る")))
            return false;
        return true;
    }
}
