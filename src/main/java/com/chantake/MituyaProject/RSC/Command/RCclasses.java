package com.chantake.MituyaProject.RSC.Command;

import com.chantake.MituyaProject.RSC.Circuit.CircuitIndex;
import com.chantake.MituyaProject.RSC.Circuit.CircuitLoader;
import com.chantake.MituyaProject.RSC.RCPrefs;
import com.chantake.MituyaProject.Util.Paging.Pager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Tal Eisenberg
 */
public class RCclasses extends RCCommand {

    public static void printClassesList(CommandSender sender, List<CircuitIndex> libs) {
        String list = "";
        Collections.sort(libs, new CircuitIndexComparator());
        ChatColor infoColor = RCPrefs.getInfoColor();
        ChatColor errorColor = RCPrefs.getErrorColor();
        ChatColor otherColor = ChatColor.YELLOW;

        for (CircuitIndex lib : libs) {
            ChatColor color = infoColor;

            if (lib == null) continue;

            list += "\n";

            List<String> names = new ArrayList<>();
            for (Class c : lib.getCircuitClasses()) names.add(c.getSimpleName());
            Collections.sort(names);

            list += ChatColor.WHITE + lib.getIndexName() + " " + lib.getVersion() + ":\n   ";
            for (String name : names) {
                list += color + name + ", ";
                if (color == infoColor) color = otherColor;
                else color = infoColor;
            }
            list = list.substring(0, list.length() - 2) + "\n";

        }


        if (!list.isEmpty())
            Pager.beginPaging(sender, "Installed circuit classes", list, infoColor, errorColor);
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        List<CircuitIndex> libs = CircuitLoader.getCircuitLibraries();

        if (libs.isEmpty()) info(sender, "There are no circuit classes installed.");
        else {
            printClassesList(sender, libs);
        }
    }

    private static class CircuitIndexComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            return ((CircuitIndex) o1).getIndexName().compareTo(((CircuitIndex) o2).getIndexName());
        }
    }

}
