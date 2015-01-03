package com.chantake.MituyaProject.RSC.Circuit.IO;

import com.chantake.MituyaProject.RSC.Circuit.Circuit;
import org.bukkit.Location;

/**
 * Represents an IO chip block. Currently, this is either an input, output or interface block.
 *
 * @author Tal Eisenberg
 */
public abstract class IOBlock {

    /**
     * IOBlock type - output pin, input pin or interface block.
     */
    public static enum Type {

        OUTPUT(OutputPin.class),
        INPUT(InputPin.class),
        INTERFACE(InterfaceBlock.class);

        Class<? extends IOBlock> cls;

        Type(Class<? extends IOBlock> clazz) {
            this.cls = clazz;
        }

        public Class<? extends IOBlock> getIOClass() {
            return cls;
        }
    }

    protected Circuit circuit = null;
    protected Location loc = null;
    protected int index = -1;

    /**
     * Creates an IOBlock instance of the desired type.
     *
     * @param type IO block type.
     * @param c owner circuit of the block.
     * @param l block location.
     * @param index the block pin index in the circuit.
     * @return new IOBlock instance.
     */
    public static IOBlock makeIOBlock(Type type, Circuit c, Location l, int index) {
        switch (type) {
            case OUTPUT:
                return new OutputPin(c, l, index);
            case INPUT:
                return new InputPin(c, l, index);
            case INTERFACE:
                return new InterfaceBlock(c, l, index);
            default:
                return null;
        }
    }

    public IOBlock(Circuit c, Location l, int index) {
        this.circuit = c;
        this.loc = l;
        this.index = index;
    }

    /**
     *
     * @return The circuit of this input pin.
     */
    public Circuit getCircuit() {
        return circuit;
    }

    /**
     *
     * @return The location of the io block.
     */
    public Location getLocation() {
        return loc;
    }

    /**
     *
     * @return The index of the io block in its circuit.
     */
    public int getIndex() {
        return index;
    }

    protected boolean isPartOfStructure(Location b) {
        for (Location l : circuit.structure) {
            if (b.equals(l)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param blocks An array of IOBlocks
     * @return A Location array containing the locations of each IOBlock in the same order.
     */
    public static Location[] locationsOf(IOBlock[] blocks) {
        Location[] locs = new Location[blocks.length];

        for (int i = 0; i < locs.length; i++) {
            locs[i] = blocks[i].getLocation();
        }

        return locs;
    }

}
