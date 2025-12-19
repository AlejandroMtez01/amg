package org.amg.Utils;

import net.milkbowl.vault.economy.EconomyResponse;
import org.amg.AMGEPlugin;
import org.bukkit.entity.Player;

public class UtilsMetodosEconomicos {

    public  static void retirarDinero(Player jugador, double cantidad){
        EconomyResponse respuestaEconomica = AMGEPlugin.economia.withdrawPlayer(jugador,cantidad);
        jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"§eHan sido retirados §f"+cantidad+" §ede su cuenta corriente.");

    }
    public  static void ingresarDinero(Player jugador, double cantidad){
        EconomyResponse respuestaEconomica = AMGEPlugin.economia.depositPlayer(jugador,cantidad);
        jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"§eHan sido ingresados §f"+cantidad+" §ede su cuenta corriente.");
    }
}
