package org.amg.Menu;

import org.amg.AMGEPlugin;
import org.amg.FileData.FileDataManager;
import org.amg.Otros.ItemManager;
import org.amg.Utils.UtilsFechas;
import org.amg.Utils.UtilsItemMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;

public class MenuItemSagrados {
    private final Plugin plugin;
    private final ItemManager itemManager;
    private static final int ITEMS_POR_PAGINA = 54;
    private static final int ITEMS_PARA_MOSTRAR = 28;

    public MenuItemSagrados(Plugin plugin, ItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
    }

    public void abrir(Player player, int pagina) {
        List<ItemStack> items = itemManager.obtenerTodosLosItems();
        int totalPaginas = (int) Math.ceil((double) items.size() / ITEMS_PARA_MOSTRAR);

        if (pagina < 1) pagina = 1;
        if (pagina > totalPaginas) pagina = totalPaginas;

        Inventory inv = Bukkit.createInventory(null, 54,
                ChatColor.translateAlternateColorCodes('&', "&6Items Especiales &7- Página " + pagina + "/" + totalPaginas));

        // Agregar items a la página correspondiente
        int inicio = (pagina - 1) * ITEMS_PARA_MOSTRAR;
        int fin = Math.min(inicio + ITEMS_PARA_MOSTRAR, items.size());


        //Cristales //TO:DO
        ItemStack cristal = crearCristalEncantado();


        for (int i = 0; i < ITEMS_POR_PAGINA; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || (i + 1) % 9 == 0) {

                if (pagina > 1 && i==45) {
                    ItemStack anterior = new ItemStack(Material.ARROW);
                    ItemMeta metaAnterior = anterior.getItemMeta();
                    metaAnterior.setDisplayName(ChatColor.YELLOW + "Página anterior");
                    metaAnterior.setLore(Collections.singletonList(ChatColor.GRAY + "Página " + (pagina - 1)));
                    anterior.setItemMeta(metaAnterior);
                    inv.setItem(45, anterior);
                }else

                // Botón de página siguiente
                if (pagina < totalPaginas && i==53) {
                    ItemStack siguiente = new ItemStack(Material.ARROW);
                    ItemMeta metaSiguiente = siguiente.getItemMeta();
                    metaSiguiente.setDisplayName(ChatColor.YELLOW + "Página siguiente");
                    metaSiguiente.setLore(Collections.singletonList(ChatColor.GRAY + "Página " + (pagina + 1)));
                    siguiente.setItemMeta(metaSiguiente);
                    inv.setItem(53, siguiente);
                }else if(i==49){
                    // Botón de cerrar

                    ItemStack cerrar = new ItemStack(Material.BARRIER);
                    ItemMeta metaCerrar = cerrar.getItemMeta();
                    metaCerrar.setDisplayName(ChatColor.RED + "Cerrar menú");
                    cerrar.setItemMeta(metaCerrar);
                    inv.setItem(49, cerrar);

                    player.openInventory(inv);
                }else{
                    inv.setItem(i, cristal);

                }

            }else {
                if (inicio < fin) {


                    ItemStack item = items.get(inicio);
                    Iterator<String> iterator = itemManager.obtenerInfoJugadorPorItem(item).values().iterator();
                    String nombreJugador = iterator.next();

                    long ms = itemManager.obtenerFechaEnMSItem(item);


                    ItemMeta meta = item.getItemMeta();

                    if (meta instanceof Damageable) {
                        Damageable damageable = (Damageable) meta;

                        // Obtener la durabilidad máxima del material
                        int maxDurability = item.getType().getMaxDurability();

                        // Establecer el daño (0 = completamente nuevo)
                        damageable.setDamage(0); // Le quedan 100 puntos de durabilidad

                        item.setItemMeta(meta);
                    }
                    inicio++;
                    ItemMeta itemMeta = item.getItemMeta();
                    List<String> lista = new ArrayList<>();
                    lista.add("");
                    lista.add("§fPropiedad de: §c"+nombreJugador);
                    lista.add("");
                    lista.add("§fFecha de Publicación: §a"+ UtilsFechas.formatoFecha(ms));
                    lista.add("");
                    itemMeta.setLore(lista);
                    item.setItemMeta(itemMeta);
                    inv.setItem(i, item);
                }

            }
        }

/*
        for (int i = inicio; i < fin; i++) {
            ItemStack item = items.get(i);
            String nombreJugador = itemManager.obtenerInfoJugadorPorItem(item).values().iterator().next();

            UtilsItemMeta.mostrarItemSinUso(item);
            UtilsItemMeta.addLoreEspaciado(item, "§fPropiedad de: §c" + nombreJugador);
            UtilsItemMeta.addLoreEspaciado(item, "§cClick Derecho para eliminar item de §6§lITEMS SAGRADOS§f.");
            inv.setItem(i - inicio, item);
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

 */


    }
    private ItemStack crearCristalEncantado() {
        ItemStack cristal = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemMeta meta = cristal.getItemMeta();

        meta.setDisplayName("§5§lCRISTAL ENCANTADO");
        meta.setLore(Arrays.asList(
                "§7",
                "§dEste cristal emana energía mística",
                "§dque protege los objetos sagrados",
                "§7",
                "§8» §5Poder Mágico §8«"
        ));

        // Añadir encantamiento visual (sin efectos reales)
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        cristal.setItemMeta(meta);
        return cristal;
    }

}