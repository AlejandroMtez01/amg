package org.amg.Otros;

import org.amg.AMGEPlugin;
import org.amg.FileData.FileDataManager;
import org.amg.Menu.MenuItemSagrados;
import org.amg.Utils.UtilsItemMeta;
import org.amg.Utils.UtilsMensajes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
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
    public boolean renovarItem(Player player, ItemStack itemViejo, ItemStack itemNuevo){
        boolean seHaGuardado = false;
        //if (eliminarItemPorClick(player.getUniqueId(),itemViejo,player)){
        if (eliminarItem(itemViejo)){
        player.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Eliminando antiguo §6§lITEM SAGRADO");
        }
        seHaGuardado = guardarItem(player,itemNuevo);
        if (seHaGuardado){
            player.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Creando nuevo §6§lITEM SAGRADO");

        }
        return (seHaGuardado);
    }

    public void recargarDatos() {
        // 1. Si usas un archivo personalizado, recárgalo primero.
        // Si usas config.yml por defecto: plugin.reloadConfig();
        // Si usas una clase FileDataManager (como vi en tus imports), úsala:
        fileDataManager.recargarArchivo();



        Bukkit.getLogger().info("[AMGE] Los items sagrados han sido recargados desde el disco.");
    }
    public boolean guardarItem(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }
        
        // Asegurarse de que el item tenga un nombre especial
        /*ItemMeta meta = item.getItemMeta();
        if (meta != null && !meta.hasDisplayName()) {
            //meta.setDisplayName("§6Item Especial de " + player.getName());
            //item.setItemMeta(meta);
            return false; //Únicamente se podrán añadir aquellos items que contengan nombres personalizados
        }*/
        
        return fileDataManager.guardarItem(player.getUniqueId(), player.getName(), item);
    }
    
    /*public boolean renovarItem(Player player, ItemStack itemViejo, ItemStack itemNuevo) {
        if (itemViejo == null || itemNuevo == null || 
            itemViejo.getType().isAir() || itemNuevo.getType().isAir()) {
            return false;
        }
        
        return fileDataManager.actualizarItem(player.getUniqueId(), itemViejo, itemNuevo);
    }*/
    
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
        ItemStack itemCopia = item.clone();
        UtilsItemMeta.mostrarItemSinUso(itemCopia);
        return fileDataManager.eliminarItemIgnorandoLore(jugadorUUID, itemCopia,jugador);
    }
    public Long obtenerFechaEnMSItem(ItemStack item){
        return fileDataManager.obtenerFechaEnMSItem(item);
    }
    public boolean eliminarItem(ItemStack item){
        return fileDataManager.eliminarItemClick(item);
    }
}