package org.amg.MenuListener;

import org.amg.AMGEPlugin;
import org.amg.Otros.ItemManager;
import org.amg.Utils.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MenuListenerMejorarEncantamientoMoney implements Listener {
    private final AMGEPlugin plugin;
    private final ItemManager itemManager;

    public MenuListenerMejorarEncantamientoMoney(AMGEPlugin plugin, ItemManager itemManager) {
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


            String nombreEnc = itemClick.getItemMeta().getDisplayName().split(" ")[0].substring(2);
            //Nombres Compuestos con ' '
            String[] nombreEncs = itemClick.getItemMeta().getDisplayName().split(" ");

            for (int i = 0; i < nombreEncs.length - 1; i++) {
                if (i == 0) {
                    nombreEnc = nombreEncs[i].substring(2);
                } else {
                    nombreEnc += " " + nombreEncs[i];

                }
            }

            int nivelSiguiente = UtilsEncantamientos.convertirRomano2Nivel(itemClick.getItemMeta().getDisplayName().split(" ")[1]);
            //jugador.sendMessage("Nombre Encantamiento: " + nombreEnc);
            Enchantment enc = obtenerEncantamientoPorNombre(nombreEnc, jugador);
            //jugador.sendMessage("enc: " + enc);


            //jugador.sendMessage("Llego a la línea 63 "+UtilsEncantamientos.tieneEncantamiento(itemMano,enc));


            //if (enc != null && itemMano.containsEnchantment(enc)) {
            if (enc != null && UtilsEncantamientos.tieneEncantamiento(itemMano, enc)) {

                int puedeMejorarEncantamiento = UtilsEncantamientos.encantamientoMejorado(jugador.getInventory().getContents(), itemMano, enc, nivelSiguiente, jugador);
                int diferenciaEncantamientoMaximoVanilla = nivelSiguiente - UtilsEncantamientos.obtenerMaximoNivelEncantamiento(enc.getKey().getKey());
                double precioOperacion = UtilsPrecios.calcularPrecioNivelesMejoraEncantamientoNuevo(diferenciaEncantamientoMaximoVanilla);
                boolean tieneEconomiaSuficiente = AMGEPlugin.economia.has(jugador, precioOperacion);

                if (puedeMejorarEncantamiento != -1) {

                    if (!tieneEconomiaSuficiente) {
                        jugador.sendMessage("No tienes dinero suficiente (" + UtilsPrecios.calcularPrecioNivelesMejoraEncantamientoNuevo(diferenciaEncantamientoMaximoVanilla) + ")");

                    } else {
                        //jugador.sendMessage("LLego a la línea 77");
                        ItemStack itemParaEliminar = itemMano.clone();

                        UtilsMetodosEconomicos.retirarDinero(jugador, precioOperacion);

                        UtilsEncantamientos.eliminarEncantamiento(puedeMejorarEncantamiento, enc, jugador);
                        itemMano.addUnsafeEnchantment(enc, nivelSiguiente);
                        UtilsMetodos.repararItem(itemMano, jugador);

                        jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL + "§eMejora de encantamiento implementada!");
                        itemManager.renovarItem(jugador, itemParaEliminar, itemMano);
                        itemManager.eliminarItemPorClick(jugador.getUniqueId(), itemParaEliminar, jugador);


                    }

                } else {
                    jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL + "No tienes encantamiento iguales para obtener el siguiente nivel");  //TO-DO: Mostar nombre encantamiento.
                }
            }
            jugador.closeInventory();

        }
    }


    private static Enchantment obtenerEncantamientoPorNombre(String nombre, Player jugador) {
        //jugador.sendMessage("Recorriendo todos los encantamientos");
        String nombreBusqueda = nombre.replace(" ", "_").toUpperCase();
        for (Enchantment enc : Enchantment.values()) {
            //jugador.sendMessage("Encantamiento (1) "+enc.getKey().getKey()+" vs "+nombre);
            //jugador.sendMessage("Encantamiento (2) "+enc.getKey().getNamespace()+" vs "+nombre);
            if (enc.getKey().getKey().replace(" ", "_").equalsIgnoreCase(nombreBusqueda)) { //Así incluye los enchant custom también.
                return enc;
            }
        }
        return null;
    }
}
