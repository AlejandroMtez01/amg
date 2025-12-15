package org.amg.Comandos;

import org.amg.AMGEPlugin;
import org.amg.Utils.UtilsChat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class compartirInformacionItem implements CommandExecutor {
    private final AMGEPlugin plugin;

    public compartirInformacionItem(AMGEPlugin plugin) {
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

        UtilsChat.broadcastItemSpigot(jugador);
        return true;

    }
}
