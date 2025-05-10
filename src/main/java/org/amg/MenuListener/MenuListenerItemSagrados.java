package org.amg.MenuListener;

import org.amg.AMGEPlugin;
import org.amg.Otros.ItemManager;
import org.amg.Utils.UtilsMensajes;
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
import java.util.List;
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

            //Control de Clicks
            if (event.isLeftClick()){
                List<String> lore = clicked.getItemMeta().getLore();
                String nombrePropietario = lore.get(1).split(":")[1];

                if (player.hasPermission("amg.sagrados.give")){
                    player.getInventory().addItem(clicked);
                    player.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Obteniendo el §6ITEM SAGRADO§f.");

                }else{
                    player.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Cuentan los DIOSES que este elemento fue creado por "+nombrePropietario+"§f.");

                }



            }else if (event.isRightClick()){
                if (itemManager.eliminarItemPorClick(player.getUniqueId(),clicked,player)) {
                    player.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Eliminando item de los §6§lITEMS SAGRADOS§f.");

                }else{
                    player.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"No puedes eliminar un §6§lITEM SAGRADO§f si no eres el propietario.");
                }

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