package com.chantake.MituyaProject.RSC.Command.Filters;

import com.chantake.MituyaProject.RSC.Chip.Chip;
import com.chantake.MituyaProject.Util.Locations;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tal Eisenberg
 */
public class LocationFilter implements ChipFilter {
    Location location;
    int radius = 0;

    @Override
    public Collection<Chip> filter(Collection<Chip> chips) {
        List<Chip> filtered = chips.stream().filter(chip -> Locations.isInRadius(location, chip.activationBlock, radius)).collect(Collectors.toList());

        return filtered;
    }

    @Override
    public void parse(CommandSender sender, String[] args) throws IllegalArgumentException {
        try {
            if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("this")) {
                if ((sender instanceof Player)) {
                    location = ((Player) sender).getLocation();
                }

                if (args.length == 2) {
                    radius = Integer.decode(args[1]);
                }

                return;
            }

            if (args.length != 3 && args.length != 4) {
                StringBuilder sb = new StringBuilder();
                for (String s : args) sb.append(s);

                throw new IllegalArgumentException("Bad location filter syntax: " + sb.toString() + ". Expecting 'location: x,y,z,[radius]'");
            }

            int x = Integer.decode(args[0]);
            int y = Integer.decode(args[1]);
            int z = Integer.decode(args[2]);

            if (args.length == 4) radius = Integer.decode(args[3]);
            location = new Location(null, x, y, z);
        } catch (NumberFormatException ne) {
            StringBuilder sb = new StringBuilder();
            for (String s : args) sb.append(s);

            throw new IllegalArgumentException("Bad location filter syntax: " + sb.toString() + ". Expecting 'location: x,y,z,[radius]'");
        }
    }
}
