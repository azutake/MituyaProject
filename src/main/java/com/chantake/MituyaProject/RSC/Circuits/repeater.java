package com.chantake.MituyaProject.RSC.Circuits;

import com.chantake.MituyaProject.RSC.Circuit.Circuit;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Tal Eisenberg
 */
public class repeater extends Circuit {

    @Override
    public void inputChange(int idx, boolean state) {
        if (idx == 0) {
            for (int i = 0; i < outputs.length; i++) {
                sendOutput(i, state);
            }
        }
    }

    @Override
    protected boolean init(CommandSender sender, String[] args) {
        return true;
    }
}
