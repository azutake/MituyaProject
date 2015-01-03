package com.chantake.MituyaProject.RSC.Circuits;

import com.chantake.MituyaProject.RSC.BitSet.BitSet7;
import com.chantake.MituyaProject.RSC.Circuit.BitSetCircuit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tal Eisenberg
 */
public class nand extends BitSetCircuit {

    @Override
    protected void bitSetChanged(int bitSetidx, BitSet7 set) {
        try {
            BitSet7 out = (BitSet7)inputBitSets[0].clone();

            for (int i = 1; i < this.inputBitSets.length; i++) {
                out.and(inputBitSets[i]);
            }

            out.flip(0, wordlength);
            this.sendBitSet(out);
        }
        catch (CloneNotSupportedException ex) {
            Logger.getLogger(nand.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
