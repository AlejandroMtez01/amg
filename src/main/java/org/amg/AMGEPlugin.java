package org.amg;

import net.milkbowl.vault.economy.Economy;
import org.amg.Comandos.EliminarEncantamiento;
import org.amg.Comandos.MejorarEncantamiento;
import org.amg.Comandos.Renombrar;
import org.amg.Comandos.Vender;
import org.amg.MenuListener.MenuListenerEliminarEncantamiento;
import org.amg.MenuListener.MenuListenerMejorarEncantamiento;
import org.amg.Utils.UtilsMensajes;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class AMGEPlugin extends JavaPlugin {
    public static Economy economia;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("No se encontró un plugin de economía compatible con Vault.");
            getServer().getPluginManager().disablePlugin(this);
        }
        getLogger().info(UtilsMensajes.NOMBRE_FORMAL+": Plugin  habilitado correctamente");
        //---------

        //Gestión de comandos.

        getCommand("eliminarEncantamiento").setExecutor(new EliminarEncantamiento(this));
        getCommand("mejorarEncantamiento").setExecutor(new MejorarEncantamiento(this));
        getCommand("renombrar").setExecutor(new Renombrar(this));
        getCommand("vender").setExecutor(new Vender(this));

        //Gestión de eventos.

        getServer().getPluginManager().registerEvents(new MenuListenerEliminarEncantamiento(), this);
        getServer().getPluginManager().registerEvents(new MenuListenerMejorarEncantamiento(), this);

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