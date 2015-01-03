package com.chantake.MituyaProject.RSC.Circuits;

import com.chantake.MituyaProject.RSC.BitSet.BitSetUtils;
import com.chantake.MituyaProject.RSC.Circuit.Circuit;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Tal Eisenberg
 */
public class dregister extends Circuit {

    private static final int clockIdx = 0;
    private static final int resetIdx = 1;

    @Override
    public void inputChange(int inIdx, boolean state) {
        if (inIdx == resetIdx && state) {
            this.sendBitSet(BitSetUtils.clearBitSet);
        } else if (inputBits.get(clockIdx)) {
            sendBitSet(inputBits.get(2, outputs.length + 2));
        }
    }

    @Override
    protected boolean init(CommandSender sender, String[] args) {
        if (inputs.length != outputs.length + 2) {
            sender.sendMessage("Expecting 2 more inputs than outputs. Found " + inputs.length + " input(s) and " + outputs.length + " output(s).");
            return false;
        } else {
            if (sender != null) {
                resetOutputs();
            }
            return true;
        }
    }

    @Override
    protected boolean isStateless() {
        return false;
    }
}
