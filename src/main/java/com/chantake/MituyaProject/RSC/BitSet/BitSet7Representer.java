package com.chantake.MituyaProject.RSC.BitSet;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Converts a BitSet into a YAML !b tag value. The BitSet is converted to a BigInteger that is printed as a decimal integer.
 *
 * @author Tal Eisenberg
 */
public class BitSet7Representer extends Representer {

    public BitSet7Representer() {
        this.representers.put(BitSet7.class, new RepresentBitSet7());
    }

    private class RepresentBitSet7 implements Represent {

        @Override
        public Node representData(Object data) {
            BitSet7 bits = (BitSet7)data;
            String value = BitSetUtils.bitSetToBigInt(bits).toString();
            return representScalar(new Tag("!b"), value);
        }

    }
}
