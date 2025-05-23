package org.amg.MenuListener;

import net.milkbowl.vault.economy.EconomyResponse;
import org.amg.AMGEPlugin;
import org.amg.Otros.ItemManager;
import org.amg.Utils.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuListenerMejorarEncantamiento implements Listener {
    private final AMGEPlugin plugin;
    private final ItemManager itemManager;
    public MenuListenerMejorarEncantamiento(AMGEPlugin plugin, ItemManager itemManager) {
        this.plugin = plugin;
        this.itemManager = itemManager;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent evento) {
        if (!(evento.getWhoClicked() instanceof Player)) return;
        if (!evento.getView().getTitle().equals("Mejorar Encantamientos del Item")) return;


        Player jugador = (Player) evento.getWhoClicked();
        ItemStack itemClick = evento.getCurrentItem();
        ItemStack itemMano = jugador.getInventory().getItemInMainHand();



        if (itemClick == null || itemClick.getType() != Material.ENCHANTED_BOOK) return;

        evento.setCancelled(true);

        if (evento.isLeftClick()) {
            if (itemMano == null || itemMano.getType().isAir()) {
                jugador.sendMessage("¡Ya no tienes el item en la mano!");
                jugador.closeInventory();
                return;
            }




            //jugador.sendMessage("Línea 33: " + itemClick.getItemMeta().getDisplayName());
            String nombreEnc = itemClick.getItemMeta().getDisplayName().split(" ")[0].substring(2);
            int nivelSiguiente = UtilsEncantamientos.convertirRomano2Nivel(itemClick.getItemMeta().getDisplayName().split(" ")[1]);
            //jugador.sendMessage("nombreEnc: " + nombreEnc);
            Enchantment enc =  obtenerEncantamientoPorNombre(nombreEnc);
            //jugador.sendMessage("enc: " + enc);

            jugador.getInventory().getContents();


            if (enc != null && itemMano.containsEnchantment(enc)) {
                int puedeMejorarEncantamiento = UtilsEncantamientos.encantamientoMejorado(jugador.getInventory().getContents(), itemMano, enc,nivelSiguiente,jugador);
                double precioOperacion = UtilsPrecios.calcularPrecioMejoraEncantamiento(nivelSiguiente-UtilsEncantamientos.obtenerMaximoNivelEncantamiento(enc.getKey().getKey()));
                boolean tieneEconomiaSuficiente = AMGEPlugin.economia.has(jugador, precioOperacion);

                if (puedeMejorarEncantamiento != -1) {

                    if (!tieneEconomiaSuficiente){
                        jugador.sendMessage("No tienes dinero suficiente ("+UtilsPrecios.calcularPrecioMejoraEncantamiento(nivelSiguiente)+")");

                    }else{
                        ItemStack itemParaEliminar = itemMano.clone();

                        UtilsMetodosEconomicos.retirarDinero(jugador,precioOperacion);

                        UtilsEncantamientos.eliminarEncantamiento(puedeMejorarEncantamiento,enc,jugador);
                        itemMano.addUnsafeEnchantment(enc, nivelSiguiente);
                        UtilsMetodos.repararItem(itemMano,jugador);

                        jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"§eMejora de encantamiento implementada!");
                        itemManager.renovarItem(jugador,itemParaEliminar,itemMano);
                        itemManager.eliminarItemPorClick(jugador.getUniqueId(),itemParaEliminar,jugador);
                        //PENDIENTE DE VER PORQUE NO AÑADE EL ENCHNANT (UNSAFE)

                        //Cuando es el mismo elemento lo elimina.


                        //jugador.getInventory().remove(itemMano); //Opcionalmente, se puede eliminar el ITEM.



                    }

                } else {
                    jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"No tienes encantamiento iguales para obtener el siguiente nivel");  //TO-DO: Mostar nombre encantamiento.
                }

//                if (!AMGEPlugin.economia.has(jugador,200)){
//                    jugador.sendMessage("AMG. Disculpa no tienes suficiente dinero para eliminar el encantamiento");
//                }else{
//                    AMGEPlugin.economia.bankWithdraw(jugador.getName(),200);
//
//                    itemMano.removeEnchantment(enc);
//                    jugador.sendMessage("§aEncantamiento " + nombreEnc + " eliminado!");
//
//                    jugador.sendMessage("AMG. Se han retirado 200€ de su cuenta.");
//
//                    jugador.closeInventory();
//                }


            }
            jugador.closeInventory();

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
}
