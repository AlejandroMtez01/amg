package org.amg.Comandos;

import org.amg.AMGEPlugin;
import org.amg.Otros.ItemManager;
import org.amg.Utils.UtilsMensajes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GuardarSagrados implements CommandExecutor {
    private final AMGEPlugin plugin;
    private final ItemManager itemManager;
    
    public GuardarSagrados(AMGEPlugin plugin, ItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por jugadores.");
            return true;
        }
        
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (itemManager.guardarItem(player, item)) {
            player.sendMessage(UtilsMensajes.NOMBRE_INFORMAL +"§a¡Item guardado correctamente!");
        } else {
            player.sendMessage(UtilsMensajes.NOMBRE_INFORMAL +"§c¡Error al guardar el item! Asegúrate de estar sosteniendo un item válido.");
        }
        
        return true;
    }
}