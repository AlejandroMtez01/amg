package org.amg.Comandos;

import org.amg.AMGEPlugin;
import org.amg.Otros.ItemManager;
import org.amg.Utils.UtilsMensajes;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Sagrados implements CommandExecutor {
    private final AMGEPlugin plugin;
    private final ItemManager itemManager;
    
    public Sagrados(AMGEPlugin plugin, ItemManager itemManager) {
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
        int pagina = 1;
        
        if (args.length > 0) {
            try {
                pagina = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"§c¡Número de página inválido!");
                return true;
            }
        }
        if (itemManager.obtenerTodosLosItems().size()==0){
            player.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"§c¡No existen elementos sagrados!");
            return true;
        }
        
        itemManager.abrirMenuItems(player, pagina);
        return true;
    }
}