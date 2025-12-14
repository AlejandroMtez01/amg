package org.amg.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UtilsVender {

    private static Material[] materialesEnVenta= {Material.GOLD_BLOCK,Material.GOLD_INGOT,Material.GOLD_NUGGET};
    private static Material[] materialesEliminar= {Material.GOLDEN_SWORD,Material.NETHERRACK,Material.COBBLESTONE,Material.COBBLED_DEEPSLATE,Material.ANDESITE,Material.GRANITE,Material.DIORITE,Material.TUFF};
    private static Double[] precioMaterialesEnVenta = {1.00,1.00/9.00,1.00/81.00};

    private static int encontrarItemVenta(ItemStack item){
        for (int i = 0; i < materialesEnVenta.length; i++) {
            if (item.getType().equals(materialesEnVenta[i])){
                return i;
            }
        }
        return -1;
    }

    private static int encontrarItemEliminar(ItemStack item){
        for (int i = 0; i < materialesEliminar.length; i++) {
            if (item.getType().equals(materialesEliminar[i])){
                return i;
            }
        }
        return -1;
    }
    public static void venderItemMano(Player jugador){
        ItemStack itemMano = jugador.getInventory().getItemInMainHand();
        if (itemMano != null && !itemMano.getType().isAir()) {

            int indiceElemento = encontrarItemVenta(itemMano);
            double ingreso = 0;
            if (indiceElemento != -1) {
                ingreso = itemMano.getAmount() * precioMaterialesEnVenta[indiceElemento];
                UtilsMetodosEconomicos.ingresarDinero(jugador, ingreso);
                jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL + "La venta de los items te ha generado " + ingreso + "€");
                jugador.getInventory().setItemInMainHand(null);


            }else{
                jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL + "El item seleccionado no se puede vender.");
            }
        }

    }
    public static void venderItemInventario(Player jugador){
        boolean eliminadosItems = false;
        ItemStack[] inventario = jugador.getInventory().getContents();
        double dineroObtenido = 0;
        for (int i = 0; i <inventario.length ; i++) {
            if (inventario[i] != null && !inventario[i].getType().isAir()) {
                int indiceElemento = encontrarItemVenta(inventario[i]);

                if (indiceElemento != -1) {
                    double ingreso = inventario[i].getAmount() * precioMaterialesEnVenta[indiceElemento];
                    dineroObtenido += ingreso;
                    UtilsMetodosEconomicos.ingresarDinero(jugador, ingreso);
                    jugador.getInventory().setItem(i,null);
                }
                indiceElemento = encontrarItemEliminar((inventario[i]));
                if (indiceElemento != -1) {
                    jugador.getInventory().setItem(i,null);
                    eliminadosItems = true;

                }

            }
        }
        if (dineroObtenido>0) {
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL + "La venta de los items te ha generado " + dineroObtenido + "€");
        }else if (eliminadosItems){
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL + "Eliminados items inútiles del inventario.");

        }else{
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL + "No tienes item para vender");

        }



    }
}
