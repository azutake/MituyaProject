package com.chantake.MituyaProject.RSC.Circuits;

import com.chantake.MituyaProject.RSC.Circuit.Circuit;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Tal Eisenberg
 */
public class encoder extends Circuit {

    @Override
    public void inputChange(int inIdx, boolean newLevel) {
        if (inputBits.isEmpty()) {
            sendBitSet(inputBits);
        } else {
            sendInt(0, outputs.length, inputBits.length() - 1);
        }
    }

    @Override
    protected boolean init(CommandSender sender, String[] args) {
        if (inputs.length > Math.pow(2, outputs.length)) {
            error(sender, "Number of inputs must be no more than 2 to the power of the number of outputs.");
            return false;
        }

        return true;
    }
}
