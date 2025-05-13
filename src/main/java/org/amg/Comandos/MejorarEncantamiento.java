package org.amg.Comandos;

import org.amg.AMGEPlugin;
import org.amg.Menu.MenuEliminarEncantamientos;
import org.amg.Menu.MenuMejorarEncantamientos;
import org.amg.Utils.UtilsEncantamientos;
import org.amg.Utils.UtilsMensajes;
import org.amg.Utils.UtilsMetodos;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MejorarEncantamiento implements CommandExecutor {
    private final AMGEPlugin plugin;

    public MejorarEncantamiento(AMGEPlugin plugin) {
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

        if (item == null || item.getType().isAir() ) {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"¡Debes tener un item en la mano!");
            return false;
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasEnchants()) {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"¡Este item no tiene encantamientos!.");
            return false;
        }
        if (item.getType().equals(Material.ENCHANTED_BOOK)) {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"No se puede realizar esta acción con un libro encantado.");
            return false;
        }
        if (! (UtilsEncantamientos.tieneEncantamientosTOP(item) || UtilsEncantamientos.tieneEncantamientosMaximosVanilla(item))){
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"¡Este item no tiene encantamientos suficientemente elevados (Para poder mejorar un encantamiento debe estar como mínimo al nivel máximo).");
            return false;
        }

        MenuMejorarEncantamientos.mostrarMenu(jugador, item);
        return true;
    }
}

