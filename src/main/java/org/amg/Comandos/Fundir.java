package org.amg.Comandos;

import org.amg.AMGEPlugin;
import org.amg.Utils.UtilsMensajes;
import org.amg.Utils.UtilsMetodos;
import org.amg.Utils.UtilsVender;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Fundir implements CommandExecutor {
    private final AMGEPlugin plugin;

    public Fundir(AMGEPlugin plugin) {
        this.plugin = plugin;
    }
    private final String PERMISO_FUNDIR = "amg.fundir";


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //Condicione para que se pueda ejecutar
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return false;
        }

        Player jugador = (Player) sender;

        if (jugador.hasPermission(PERMISO_FUNDIR)) {


            if (args.length == 0) {
                Map<Material, Material> materialesFundibles = new HashMap<>();
                materialesFundibles.put(Material.GOLD_ORE, Material.GOLD_INGOT);
                materialesFundibles.put(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT);
                materialesFundibles.put(Material.RAW_GOLD, Material.GOLD_INGOT);
                materialesFundibles.put(Material.IRON_ORE, Material.IRON_INGOT);
                materialesFundibles.put(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT);
                materialesFundibles.put(Material.RAW_IRON, Material.IRON_INGOT);
                materialesFundibles.put(Material.COPPER_ORE, Material.COPPER_INGOT);
                materialesFundibles.put(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT);
                materialesFundibles.put(Material.RAW_COPPER, Material.COPPER_INGOT);
                materialesFundibles.put(Material.SAND, Material.GLASS);
                materialesFundibles.put(Material.RED_SAND, Material.GLASS);
                materialesFundibles.put(Material.CLAY_BALL, Material.BRICK);
                materialesFundibles.put(Material.NETHERRACK, Material.NETHER_BRICK);
                materialesFundibles.put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);

                UtilsMetodos.fundirMateriales(materialesFundibles, jugador);


            }
            return true;
        }else{
            jugador.sendMessage(UtilsMensajes.NOMBRE_INFORMAL+"§cNo tienes permiso para usar este comando.");
            return false;
        }

    }
}
