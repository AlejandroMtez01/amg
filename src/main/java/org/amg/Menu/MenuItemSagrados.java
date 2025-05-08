package org.amg.Menu;

import org.amg.AMGEPlugin;
import org.amg.Otros.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Array;
import java.util.*;

public class MenuItemSagrados {
    private final Plugin plugin;
    private final ItemManager itemManager;
    private static final int ITEMS_POR_PAGINA = 45;
    
    public MenuItemSagrados(Plugin plugin, ItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
    }
    
    public void abrir(Player player, int pagina) {
        List<ItemStack> items = itemManager.obtenerTodosLosItems();
        int totalPaginas = (int) Math.ceil((double) items.size() / ITEMS_POR_PAGINA);
        
        if (pagina < 1) pagina = 1;
        if (pagina > totalPaginas) pagina = totalPaginas;
        
        Inventory inv = Bukkit.createInventory(null, 54, 
            ChatColor.translateAlternateColorCodes('&', "&6Items Especiales &7- Página " + pagina + "/" + totalPaginas));
        
        // Agregar items a la página correspondiente
        int inicio = (pagina - 1) * ITEMS_POR_PAGINA;
        int fin = Math.min(inicio + ITEMS_POR_PAGINA, items.size());
        
        for (int i = inicio; i < fin; i++) {
            ItemStack item = items.get(i);

            ItemMeta meta = item.getItemMeta();

            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;

                // Obtener la durabilidad máxima del material
                int maxDurability = item.getType().getMaxDurability();

                // Establecer el daño (0 = completamente nuevo)
                damageable.setDamage(0); // Le quedan 100 puntos de durabilidad

                item.setItemMeta(meta);
            }


            ItemMeta itemMeta = item.getItemMeta();
            List<String> lista = new ArrayList<>();
            lista.add("");
            lista.add("§fPropiedad de: §cPepito");
            itemMeta.setLore(lista);
            item.setItemMeta(itemMeta);
            inv.setItem(i - inicio, items.get(i));
        }
        
        // Botón de página anterior
        if (pagina > 1) {
            ItemStack anterior = new ItemStack(Material.ARROW);
            ItemMeta metaAnterior = anterior.getItemMeta();
            metaAnterior.setDisplayName(ChatColor.YELLOW + "Página anterior");
            metaAnterior.setLore(Collections.singletonList(ChatColor.GRAY + "Página " + (pagina - 1)));
            anterior.setItemMeta(metaAnterior);
            inv.setItem(45, anterior);
        }
        
        // Botón de página siguiente
        if (pagina < totalPaginas) {
            ItemStack siguiente = new ItemStack(Material.ARROW);
            ItemMeta metaSiguiente = siguiente.getItemMeta();
            metaSiguiente.setDisplayName(ChatColor.YELLOW + "Página siguiente");
            metaSiguiente.setLore(Collections.singletonList(ChatColor.GRAY + "Página " + (pagina + 1)));
            siguiente.setItemMeta(metaSiguiente);
            inv.setItem(53, siguiente);
        }
        
        // Botón de cerrar
        ItemStack cerrar = new ItemStack(Material.BARRIER);
        ItemMeta metaCerrar = cerrar.getItemMeta();
        metaCerrar.setDisplayName(ChatColor.RED + "Cerrar menú");
        cerrar.setItemMeta(metaCerrar);
        inv.setItem(49, cerrar);
        
        player.openInventory(inv);
    }
}