package org.amg.Comandos;

import org.amg.AMGEPlugin;
import org.amg.Utils.UtilsMensajes;
import org.amg.Utils.UtilsMetodos;
import org.amg.Utils.UtilsVender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Vender implements CommandExecutor {
    private final AMGEPlugin plugin;

    public Vender(AMGEPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //Condicione para que se pueda ejecutar
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return false;
        }
        Player jugador = (Player) sender;

        if (args.length==0){ // /vender
            UtilsVender.venderItemMano(jugador);

        }else if (args.length==1 && args[0].equalsIgnoreCase("TODO")){ // /vender todo
            UtilsVender.venderItemInventario(jugador);

        }
        return true;

    }
}
