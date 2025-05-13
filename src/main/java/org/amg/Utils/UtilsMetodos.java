package org.amg.Utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

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


}
