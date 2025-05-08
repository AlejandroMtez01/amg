package org.amg.Comandos;

import org.amg.AMGEPlugin;
import org.amg.Menu.MenuEliminarEncantamientos;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EliminarEncantamiento implements CommandExecutor {
    private final AMGEPlugin plugin;

    public EliminarEncantamiento(AMGEPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return false;
        }

        Player jugador = (Player) sender;
        ItemStack item = jugador.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            jugador.sendMessage("¡Debes tener un item en la mano!");
            return false;
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasEnchants()) {
            jugador.sendMessage("¡Este item no tiene encantamientos!");
            return false;
        }

        MenuEliminarEncantamientos.mostrarMenu(jugador, item);
        return true;
    }
}

