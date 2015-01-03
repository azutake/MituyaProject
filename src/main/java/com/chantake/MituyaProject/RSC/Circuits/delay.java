package com.chantake.MituyaProject.RSC.Circuits;

import com.chantake.MituyaProject.RSC.Circuit.Circuit;
import com.chantake.MituyaProject.Tool.Parsing.UnitParser;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Tal Eisenberg
 */
public class delay extends Circuit {

    private long interval;
    private long intervalInTicks;

    @Override
    public void inputChange(final int inIdx, final boolean state) {
        if (intervalInTicks == 0) {
            sendOutput(inIdx, state);
        } else {
            redstoneChips.getServer().getScheduler().scheduleSyncDelayedTask(redstoneChips.getPlugin(), new Runnable() {

                @Override
                public void run() {
                    sendOutput(inIdx, state);
                }
            }, intervalInTicks);
        }
    }

    @Override
    protected boolean init(CommandSender sender, String[] args) {
        if (inputs.length != outputs.length) {
            error(sender, "Expecting the same number of inputs and outputs.");
            return false;
        }

        if (inputs.length == 0) {
            error(sender, "Expecting at least one input and one output.");
        }

        if (args.length == 0) {
            interval = 1000; // 1 sec default
        }
        if (args.length >= 1) {
            try {
                interval = Math.round(UnitParser.parse(args[0]));
            }
            catch (IllegalArgumentException e) {
                error(sender, "Bad pulse duration argument: " + args[0]);
                return false;
            }
        }

        intervalInTicks = Math.round(interval / 50);

        return true;
    }
}
