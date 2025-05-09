package org.amg.Utils;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class UtilsItemMeta {
    public static ItemStack mostrarItemSinUso(ItemStack item){
        ItemMeta meta = item.getItemMeta();

        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;

            // Obtener la durabilidad máxima del material
            int maxDurability = item.getType().getMaxDurability();

            // Establecer el daño (0 = completamente nuevo)
            damageable.setDamage(0); // Le quedan 100 puntos de durabilidad

            item.setItemMeta(meta);
        }
        return item;

    }
    public static ItemStack addLoreEspaciado(ItemStack item, String texto){
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lista = new ArrayList<>();
        lista.add("");
        lista.add(texto);
        if (itemMeta.getLore() == null){
            itemMeta.setLore(lista);

        }else{
            List<String> lore = itemMeta.getLore();
            lore.add("");
            lore.add(texto);
            itemMeta.setLore(lore);
        }

        item.setItemMeta(itemMeta);
        return item;
    }

}
