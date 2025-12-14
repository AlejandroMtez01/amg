package org.amg.Utils;

import org.amg.AMGEPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

public class UtilsMetodos {

    public static void repararItem(ItemStack item, Player jugador){
        Damageable meta = (Damageable)  item.getItemMeta();
        if (meta.getDamage() == 0){
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"El item que sostiene no necesita ser reparado.");
        }else{
            meta.setDamage(0); //Reparación.
            item.setItemMeta((meta));
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"El item ha sido reparado correctamente.");
        }

    }
    private static final int SHORT_BLINDNESS_DURATION = 5; // Ticks (1/4 segundo)


    public static void fundirMateriales(Map<Material, Material> materialesFundibles,Player jugador) {

        int materialesFundidos = 0;

        // Recorrer todo el inventario
        for (ItemStack item : jugador.getInventory().getContents()) {
            if (item != null && materialesFundibles.containsKey(item.getType())) {
                Material resultado = materialesFundibles.get(item.getType());
                int cantidad = item.getAmount();

                // Reemplazar el material por su versión fundida
                item.setType(resultado);
                materialesFundidos += cantidad;
            }
        }

        if (materialesFundidos > 0) {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"§aHas fundido §e" + materialesFundidos + " §amateriales!");
        } else {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"§cNo tienes materiales fundibles en tu inventario.");
        }
    }
    public static void simulateReconnect(Player player, Plugin plugin) {
        try {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || !item.hasItemMeta()) return;

            // 1. Aplicar ceguera momentánea (simula el paquete de reconexión)
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.BLINDNESS,
                    SHORT_BLINDNESS_DURATION,
                    1,
                    false,
                    false,
                    false
            ));

            // 2. Forzar actualización completa del jugador
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Cambiar temporalmente de slot
                int originalSlot = player.getInventory().getHeldItemSlot();
                int tempSlot = originalSlot == 0 ? 1 : 0;

                player.getInventory().setHeldItemSlot(tempSlot);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.getInventory().setHeldItemSlot(originalSlot);

                    // Actualización final
                    player.updateInventory();
                    //player.updateScaledHealth();
                }, 2L);
            }, SHORT_BLINDNESS_DURATION);

        } catch (Exception e) {
            plugin.getLogger().warning("Error en simulateReconnect: " + e.getMessage());
        }
    }
    public static boolean esShulker(Material material) {
        // Lista de todos los tipos de Shulker Boxes
        return material == Material.SHULKER_BOX ||
                material == Material.WHITE_SHULKER_BOX ||
                material == Material.ORANGE_SHULKER_BOX ||
                material == Material.MAGENTA_SHULKER_BOX ||
                material == Material.LIGHT_BLUE_SHULKER_BOX ||
                material == Material.YELLOW_SHULKER_BOX ||
                material == Material.LIME_SHULKER_BOX ||
                material == Material.PINK_SHULKER_BOX ||
                material == Material.GRAY_SHULKER_BOX ||
                material == Material.LIGHT_GRAY_SHULKER_BOX ||
                material == Material.CYAN_SHULKER_BOX ||
                material == Material.PURPLE_SHULKER_BOX ||
                material == Material.BLUE_SHULKER_BOX ||
                material == Material.BROWN_SHULKER_BOX ||
                material == Material.GREEN_SHULKER_BOX ||
                material == Material.RED_SHULKER_BOX ||
                material == Material.BLACK_SHULKER_BOX;
    }



}
