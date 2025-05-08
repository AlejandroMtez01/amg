package org.amg.Menu;

import org.amg.AMGEPlugin;
import org.amg.Utils.UtilsMetodos;
import org.amg.Utils.UtilsPrecios;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MenuEliminarEncantamientos implements Listener {

    private static Plugin plugin;

    public MenuEliminarEncantamientos(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    private static final Set<String> ENCANTAMIENTOS_VANILLA = Set.of(
            "protection", "fire_protection", "feather_falling", "blast_protection", "projectile_protection",
            "respiration", "aqua_affinity", "thorns", "depth_strider", "frost_walker", "binding_curse",
            "sharpness", "smite", "bane_of_arthropods", "knockback", "fire_aspect", "looting", "sweeping",
            "efficiency", "silk_touch", "unbreaking", "fortune",
            "power", "punch", "flame", "infinity",
            "luck_of_the_sea", "lure",
            "loyalty", "impaling", "riptide", "channeling",
            "multishot", "piercing", "quick_charge",
            "soul_speed", "swift_sneak", "mending", "vanishing_curse"
    );
    public static void mostrarMenu(Player jugador, ItemStack item) {
        Map<Enchantment, Integer> encantamientos = item.getEnchantments();
        int size = (int) Math.ceil(encantamientos.size() / 9.0) * 9;
        if (size < 9) size = 9;

        Inventory menu = Bukkit.createInventory(jugador, size, "Eliminar Encantamientos del Item");

        for (Enchantment enc : encantamientos.keySet()) {
            // ✅ Filtrar solo encantamientos de Minecraft vanilla
            String id = enc.getKey().getKey();
            if (!ENCANTAMIENTOS_VANILLA.contains(id)) continue;
            int nivel = encantamientos.get(enc);
            ItemStack libro = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = libro.getItemMeta();

            String nombre = obtenerNombreLegible(enc);
            meta.setDisplayName("§e" + nombre + " " + UtilsMetodos.convertirNivel2Romano(nivel));



            List<String> lore = new ArrayList<>();
            lore.add("§7Nivel: §f" + nivel);
            lore.add("");
            lore.add("§ePrecio: §f"+UtilsPrecios.PRECIO_ELIMINAR_ENCANTAMIENTO);
            //En función de la economía el LORE tendrá una información u otra.
            if (AMGEPlugin.economia.has(jugador, UtilsPrecios.PRECIO_ELIMINAR_ENCANTAMIENTO)) {
                lore.add("§7Click izquierdo para eliminar");
            }else{
                lore.add("§cDinero insuficiente (§f"+UtilsPrecios.PRECIO_ELIMINAR_ENCANTAMIENTO+"€§c)");

            }
            meta.setLore(lore);

            //Establecemos los metados del item (Lore)
            libro.setItemMeta(meta);

            //Se añade al menú el elemento modificado.
            menu.addItem(libro);
        }

        jugador.openInventory(menu);
    }



    private static String obtenerNombreLegible(Enchantment enc) {
        String nombre = enc.getKey().getKey();
        return nombre.substring(0, 1).toUpperCase() + nombre.substring(1).replace("_", " ");
    }




}