package com.chantake.MituyaProject.RSC.Circuit.IO;

import com.chantake.MituyaProject.RSC.Circuit.Circuit;
import com.chantake.MituyaProject.RSC.Circuit.IO.InputPin.SourceType;
import com.chantake.MituyaProject.Tool.ChunkLocation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.NoteBlock;
import org.bukkit.material.Attachable;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;

/**
 * Represents a chip output pin.
 *
 * @author Tal Eisenberg
 */
public class OutputPin extends IOBlock {

    private static final Material[] outputMaterials = new Material[]{Material.LEVER, Material.REDSTONE_TORCH_OFF,
        Material.REDSTONE_TORCH_ON, Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK, Material.TRAP_DOOR,
        Material.POWERED_RAIL, Material.NOTE_BLOCK};

    private final List<Location> outputBlocks;

    /**
     * Constructs an OutputPin object.
     *
     * @param circuit Owner of the output pin.
     * @param outputBlock The pin block.
     * @param index The pin index in the circuit's output list.
     */
    public OutputPin(Circuit circuit, Location outputBlock, int index) {
        super(circuit, outputBlock, index);

        outputBlocks = new ArrayList<Location>();

        addOutputBlock(new Location(circuit.world, outputBlock.getBlockX(), outputBlock.getBlockY() + 1, outputBlock.getBlockZ()));
        addOutputBlock(new Location(circuit.world, outputBlock.getBlockX(), outputBlock.getBlockY() - 1, outputBlock.getBlockZ()));
        addOutputBlock(new Location(circuit.world, outputBlock.getBlockX() + 1, outputBlock.getBlockY(), outputBlock.getBlockZ()));
        addOutputBlock(new Location(circuit.world, outputBlock.getBlockX() - 1, outputBlock.getBlockY(), outputBlock.getBlockZ()));
        addOutputBlock(new Location(circuit.world, outputBlock.getBlockX(), outputBlock.getBlockY(), outputBlock.getBlockZ() + 1));
        addOutputBlock(new Location(circuit.world, outputBlock.getBlockX(), outputBlock.getBlockY(), outputBlock.getBlockZ() - 1));

    }

    private void addOutputBlock(Location loc) {
        if (!isPartOfStructure(loc)) {
            outputBlocks.add(loc);
        }
    }

    /**
     *
     * @return a list of blocks surrounding the output block that can receive a signal from this output pin.
     */
    public List<Location> getOutputBlocks() {
        return outputBlocks;
    }

    private static final BlockFace[] adjacentFaces = new BlockFace[]{BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    /**
     * Updates the state of blocks that are touching the output block.
     *
     * @param state The new output state.
     */
    public void setState(boolean state) {
        boolean hasActuator = false;

        for (Location l : outputBlocks) {
            if (shouldUpdateChunk(l)) {
                if (changeBlockState(l, state)) {
                    hasActuator = true;
                }
            }
        }

        if (!hasActuator) {
            for (Location l : outputBlocks) {
                InputPin i = circuit.getPlugin().getCircuitManager().getInputPin(l);

                if (i != null && shouldUpdateChunk(i.getLocation())) {
                    i.updateValue(loc.getBlock(), state, SourceType.DIRECT);
                }
            }
        }
    }

    private boolean changeBlockState(Location outputLoc, boolean state) {
        Block outputBlock = outputLoc.getBlock();

        if (outputBlock.getType() == Material.LEVER) {
            if (!checkAttached(outputBlock)) {
                return false;
            }
            updateLever(outputBlock, state);

        } else if (outputBlock.getType() == Material.POWERED_RAIL) {
            updatePoweredRail(outputBlock, state);

        } else if (outputBlock.getType() == Material.WOODEN_DOOR || outputBlock.getType() == Material.IRON_DOOR_BLOCK) {
            updateDoor(outputBlock, state);

        } else if (outputBlock.getType() == Material.TRAP_DOOR) {
            updateTrapDoor(outputBlock, state);

        } else if (outputBlock.getType() == Material.REDSTONE_TORCH_OFF || outputBlock.getType() == Material.REDSTONE_TORCH_ON) {
            if (!checkAttached(outputBlock)) {
                return false;
            }
            updateRedstoneTorch(outputBlock, state);

        } else if (outputBlock.getType() == Material.NOTE_BLOCK) {
            updateNoteBlock(outputBlock, state);

        } else {
            return false;
        }

        return true;

    }

    private boolean checkAttached(Block outputDevice) {
        Attachable a = (Attachable)outputDevice.getState().getData();
        BlockFace f = a.getAttachedFace();
        return f != null && outputDevice.getRelative(f).equals(loc.getBlock());
    }

    private void updateLever(Block outputBlock, boolean state) {
        if (updateBlockData(outputBlock, state)) {
            outputBlock.getState().update();
            Block b = loc.getBlock();
            byte oldData = b.getData();
            byte notData;
            if (oldData > 1) {
                notData = (byte)(oldData - 1);
            } else if (oldData < 15) {
                notData = (byte)(oldData + 1);
            } else {
                notData = 0;
            }
            b.setData(notData, true);
            b.setData(oldData, true);
        }
    }

    private void updateRedstoneTorch(Block outputBlock, boolean state) {
        byte oldData = outputBlock.getData();
        int type = (state ? Material.REDSTONE_TORCH_ON : Material.REDSTONE_TORCH_OFF).getId();
        outputBlock.setTypeIdAndData(type, oldData, true);
    }

    private void updatePoweredRail(Block outputBlock, boolean state) {
        if (updateBlockData(outputBlock, state)) {
            outputBlock.getState().update();
        }
    }

    private void updateNoteBlock(Block outputBlock, boolean state) {
        if (state) {
            NoteBlock note = (NoteBlock)outputBlock.getState();
            note.play();
        }
    }

    private void updateDoor(Block outputBlock, boolean state) {
        Block otherBlock = outputBlock.getRelative(BlockFace.UP);
        if (otherBlock.getType() != outputBlock.getType()) {
            otherBlock = outputBlock.getRelative(BlockFace.DOWN);
            if (otherBlock.getType() != outputBlock.getType()) {
                otherBlock = null;
            }
        }

        if (otherBlock != null) {
            BlockState s1 = outputBlock.getState();
            Door door = (Door)s1.getData();
            if (door.isOpen() != state) {
                door.setOpen(state);
                s1.setData(door);
                s1.update();

                BlockState s2 = otherBlock.getState();
                Door door2 = (Door)s2.getData();
                door2.setOpen(state);
                s2.setData(door2);
                s2.update();
                circuit.world.playEffect(outputBlock.getLocation(), Effect.DOOR_TOGGLE, 0);
            }
        }
    }

    private void updateTrapDoor(Block outputBlock, boolean state) {
        BlockState s = outputBlock.getState();
        MaterialData md = s.getData();
        byte oldData = md.getData();
        if (state) {
            md.setData((byte)(md.getData() | 0x4));
        } else {
            md.setData((byte)(md.getData() & 0x3));
        }
        if (oldData != md.getData()) {
            s.setData(md);
            s.update();
            circuit.world.playEffect(outputBlock.getLocation(), Effect.DOOR_TOGGLE, 0);
        }
    }

    private boolean updateBlockData(Block b, boolean state) {
        byte data = b.getData();
        boolean oldLevel = ((data & 0x08) > 0);
        if (oldLevel == state) {
            return false;
        }

        byte newData = (byte)(state ? data | 0x8 : data & 0x7);

        b.setData(newData, true);

        return true;
    }

    /**
     * Returns whether this output pin uses direct connections. This is true only when there are no other output devices connected to the output block.
     *
     * @return
     */
    public boolean isDirect() {
        for (Location l : outputBlocks) {
            Block b = l.getBlock();
            Material m = b.getType();
            if (m == Material.LEVER || m == Material.REDSTONE_TORCH_OFF || m == Material.REDSTONE_TORCH_ON) {
                Attachable a = (Attachable)b.getState().getData();
                BlockFace f = a.getAttachedFace();
                if (f != null && b.getRelative(f).equals(loc.getBlock())) {
                    return false;
                }
            } else if (m == Material.WOODEN_DOOR || m == Material.IRON_DOOR_BLOCK || m == Material.TRAP_DOOR
                    || m == Material.POWERED_RAIL || m == Material.NOTE_BLOCK) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @return The current state of the output block (on or off).
     */
    public boolean getState() throws CloneNotSupportedException {
        return circuit.getOutputBits().get(index);
    }

    /**
     *
     * @param material
     * @return true if an output pin can change the state of material.
     */
    public static boolean isOutputMaterial(Material material) {
        for (Material m : outputMaterials) {
            if (m == material) {
                return true;
            }
        }

        return false;
    }

    /**
     * Updates the state of the pin output blocks.
     */
    public void refreshOutputs() {
        try {
            boolean state = this.getState();
            for (Location l : this.outputBlocks) {
                if (ChunkLocation.fromLocation(l).isChunkLoaded()) {
                    changeBlockState(l, state);
                }
            }
        }
        catch (CloneNotSupportedException ex) {
            Logger.getLogger(OutputPin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean shouldUpdateChunk(Location l) {
        ChunkLocation chunk = ChunkLocation.fromLocation(l);
        return chunk.isChunkLoaded() && !circuit.getPlugin().getCircuitManager().isProcessingChunk(chunk);
    }
}
