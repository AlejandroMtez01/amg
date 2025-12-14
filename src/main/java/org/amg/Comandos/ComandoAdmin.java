package org.amg.Comandos;

import org.amg.Otros.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ComandoAdmin implements CommandExecutor {

    private final ItemManager itemManager;

    public ComandoAdmin(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Verificar si es el comando de reload
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {

            // 1. Permisos (¡Importante!)
            if (!sender.hasPermission("amge.admin")) {
                sender.sendMessage(ChatColor.RED + "No tienes permiso para hacer esto.");
                return true;
            }

            // 2. Ejecutar la recarga
            sender.sendMessage(ChatColor.YELLOW + "Recargando configuración de Items Sagrados...");

            try {
                itemManager.recargarDatos(); // Llamamos al método que creamos en el Paso 1
                sender.sendMessage(ChatColor.GREEN + "¡Recarga completada con éxito!");
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "Ocurrió un error al recargar. Revisa la consola.");
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }
}