package org.amg.Comandos;

import org.amg.AMGEPlugin;
import org.amg.Utils.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class reparar implements CommandExecutor {
    private final AMGEPlugin plugin;

    public reparar(AMGEPlugin plugin) {
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
        ItemStack item = jugador.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL + "¡Debes tener un item en la mano!");
            return false;
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasEnchants()) {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL + "¡Este item no tiene encantamientos!");
            return false;
        }
        if (!UtilsEncantamientos.tieneEncantamientosMaximosVanillaOSuperiores(item)) {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL  + "¡Para poder reparar un items por comandos, se tiene que tratar de un elemento §dSAGRADO§7!");
            return false;
        }

        //Obtener durabilidad
        ItemMeta meta = item.getItemMeta();
        Damageable damageable = (Damageable) meta;

        int damage = damageable.getDamage(); // Daño acumulado
        if (item.getType().getMaxDurability() <= 0 || damage == 0) {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL  + "¡Este item no necesita ser reparado!");
            return false;
        }


        if (AMGEPlugin.economia.has(jugador, 1)) {

            UtilsMetodosEconomicos.retirarDinero(jugador, 1);

            UtilsMetodos.repararItem(jugador.getInventory().getItemInMainHand(), jugador);
            UtilsChat.notificacionReparacion(jugador);
        }


        return true;

    }
}
