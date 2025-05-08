package org.amg.MenuListener;

import org.amg.AMGEPlugin;
import org.amg.Utils.UtilsMensajes;
import org.amg.Utils.UtilsPrecios;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.Console;

public class MenuListenerEliminarEncantamiento implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent evento) {
        if (!(evento.getWhoClicked() instanceof Player)) return;
        if (!evento.getView().getTitle().equals("Eliminar Encantamientos del Item")) return;

        Player jugador = (Player) evento.getWhoClicked();
        ItemStack itemClick = evento.getCurrentItem();

        if (itemClick == null || itemClick.getType() != Material.ENCHANTED_BOOK) return;

        evento.setCancelled(true);

        if (evento.isLeftClick()) {
            ItemStack itemMano = jugador.getInventory().getItemInMainHand();
            if (itemMano == null || itemMano.getType().isAir()) {
                jugador.sendMessage("¡Ya no tienes el item en la mano!");
                jugador.closeInventory();
                return;
            }
            String nombreEnc = "";
            if (itemClick.getItemMeta().getDisplayName().split(" ").length==2){
                //Se hace el substring parra quitar el &e
                nombreEnc = itemClick.getItemMeta().getDisplayName().split(" ")[0].substring(2).trim();
            }else{
                nombreEnc = itemClick.getItemMeta().getDisplayName().split(" ")[0].substring(2).trim()+" "+itemClick.getItemMeta().getDisplayName().split(" ")[1].trim();

            }

            Enchantment enc = obtenerEncantamientoPorNombre2(nombreEnc,jugador);
            //jugador.sendMessage("Pruebas de Funcionamiento: ");
            //jugador.sendMessage("Nombre Encantamiento a Eliminar (nombreEnc)"+nombreEnc);
            //jugador.sendMessage("Nombre Encantamiento a Eliminar (enc)"+enc);


            if (enc != null && itemMano.containsEnchantment(enc)) {
                jugador.sendMessage("Entro en el menú de encatamientos");


                if (!AMGEPlugin.economia.has(jugador, UtilsPrecios.PRECIO_ELIMINAR_ENCANTAMIENTO)){
                    jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"§cDisculpa no tienes suficiente dinero para eliminar el encantamiento");
                }else{
                    AMGEPlugin.economia.bankWithdraw(jugador.getName(),UtilsPrecios.PRECIO_ELIMINAR_ENCANTAMIENTO);
                    itemMano.removeEnchantment(enc);
                    jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"§aEncantamiento " + nombreEnc + " ha sido eliminado!");
                    jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"Se han retirado "+UtilsPrecios.PRECIO_ELIMINAR_ENCANTAMIENTO+" de su cuenta bancaria.");
                    jugador.closeInventory();
                }


            }
        }
    }
    private static Enchantment obtenerEncantamientoPorNombre(String nombre) {
        String nombreBusqueda = nombre.replace(" ", "_").toUpperCase();
        for (Enchantment enc : Enchantment.values()) {

            if (enc.getKey().getKey().equalsIgnoreCase(nombreBusqueda)) {
                return enc;
            }
        }
        return null;
    }
    private static Enchantment obtenerEncantamientoPorNombre2(String nombre, Player jugador) {
        String nombreBusqueda = nombre.replace(" ", "_").toLowerCase();
        //jugador.sendMessage("Buscando comparaciones entre enchants");
        for (Enchantment enc : Enchantment.values()) {
            //jugador.sendMessage(enc.getKey().getKey()+" vs "+nombreBusqueda);

            if (enc.getKey().getKey().equalsIgnoreCase(nombreBusqueda)) {
                return enc;
            }
        }
        return null;
    }
}
