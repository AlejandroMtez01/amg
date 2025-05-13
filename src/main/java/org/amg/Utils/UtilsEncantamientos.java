package org.amg.Utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;
import java.util.Set;

public class UtilsEncantamientos {
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
    public static boolean tieneEcantamientosEspeciales(ItemStack item) {
        Map<Enchantment, Integer> encantamientos = item.getEnchantments();


        for (Enchantment enc : encantamientos.keySet()) {
            // ✅ Filtrar solo encantamientos de Minecraft vanilla
            String id = enc.getKey().getKey();
            int nivel = encantamientos.get(enc);
            if(!ENCANTAMIENTOS_VANILLA.contains(id)){ //Si no contiene un encantamiento vanilla.
                return true;
            }

        }
        return false;
    }
    public static int obtenerMaximoNivelEncantamiento(String claveEncantamiento) {
        Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(claveEncantamiento));
        if (enchant != null) {
            return enchant.getMaxLevel();
        }
        return 0;
    }
    public static boolean tieneEncantamientosTOP(ItemStack it) {
        Map<Enchantment, Integer> encantamientos = it.getEnchantments();
        for (Enchantment enc : encantamientos.keySet()) {
            if (obtenerMaximoNivelEncantamiento(enc.getKey().getKey()) < encantamientos.get(enc)) {
                return true;
            }
        }
        return false;
    }
    public static boolean tieneEncantamientosMaximosVanilla(ItemStack it) {
        Map<Enchantment, Integer> encantamientos = it.getEnchantments();
        for (Enchantment enc : encantamientos.keySet()) {
            if (obtenerMaximoNivelEncantamiento(enc.getKey().getKey()) == encantamientos.get(enc)) {
                return true;
            }

        }
        return false;
    }
    public static int encantamientoMejorado(ItemStack[] huecosInventario, ItemStack itemMano, Enchantment encantamiento, int level, Player jugador) {
        jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Recorriendo todos los items del inventario (v 1.5)");
        for (int i = 0; i < jugador.getInventory().getContents().length; i++) {
            ItemStack elemento = jugador.getInventory().getContents()[i];


            if (itemMano == null || !itemMano.containsEnchantment(encantamiento) ||
                    itemMano.getEnchantmentLevel(encantamiento) != level - 1) {
                return -1;
            }
            if (elemento == null || elemento.getType().isAir() || i == jugador.getInventory().getHeldItemSlot()) {
            } else {

                if (elemento.containsEnchantment(encantamiento) && elemento.getEnchantmentLevel(encantamiento) == (level - 1)) {
                    return i;
                }
                if (elemento.getType() == Material.ENCHANTED_BOOK && tieneEncantamientoLibro(elemento, encantamiento)) {
                    if (getNivelEncantamientoLibro(elemento, encantamiento) == (level - 1)) {
                        return i;
                    }
                }
            }
        }
        //jugador.sendMessage("No existen encantamientos similares");
        return -1;

    }
    // Métodos auxiliares nuevos para manejar libros encantados
    private static boolean tieneEncantamientoLibro(ItemStack item, Enchantment encantamiento) {
        if (item.getType() != Material.ENCHANTED_BOOK) return false;

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        return meta != null && meta.hasStoredEnchant(encantamiento);
    }

    private static int getNivelEncantamientoLibro(ItemStack item, Enchantment encantamiento) {
        if (item.getType() != Material.ENCHANTED_BOOK) return 0;

        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        return meta != null ? meta.getStoredEnchantLevel(encantamiento) : 0;
    }

    public static void eliminarEncantamiento(int id,Enchantment encantamiento,Player jugador){
        ItemStack item = jugador.getInventory().getContents()[id];
        //Si se trata de un libro
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            meta.removeStoredEnchant(encantamiento);


            // Cambiar a libro normal si no tiene encantamientos
            if (meta.getStoredEnchants().isEmpty()) {
                item.setType(Material.BOOK);
                item.setItemMeta(null); // Eliminar la meta completamente
            } else {
                item.setItemMeta(meta);
            }
        }else{
            jugador.getInventory().getContents()[id].removeEnchantment(encantamiento);
        }
    }
    public static int convertirRomano2Nivel(String nivel) {
        switch (nivel) {
            case "I":    return 1;
            case "II":   return 2;
            case "III":  return 3;
            case "IV":   return 4;
            case "V":    return 5;
            case "VI":   return 6;
            case "VII":  return 7;
            case "VIII": return 8;
            case "IX":   return 9;
            case "X":    return 10;
            default: return 1; // Valor por defecto si no se puede parsear

        }
    }
    public static String convertirNivel2Romano(int nivel) {
        switch (nivel) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            default:
                return String.valueOf(nivel);
        }
    }
}
