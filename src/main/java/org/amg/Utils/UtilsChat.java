package org.amg.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UtilsChat {
    public static void broadcastMejora(Player jugador, ItemStack itemReal, String encantamientoNombre, int nivel) {

        // 1. CLONAR EL ITEM PARA MODIFICARLO VISUALMENTE (SOLO PARA EL CHAT)
        // Esto sirve para añadir la línea de "MEJORADO" subrayada en el Lore
        ItemStack itemDisplay = itemReal.clone();
        ItemMeta meta = itemDisplay.getItemMeta();
// 1. Ocultamos los encantamientos por defecto para dibujarlos nosotros
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);

        // 2. OBTENER EL NBT (JSON) DEL ITEM (Necesario para el Hover)
        // Usamos reflexión para que funcione en la 1.20 sin depender de NMS directos

        // 3. CONSTRUIR EL MENSAJE
        ComponentBuilder mensaje = new ComponentBuilder("");

        // Parte 1: Texto normal
        mensaje.append(UtilsMensajes.NOMBRE_INFORMAL).color(ChatColor.GOLD)
                .append(jugador.getName()).color(ChatColor.YELLOW)
                .append(" ha mejorado su ").color(ChatColor.WHITE);

        // Parte 2: EL ITEM (Con Hover)
        String nombreItem = (meta.hasDisplayName()) ? meta.getDisplayName() : itemReal.getType().name();

        // Creamos el componente del item
        TextComponent componenteItem = new TextComponent(nombreItem);
        componenteItem.setColor(ChatColor.AQUA);

        // Aquí está la magia: SHOW_ITEM
        // 'Item' requiere: (ID del item tipo minecraft:stone, Cantidad, NBT Tag)
        /*componenteItem.setHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_ITEM,
                new Item(itemDisplay.getType().getKey().toString(), itemDisplay.getAmount(), ItemTag.ofNbt(itemNbt))
        ));*/

        componenteItem.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sagrados"));
        TextComponent[] hover = new TextComponent[]{new TextComponent("§aClick para ver el item completo en §cMENU DE ITEMS SAGRADOS§f.")};

        componenteItem.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));

        // Añadimos el item al mensaje
        mensaje.append(componenteItem);

        // Parte 3: Texto final
        mensaje.append(" con el encantamiento ").color(ChatColor.WHITE)
                .append(encantamientoNombre + " " + nivel).color(ChatColor.AQUA); // Subrayado en el chat también

        // 4. ENVIAR A TODOS
        Bukkit.spigot().broadcast(mensaje.create());
    }

    public static void broadcastItemSpigot(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) return;

        // 1. Crear el constructor del texto Hover
        ComponentBuilder hoverText = new ComponentBuilder();

        // Nombre del item
        String displayName = (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
                ? item.getItemMeta().getDisplayName()
                : item.getType().name();

        hoverText.append(displayName).color(ChatColor.DARK_AQUA);

        // 2. Añadir Encantamientos manualmente al hover
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasEnchants()) {
            hoverText.append("\n\n§9Encantamientos:").append("\n");

            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                // Nota: getKey() es moderno, para versiones viejas usa getName()
                String enchName = UtilsEncantamientos.traducirEncantamiento(entry.getKey());
                String level = UtilsEncantamientos.convertirNivel2Romano(entry.getValue());
                hoverText.append(enchName + " " + level).color(ChatColor.GRAY).append("\n");
            }
        }

        // 3. Añadir Lore manualmente
        if (meta != null && meta.hasLore()) {
            for (String line : meta.getLore()) {
                hoverText.append(line).append("\n");
            }
        }

        // 4. Crear el componente visual del chat [Item]
        TextComponent itemComponent = new TextComponent(displayName);
        itemComponent.setColor(ChatColor.GOLD);

        // Asignar el evento Hover (SHOW_TEXT es más compatible que SHOW_ITEM si no tienes el JSON exacto)
        itemComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.create()));

        // 5. Crear el mensaje final
        TextComponent message = new TextComponent("📢 " + player.getName() + " enseña: ");
        message.setColor(ChatColor.YELLOW);
        message.addExtra(itemComponent);

        // 6. Broadcast
        player.getServer().spigot().broadcast(message);
    }

}





