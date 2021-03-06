package com.chantake.MituyaProject.RSC.Command.Filters;

import com.chantake.MituyaProject.RSC.Chip.Chip;
import com.chantake.MituyaProject.RSC.Circuit.CircuitLoader;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tal Eisenberg
 */
public class TypeFilter implements ChipFilter {
    String cclass;

    @Override
    public void parse(CommandSender sender, String[] string) throws IllegalArgumentException {
        if (string.length != 1) {
            StringBuilder sb = new StringBuilder();
            for (String s : string) sb.append(s);

            throw new IllegalArgumentException("Bad class filter: " + sb.toString() + ". Expecting 'class: <chip class>'.");
        } else {
            for (String sclass : CircuitLoader.getCircuitClasses().keySet()) {
                if (sclass.startsWith(string[0])) {
                    cclass = sclass;
                    break;
                }
            }

            if (cclass == null)
                throw new IllegalArgumentException("Unknown chip class: " + string[0]);
        }
    }

    @Override
    public Collection<Chip> filter(Collection<Chip> chips) {
        List<Chip> filtered = chips.stream().filter(chip -> chip.getType().equalsIgnoreCase(cclass)).collect(Collectors.toList());

        return filtered;
    }

}
