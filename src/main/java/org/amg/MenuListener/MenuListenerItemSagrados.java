package org.amg.MenuListener;

import org.amg.AMGEPlugin;
import org.amg.Otros.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuListenerItemSagrados implements Listener {
    private final AMGEPlugin plugin;
    private final ItemManager itemManager;
    private final Map<UUID, ItemStack> itemsSeleccionados;
    
    public MenuListenerItemSagrados(AMGEPlugin plugin, ItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
        this.itemsSeleccionados = new HashMap<>();
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        String title = event.getView().getTitle();
        
        if (!title.startsWith(ChatColor.translateAlternateColorCodes('&', "&6Items Especiales &7- Página "))) {
            return;
        }
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        
        // Manejar clic en botones de paginación
        if (clicked.getType() == Material.ARROW) {
            String displayName = clicked.getItemMeta().getDisplayName();
            int currentPage = Integer.parseInt(title.split("Página ")[1].split("/")[0]);
            
            if (displayName.contains("anterior")) {
                itemManager.abrirMenuItems(player, currentPage - 1);
            } else if (displayName.contains("siguiente")) {
                itemManager.abrirMenuItems(player, currentPage + 1);
            }
        }
        // Manejar clic en botón de cerrar
        else if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
        }
        // Manejar clic en un item especial
        else {
            // Verificar si el jugador está en modo renovación
            if (player.hasMetadata("modo_renovacion")) {
                ItemStack itemEnMano = player.getInventory().getItemInMainHand();
                if (itemEnMano != null && !itemEnMano.getType().isAir()) {
                    Map<String, String> info = itemManager.obtenerInfoJugadorPorItem(clicked);
                    if (info != null && info.get("uuid_jugador").equals(player.getUniqueId().toString())) {
                        if (itemManager.renovarItem(player, clicked, itemEnMano)) {
                            player.sendMessage(ChatColor.GREEN + "¡Item renovado correctamente!");
                        } else {
                            player.sendMessage(ChatColor.RED + "¡Error al renovar el item!");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "¡Solo puedes renovar tus propios items!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "¡Debes sostener un item en la mano para renovar!");
                }
                player.removeMetadata("modo_renovacion", plugin);
                player.closeInventory();
            }
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (player.hasMetadata("modo_renovacion")) {
                player.removeMetadata("modo_renovacion", plugin);
            }
        }
    }
}