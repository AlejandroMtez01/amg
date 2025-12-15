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
    public void recargarArchivo() {
        // 1. Guardamos el archivo si no existe (opcional, por seguridad)
        if (dataFile == null) { // 'configFile' es la variable File
            dataFile = new File(plugin.getDataFolder(), "items_especiales.yml");
        }

        this.dataConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(this.dataFile);


    }


    public boolean guardarItem(UUID jugadorUUID, String jugadorNombre, ItemStack item) {
        try {
            // Se clona y repara el Item (Tu lógica original, intacta)
            ItemStack itemCopia = item.clone();
            UtilsItemMeta.mostrarItemSinUso(itemCopia);
            String serializedItem = serializeItemStack(itemCopia);

            // --- CAMBIO PRINCIPAL ---
            // En lugar de usar la UUID del jugador como clave (que obliga a usar listas),
            // generamos una ID única para este item específico.
            String uniqueItemID = UUID.randomUUID().toString();

            // Guardamos los datos DIRECTAMENTE (Sin crear Maps ni Lists)
            // Esto crea la estructura:
            // ID_ITEM:
            //   nombre_jugador: ...
            //   item_serializado: ...
            dataConfig.set(uniqueItemID + ".nombre_jugador", jugadorNombre);
            dataConfig.set(uniqueItemID + ".uuid_propietario", jugadorUUID.toString()); // Guardo también la UUID por si acaso
            dataConfig.set(uniqueItemID + ".item_serializado", serializedItem);
            dataConfig.set(uniqueItemID + ".fecha_guardado", System.currentTimeMillis());

            // Guardamos el archivo
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
        List<ItemStack> items = new ArrayList<>();
        // Accedemos a la config (asegúrate de tener el getter en FileDataManager)
        org.bukkit.configuration.file.FileConfiguration config = dataConfig;

        if (config == null) return items;

        for (String key : config.getKeys(false)) {
            // Lectura directa (Formato Nuevo)
            String base64 = config.getString(key + ".item_serializado");

            if (base64 != null) {
                ItemStack item = deserializar(base64); // Tu método deserializar
                if (item != null) {
                    items.add(item);
                }
            }
        }
        return items;
    }
    private ItemStack deserializar(String base64) {
        try {
            // Decodifica el texto Base64 a bytes
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
            // Convierte los bytes a un objeto de Bukkit (ItemStack)
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            ItemStack item = (ItemStack) dataInput.readObject();

            dataInput.close();
            return item;
        } catch (Exception e) {
            // Si falla (texto corrupto), devuelve null para que lo controlemos arriba
            return null;
        }
    }

    public boolean eliminarItemIgnorandoLore(UUID jugadorUUID, ItemStack itemClick, Player jugador) {
        try {
            if (dataConfig == null || itemClick == null) return false;

            // 1. Preparamos el item "Objetivo" (El que has clickado)
            // Creamos copia y quitamos el Lore para poder compararlo con el guardado
            ItemStack itemComparar = itemClick.clone();
            UtilsItemMeta.eliminarLore(itemComparar); // Tu utilidad para quitar lore

            // Lo convertimos a String para comparar texto con texto
            String targetSerialized = serializeItemStack(itemComparar);

            // 2. Recorremos TODAS las claves (IDs únicas) del archivo nuevo
            for (String key : dataConfig.getKeys(false)) {

                // OPTIMIZACIÓN: Primero miramos si este item pertenece al jugador.
                // Así no perdemos tiempo deserializando items de otros jugadores.
                String ownerUUID = dataConfig.getString(key + ".uuid_propietario");

                // Si no tiene UUID guardada, intentamos mirar por nombre (por compatibilidad)
                if (ownerUUID == null) {
                    String ownerName = dataConfig.getString(key + ".nombre_jugador");
                    if (ownerName != null && !ownerName.equals(jugador.getName())) {
                        continue; // No es de este jugador, pasamos al siguiente
                    }
                } else if (!ownerUUID.equals(jugadorUUID.toString())) {
                    continue; // La UUID no coincide, pasamos al siguiente
                }

                // 3. Si el item es del jugador, leemos el contenido
                String base64 = dataConfig.getString(key + ".item_serializado");

                if (base64 != null) {
                    try {
                        // Deserializamos el item guardado
                        ItemStack storedItem = deserializeItemStack(base64);

                        if (storedItem != null) {
                            // Aplicamos la misma limpieza (clone, sin uso, sin lore)
                            ItemStack storedItemComparar = storedItem.clone();
                            UtilsItemMeta.mostrarItemSinUso(storedItemComparar);
                            UtilsItemMeta.eliminarLore(storedItemComparar);

                            // 4. COMPARACIÓN FINAL
                            if (serializeItemStack(storedItemComparar).equals(targetSerialized)) {
                                // ¡ENCONTRADO!

                                // Borramos la sección entera de esta ID (key)
                                dataConfig.set(key, null);

                                // Guardamos cambios
                                saveData();
                                return true; // Salimos indicando éxito
                            }
                        }
                    } catch (Exception e) {
                        // Si falla un item concreto, lo ignoramos y seguimos buscando
                        plugin.getLogger().warning("Item corrupto ignorado durante eliminación: " + key);
                    }
                }
            }

            return false; // No se encontró coincidencia

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error al eliminar item ignorando lore", e);
            return false;
        }
    }

    public boolean eliminarItemIgnorandoLoreOld(UUID jugadorUUID, ItemStack itemClick, Player jugador) {
        try {
            if (!dataConfig.contains(jugadorUUID.toString())) {
                return false;
            }


            List<Map<String, Object>> playerItems = (List<Map<String, Object>>) dataConfig.getList(jugadorUUID.toString());
            boolean encontrado = false;

            // Crear copia del item sin lore para comparación [Item clicado en el menú].
            ItemStack itemComparar = itemClick.clone();
            UtilsItemMeta.eliminarLore(itemComparar);

            //Se serializa el Item.
            String targetSerialized = serializeItemStack(itemComparar);

            Iterator<Map<String, Object>> iterator = playerItems.iterator();
            //Se recorre completamente el archivo
            while (iterator.hasNext()) {
                Map<String, Object> itemData = iterator.next();
                try {
                    // Deserializar y crear copia sin lore para comparar
                    ItemStack storedItem = deserializeItemStack((String) itemData.get("item_serializado"));
                    ItemStack storedItemComparar = storedItem.clone();
                    UtilsItemMeta.mostrarItemSinUso(storedItemComparar);

                    UtilsItemMeta.eliminarLore(storedItemComparar);
                    // Se busca en todos los registros un serializado igual.

                    if (serializeItemStack(storedItemComparar).equals(targetSerialized)) {
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
    public Map<String, String> obtenerInfoJugadorPorItem(ItemStack itemBuscado) {
        if (dataConfig == null) return null;

        // Recorremos todas las claves (IDs únicas)
        for (String key : dataConfig.getKeys(false)) {
            // Obtenemos el item serializado directamente
            String base64 = dataConfig.getString(key + ".item_serializado");

            if (base64 != null) {
                // Deserializamos (Usa tu método deserializeItemStack)
                ItemStack itemGuardado = deserializeItemStack(base64);

                // Si el item coincide con el que buscamos
                if (itemGuardado != null && itemGuardado.isSimilar(itemBuscado)) {
                    String nombre = dataConfig.getString(key + ".nombre_jugador");

                    Map<String, String> info = new HashMap<>();
                    info.put("nombre", (nombre != null ? nombre : "Desconocido"));
                    return info;
                }
            }
        }
        return null;
    }
    /**
     * Convierte un String (Base64) de vuelta a un ItemStack.
     */
    public org.bukkit.inventory.ItemStack deserializeItemStack(String data) {
        try {
            // CORRECCIÓN: Usamos java.util.Base64 para decodificar
            byte[] rawData = java.util.Base64.getDecoder().decode(data);

            java.io.ByteArrayInputStream inputStream = new java.io.ByteArrayInputStream(rawData);
            org.bukkit.util.io.BukkitObjectInputStream dataInput = new org.bukkit.util.io.BukkitObjectInputStream(inputStream);

            org.bukkit.inventory.ItemStack item = (org.bukkit.inventory.ItemStack) dataInput.readObject();

            dataInput.close();
            return item;
        } catch (Exception e) {
            org.bukkit.Bukkit.getLogger().severe("Error al deserializar item: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    // -----------------------------------------------------------------------
    // SUSTITUYE TU MÉTODO 'obtenerFechaEnMSItem' POR ESTE EN ItemManager.java
    // -----------------------------------------------------------------------

    public long obtenerFechaEnMSItem(org.bukkit.inventory.ItemStack itemBuscado) {
        // 1. Obtener config
        org.bukkit.configuration.file.FileConfiguration config = dataConfig; // O .getSagradosConfig(), según tu variable

        if (config == null || itemBuscado == null) return 0L;

        // 2. Recorrer IDs (Formato Nuevo Plano)
        for (String key : config.getKeys(false)) {
            // Leemos el item serializado
            String base64 = config.getString(key + ".item_serializado");

            if (base64 != null) {
                // Usamos el deserializador del FileDataManager
                org.bukkit.inventory.ItemStack itemGuardado = deserializeItemStack(base64);

                // 3. Si es el item que buscamos, devolvemos su fecha
                if (itemGuardado != null && itemGuardado.isSimilar(itemBuscado)) {
                    // getLong devuelve 0 si no existe, así que nunca será null
                    return config.getLong(key + ".fecha_guardado", 0L);
                }
            }
        }

        // Si no se encuentra, devolvemos 0 (nunca null)
        return 0L;
    }
    
    private String serializeItemStack(ItemStack item) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeObject(item);
        dataOutput.close();
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
    

}