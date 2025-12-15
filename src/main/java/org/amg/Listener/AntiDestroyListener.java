package org.amg.Listener;

import org.amg.AMGEPlugin;
import org.amg.Otros.ItemManager;
import org.amg.Utils.UtilsMensajes;
import org.amg.Utils.UtilsPrecios;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class AntiDestroyListener implements Listener {

    private final AMGEPlugin plugin;

    public AntiDestroyListener(AMGEPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void alRomperBloque(BlockBreakEvent event) {

        // Verificamos si el bloque es un Spawner
        if (event.getBlock().getType() == Material.SPAWNER) {

            // (Opcional) Permitir que los administradores sí puedan romperlo
            // Si el jugador TIENE permiso, no hacemos nada (dejamos que lo rompa)
            if (event.getPlayer().hasPermission("nospawnerbreak.bypass")) {
                return;
            }

            // Si es un spawner y NO tiene permiso:
            event.setCancelled(true); // Cancelamos la acción

            // Enviamos un mensaje de error al jugador
            event.getPlayer().sendMessage(ChatColor.RED + "¡No tienes permiso para romper spawners!");
        }
    }
}
