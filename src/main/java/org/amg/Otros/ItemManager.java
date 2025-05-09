package org.amg.Otros;

import org.amg.AMGEPlugin;
import org.amg.FileData.FileDataManager;
import org.amg.Menu.MenuItemSagrados;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemManager {
    private final Plugin plugin;
    private final FileDataManager fileDataManager;
    
    public ItemManager(Plugin plugin, FileDataManager fileDataManager) {
        this.plugin = plugin;
        this.fileDataManager = fileDataManager;
    }
    
    public boolean guardarItem(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        
        // Asegurarse de que el item tenga un nombre especial
        ItemMeta meta = item.getItemMeta();
        if (meta != null && !meta.hasDisplayName()) {
            //meta.setDisplayName("§6Item Especial de " + player.getName());
            //item.setItemMeta(meta);
            return false; //Únicamente se podrán añadir aquellos items que contengan nombres personalizados
        }
        
        return fileDataManager.guardarItem(player.getUniqueId(), player.getName(), item);
    }
    
    public boolean renovarItem(Player player, ItemStack itemViejo, ItemStack itemNuevo) {
        if (itemViejo == null || itemNuevo == null || 
            itemViejo.getType().isAir() || itemNuevo.getType().isAir()) {
            return false;
        }
        
        return fileDataManager.actualizarItem(player.getUniqueId(), itemViejo, itemNuevo);
    }
    
    public List<ItemStack> obtenerItems(UUID jugadorUUID) {
        return fileDataManager.obtenerItems(jugadorUUID);
    }
    
    public List<ItemStack> obtenerTodosLosItems() {
        return fileDataManager.obtenerTodosLosItems();
    }
    
    public Map<String, String> obtenerInfoJugadorPorItem(ItemStack item) {
        return fileDataManager.obtenerInfoJugadorPorItem(item);
    }
    
    public void abrirMenuItems(Player player, int pagina) {
        new MenuItemSagrados(plugin, this).abrir(player, pagina);
    }
    public boolean eliminarItemPorClick(UUID jugadorUUID, ItemStack item, Player jugador) {
        return fileDataManager.eliminarItemIgnorandoLore(jugadorUUID, item,jugador);
    }
    public Long obtenerFechaEnMSItem(ItemStack item){
        return fileDataManager.obtenerFechaEnMSItem(item);
    }
}