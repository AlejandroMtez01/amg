package org.amg.Utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;

public class UtilsMetodos {
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
        jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Recorriendo todos los items del inventario (v 1.4)");
        for (int i = 0; i < jugador.getInventory().getContents().length; i++) {
            ItemStack elemento = jugador.getInventory().getContents()[i];


            if (itemMano == null || !itemMano.containsEnchantment(encantamiento) ||
                    itemMano.getEnchantmentLevel(encantamiento) != level - 1) {
                return -1;
            }
            if (elemento == null || elemento.getType().isAir() || i == jugador.getInventory().getHeldItemSlot()) {
            } else {
                // Primero verificar que el item en mano tenga el encantamiento y nivel especificado
//                jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Comprobando item "+elemento.getItemMeta().getDisplayName());
//                jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Contiene el encantamiento adecuado "+encantamiento+ " / "+itemMano.containsEnchantment(encantamiento));
//                jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Contiene el nivel adecuado "+(level-1)+ " / "+itemMano.getEnchantmentLevel(encantamiento));

                // Recorro completamente el inventario buscando 2 items que tengan el mismo nivel de encantamiento
                if (elemento.containsEnchantment(encantamiento) && elemento.getEnchantmentLevel(encantamiento) == (level - 1)) {

                    //jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL + "Encontrado " + elemento.getItemMeta().getDisplayName());
                    return i;
                }
            }
        }
        //jugador.sendMessage("No existen encantamientos similares");
        return -1;

    }
    public static void eliminarEncantamientoInventario(Player jugador, ItemStack item, Enchantment enchantment){
    }
    public static void repararItem(ItemStack item, Player jugador){
        Damageable meta = (Damageable)  item.getItemMeta();
        if (meta.getDamage() == 0){
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"El item que sostiene no necesita ser reparado.");
        }else{
            meta.setDamage(0); //Reparación.
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"El item ha sido reparado correctamente.");
        }
    }
    public static void eliminarEncantamiento(ItemStack copiaElemento,Enchantment encantamiento,Player jugador){
        jugador.getInventory().remove(copiaElemento);
        copiaElemento.removeEnchantment(encantamiento);
        jugador.getInventory().addItem(copiaElemento);
    }
    public static void eliminarEncantamiento(int id,Enchantment encantamiento,Player jugador){
        jugador.getInventory().getContents()[id].removeEnchantment(encantamiento);
//        jugador.getInventory().remove(copiaElemento);
//        copiaElemento.removeEnchantment(encantamiento);
//        jugador.getInventory().addItem(copiaElemento);
    }
}
