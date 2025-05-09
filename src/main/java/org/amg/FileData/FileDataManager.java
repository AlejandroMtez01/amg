package org.amg.FileData;

import com.sun.tools.javac.Main;
import org.amg.AMGEPlugin;
import org.amg.Utils.UtilsItemMeta;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class FileDataManager {
    private final Plugin plugin;
    private File dataFile;
    private YamlConfiguration dataConfig;
    
    public FileDataManager(Plugin plugin) {
        this.plugin = plugin;
        setup();
    }
    
    private void setup() {
        dataFile = new File(plugin.getDataFolder(), "items_especiales.yml");
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "No se pudo crear el archivo de datos", e);
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "No se pudo guardar el archivo de datos", e);
        }
    }
    
    public boolean guardarItem(UUID jugadorUUID, String jugadorNombre, ItemStack item) {
        try {
            String serializedItem = serializeItemStack(item);
            
            List<Map<String, Object>> playerItems = new ArrayList<>();
            if (dataConfig.contains(jugadorUUID.toString())) {
                playerItems = (List<Map<String, Object>>) dataConfig.getList(jugadorUUID.toString());
            }
            
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("nombre_jugador", jugadorNombre);
            itemData.put("item_serializado", serializedItem);
            itemData.put("fecha_guardado", System.currentTimeMillis());
            
            playerItems.add(itemData);
            dataConfig.set(jugadorUUID.toString(), playerItems);
            saveData();
            return true;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error al guardar item", e);
            return false;
        }
    }
    
    public boolean actualizarItem(UUID jugadorUUID, ItemStack itemViejo, ItemStack itemNuevo) {
        try {
            if (!dataConfig.contains(jugadorUUID.toString())) {
                return false;
            }
            
            List<Map<String, Object>> playerItems = (List<Map<String, Object>>) dataConfig.getList(jugadorUUID.toString());
            String oldItemSerialized = serializeItemStack(itemViejo);
            
            for (Map<String, Object> itemData : playerItems) {
                if (oldItemSerialized.equals(itemData.get("item_serializado"))) {
                    itemData.put("item_serializado", serializeItemStack(itemNuevo));
                    itemData.put("fecha_actualizado", System.currentTimeMillis());
                    dataConfig.set(jugadorUUID.toString(), playerItems);
                    saveData();
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error al actualizar item", e);
            return false;
        }
    }
    
    public List<ItemStack> obtenerItems(UUID jugadorUUID) {
        List<ItemStack> items = new ArrayList<>();
        if (!dataConfig.contains(jugadorUUID.toString())) {
            return items;
        }
        
        List<Map<String, Object>> playerItems = (List<Map<String, Object>>) dataConfig.getList(jugadorUUID.toString());
        for (Map<String, Object> itemData : playerItems) {
            try {
                items.add(deserializeItemStack((String) itemData.get("item_serializado")));
            } catch (Exception e) {
                plugin.getLogger().log(Level.WARNING, "Error al cargar un item", e);
            }
        }
        return items;
    }
    
    public List<ItemStack> obtenerTodosLosItems() {
        List<ItemStack> allItems = new ArrayList<>();
        for (String key : dataConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                allItems.addAll(obtenerItems(uuid));
            } catch (IllegalArgumentException e) {
                // Ignorar claves que no sean UUID
            }
        }
        return allItems;
    }
    public boolean eliminarItemIgnorandoLore(UUID jugadorUUID, ItemStack itemClick, Player jugador) {
        try {
            if (!dataConfig.contains(jugadorUUID.toString())) {
                return false;
            }


            List<Map<String, Object>> playerItems = (List<Map<String, Object>>) dataConfig.getList(jugadorUUID.toString());
            boolean encontrado = false;

            // Crear copia del item sin lore para comparación
            ItemStack itemComparar = itemClick.clone();
            if (itemComparar.hasItemMeta()) {
                ItemMeta meta = itemComparar.getItemMeta();
                meta.setLore(null); // Eliminar el lore
                itemComparar.setItemMeta(meta);
                UtilsItemMeta.mostrarItemSinUso(itemComparar);
            }

            String targetSerialized = serializeItemStack(itemComparar);

            Iterator<Map<String, Object>> iterator = playerItems.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> itemData = iterator.next();
                try {
                    // Deserializar y crear copia sin lore para comparar
                    ItemStack storedItem = deserializeItemStack((String) itemData.get("item_serializado"));
                    ItemStack storedItemComparar = storedItem.clone();
                    UtilsItemMeta.mostrarItemSinUso(storedItemComparar);

                    if (storedItemComparar.hasItemMeta()) {
                        ItemMeta meta = storedItemComparar.getItemMeta();
                        meta.setLore(null);
                        storedItemComparar.setItemMeta(meta);
                    }

                    if (serializeItemStack(storedItemComparar).equals(targetSerialized)) {
                        jugador.sendMessage("MISMO ITEM, se PUEDE ELIMINAR");
                        iterator.remove();
                        encontrado = true;
                        break;
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Error al deserializar item", e);
                }
            }

            if (encontrado) {
                if (playerItems.isEmpty()) {
                    dataConfig.set(jugadorUUID.toString(), null);
                } else {
                    dataConfig.set(jugadorUUID.toString(), playerItems);
                }
                saveData();
                return true;
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error al eliminar item ignorando lore", e);
            return false;
        }
    }
    public Map<String, String> obtenerInfoJugadorPorItem(ItemStack item) {
        try {
            String targetSerialized = serializeItemStack(item);
            
            for (String key : dataConfig.getKeys(false)) {
                List<Map<String, Object>> playerItems = (List<Map<String, Object>>) dataConfig.getList(key);
                for (Map<String, Object> itemData : playerItems) {
                    if (targetSerialized.equals(itemData.get("item_serializado"))) {
                        Map<String, String> info = new HashMap<>();
                        info.put("nombre_jugador", (String) itemData.get("nombre_jugador"));
                        info.put("uuid_jugador", key);
                        return info;
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error al buscar info de jugador", e);
        }
        return null;
    }
    public Long obtenerFechaEnMSItem(ItemStack item) {
        try {
            String targetSerialized = serializeItemStack(item);

            for (String key : dataConfig.getKeys(false)) {
                List<Map<String, Object>> playerItems = (List<Map<String, Object>>) dataConfig.getList(key);
                for (Map<String, Object> itemData : playerItems) {
                    if (targetSerialized.equals(itemData.get("item_serializado"))) {
                        return (long) itemData.get("fecha_guardado");
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error al buscar info de jugador", e);
        }
        return null;
    }
    
    private String serializeItemStack(ItemStack item) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeObject(item);
        dataOutput.close();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
    
    private ItemStack deserializeItemStack(String data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        ItemStack item = (ItemStack) dataInput.readObject();
        dataInput.close();
        return item;
    }
}