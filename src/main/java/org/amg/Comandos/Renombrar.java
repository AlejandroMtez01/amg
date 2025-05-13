package org.amg.Comandos;

import org.amg.AMGEPlugin;
import org.amg.Otros.ItemManager;
import org.amg.Utils.UtilsEncantamientos;
import org.amg.Utils.UtilsMensajes;
import org.amg.Utils.UtilsMetodos;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Renombrar implements CommandExecutor {
    private final ItemManager itemManager;
    private final AMGEPlugin plugin;

    public Renombrar(AMGEPlugin plugin, ItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //Condicione para que se pueda ejecutar
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return false;
        }
        Player jugador = (Player) sender;
        ItemStack itemMano = jugador.getInventory().getItemInMainHand();
        ItemStack itemViejo = itemMano.clone();

        if (itemMano == null || itemMano.getType().isAir()) {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"¡Debes tener un item en la mano para poder renombrar el item!");
            return false;
        }
        if (!UtilsEncantamientos.tieneEncantamientosTOP(itemMano) && !UtilsEncantamientos.tieneEcantamientosEspeciales(itemMano)){
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Para poder renombrar un item tiene que tener (encantamientos ¡por encima de lo normal!) o (encantamientos Especiales)");
            return false;
        }
        if (args.length==0){
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Uso: /renombrar <nuevo nombre>");
            return false;
        }
        //Unir argumentso para formar un nombre completo
        String nuevoNombre = String.join(" ",args);
        nuevoNombre = ChatColor.translateAlternateColorCodes('&',nuevoNombre);

        ItemMeta meta = itemMano.getItemMeta();
        meta.setDisplayName(nuevoNombre);
        itemMano.setItemMeta(meta);

        jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"¡Objeto renombrado correctamente!" +" ["+nuevoNombre+"§f]");

        itemManager.renovarItem(jugador,itemViejo,itemMano);

        return true;

    }
}
