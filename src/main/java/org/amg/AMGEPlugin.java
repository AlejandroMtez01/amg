package org.amg;

import net.milkbowl.vault.economy.Economy;
import org.amg.Comandos.*;
import org.amg.FileData.FileDataManager;
import org.amg.MenuListener.MenuListenerEliminarEncantamiento;
import org.amg.MenuListener.MenuListenerItemSagrados;
import org.amg.MenuListener.MenuListenerMejorarEncantamiento;
import org.amg.Otros.ItemManager;
import org.amg.Utils.UtilsMensajes;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class AMGEPlugin extends JavaPlugin {
    public static Economy economia;
    private ItemManager itemManager;
    private FileDataManager fileDataManager;
    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("No se encontró un plugin de economía compatible con Vault.");
            getServer().getPluginManager().disablePlugin(this);
        }
        // Inicializar managers
        this.fileDataManager = new FileDataManager(this);
        this.itemManager = new ItemManager(this, fileDataManager);

        getLogger().info(UtilsMensajes.NOMBRE_FORMAL+": Plugin  habilitado correctamente");
        //---------

        //Gestión de comandos.

        getCommand("eliminarEncantamiento").setExecutor(new EliminarEncantamiento(this));
        getCommand("mejorarEncantamiento").setExecutor(new MejorarEncantamiento(this));
        getCommand("renombrar").setExecutor(new Renombrar(this,itemManager));
        getCommand("vender").setExecutor(new Vender(this));
        //getCommand("guardaritemsagrado").setExecutor(new GuardarSagrados(this,itemManager));
        getCommand("sagrados").setExecutor(new Sagrados(this,itemManager));


        //Gestión de eventos.

        getServer().getPluginManager().registerEvents(new MenuListenerEliminarEncantamiento(), this);
        getServer().getPluginManager().registerEvents(new MenuListenerMejorarEncantamiento(this,itemManager), this);
        getServer().getPluginManager().registerEvents(new MenuListenerItemSagrados(this,itemManager), this);

    }

    @Override
    public void onDisable() {
        getLogger().info(UtilsMensajes.NOMBRE_FORMAL+": Plugin deshabilitado correctamente");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economia = rsp.getProvider();
        return economia != null;

    }

}