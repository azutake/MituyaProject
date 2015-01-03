package com.chantake.MituyaProject.RSC.Circuit.Filter;

import com.chantake.MituyaProject.RSC.Circuit.Circuit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Tal Eisenberg
 */
public class WorldFilter extends CircuitFilter {

    public World world;

    @Override
    public void parse(CommandSender sender, String[] string) throws IllegalArgumentException {
        if (string.length != 1) {
            StringBuilder sb = new StringBuilder();
            for (String s : string) {
                sb.append(s);
            }

            throw new IllegalArgumentException("Bad world filter: " + sb.toString() + ". Expecting 'world: <world-name>'.");
        } else {
            world = rc.getServer().getWorld(string[0]);
            if (world == null) {
                throw new IllegalArgumentException("Unknown world: " + string[0]);
            }
        }
    }

    @Override
    public Collection<Circuit> filter(Collection<Circuit> circuits) {
        List<Circuit> filtered = new ArrayList<>();

        for (Circuit circuit : circuits) {
            if (circuit.world.getName().equals(world.getName())) {
                filtered.add(circuit);
            }
        }

        return filtered;
    }
}
