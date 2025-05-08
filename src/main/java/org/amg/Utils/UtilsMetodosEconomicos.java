package org.amg.Utils;

import net.milkbowl.vault.economy.EconomyResponse;
import org.amg.AMGEPlugin;
import org.bukkit.entity.Player;

public class UtilsMetodosEconomicos {

    public  static void retirarDinero(Player jugador, double cantidad){
        EconomyResponse respuestaEconomica = AMGEPlugin.economia.withdrawPlayer(jugador,cantidad);
    }
    public  static void ingresarDinero(Player jugador, double cantidad){
        EconomyResponse respuestaEconomica = AMGEPlugin.economia.depositPlayer(jugador,cantidad);
    }
}
