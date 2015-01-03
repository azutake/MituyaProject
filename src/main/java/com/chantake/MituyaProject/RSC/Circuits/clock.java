package com.chantake.MituyaProject.RSC.Circuits;

import com.chantake.MituyaProject.RSC.BitSet.BitSet7;
import com.chantake.MituyaProject.RSC.Circuit.Circuit;
import com.chantake.MituyaProject.RSC.Wireless.Transmitter;
import com.chantake.MituyaProject.Tool.Parsing.UnitParser;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Tal Eisenberg
 */
public class clock extends Circuit {

    private long onDuration, offDuration;
    private boolean masterToggle;
    private BitSet7 onBits, offBits;
    private Runnable tickTask;
    private boolean ticking;
    private long expectedNextTick;
    private int taskId = -1;
    private Transmitter transmitter;

    @Override
    public void inputChange(int inIdx, boolean state) {
        if (masterToggle) {
            if (inIdx == 0) {
                if (state) {
                    startClock();
                } else {
                    stopClock();
                    sendBitSet(offBits);
                    if (transmitter != null) {
                        transmitter.transmit(false);
                    }
                }
            }
        } else {
            onBits.set(inIdx, state);

            if (onBits.isEmpty()) {
                stopClock();
                sendBitSet(offBits);
                if (transmitter != null) {
                    transmitter.transmit(false);
                }
            } else {
                startClock();
            }
        }
    }

    @Override
    protected boolean init(CommandSender sender, String[] args) {
        String channelArg = null;
        if (args.length > 0 && args[args.length - 1].startsWith("#")) {
            // last argument is a channel name
            channelArg = args[args.length - 1].substring(1);
            String[] newArgs = new String[args.length - 1];
            if (newArgs.length > 0) {
                System.arraycopy(args, 0, newArgs, 0, newArgs.length);
            }

            args = newArgs;
        }

        if (args.length == 0) {
            setDuration(1000, 0.5); // 1 sec default, 50% pulse width
        } else {
            double pulseWidth = 0.5;
            if (args.length > 1) {
                try {
                    pulseWidth = Double.parseDouble(args[1]);
                }
                catch (NumberFormatException ne) {
                    error(sender, "Bad pulse width: " + args[1] + ". Expecting a number between 0 and 1.0");
                    return false;
                }
            }

            try {
                long freq = Math.round(UnitParser.parse(args[0]));
                setDuration(freq, pulseWidth);
            }
            catch (IllegalArgumentException e) {
                error(sender, "Bad clock frequency: " + args[0]);
                return false;
            }
        }

        if (inputs.length != 1 && inputs.length != outputs.length) {
            error(sender, "Expecting the same amount of inputs and outputs or 1 input to control all outputs.");
            return false;
        }

        offBits = new BitSet7(outputs.length);
        offBits.set(0, outputs.length, false);

        onBits = new BitSet7(outputs.length);

        if (inputs.length == 1) {
            masterToggle = true;
            onBits.set(0, outputs.length);
        }

        if ((onDuration < 50 && onDuration > 0) || (offDuration < 50 && offDuration > 0)) {
            error(sender, "Clock frequency is currently limited to 50ms per state. Use a lower freq argument or try setting pulse-width to 0.");
            return false;
        }

        if (channelArg != null) {
            try {
                transmitter = new Transmitter();
                transmitter.init(sender, channelArg, 1, this);
            }
            catch (IllegalArgumentException ie) {
                error(sender, ie.getMessage());
                return false;
            }
        }
        info(sender, "The clock will tick every " + (onDuration + offDuration) + " milliseconds for " + onDuration + " milliseconds.");

        tickTask = new TickTask();

        expectedNextTick = -1;
        ticking = false;

        return true;
    }

    private void setDuration(long duration, double pulseWidth) {
        this.onDuration = Math.round(duration * pulseWidth);
        this.offDuration = duration - onDuration;
    }

    private void startClock() {
        if (ticking) {
            return;
        }

        if (hasDebuggers()) {
            debug("Turning clock on.");
        }

        ticking = true;
        currentState = true;

        taskId = redstoneChips.getServer().getScheduler().scheduleSyncDelayedTask(redstoneChips.getPlugin(), tickTask);
        if (taskId == -1) {
            if (hasDebuggers()) {
                debug("Tick task schedule failed!");
            }
            ticking = false;
        }
    }

    private void stopClock() {
        if (!ticking) {
            return;
        }

        if (hasDebuggers()) {
            debug("Turning clock off.");
        }

        redstoneChips.getServer().getScheduler().cancelTask(taskId);
        ticking = false;
    }

    @Override
    public void disable() {
        super.disable();
        stopClock();
    }

    @Override
    public void enable() {
        super.enable();
        if (masterToggle) {
            if (inputBits.get(0)) {
                startClock();
            }
        } else if (!onBits.isEmpty()) {
            startClock();
        }
    }

    @Override
    public void circuitShutdown() {
        stopClock();
    }
    boolean currentState = true;

    private class TickTask implements Runnable {

        @Override
        public void run() {
            if (!ticking) {
                return;
            }

            tick();

            long delay;

            if (onDuration == 0) {
                delay = offDuration;
            } else if (offDuration == 0) {
                delay = onDuration;
            } else {
                delay = (currentState ? onDuration : offDuration);
            }

            long correction = 0;
            if (expectedNextTick != -1) {
                correction = expectedNextTick - System.currentTimeMillis();
            }

            if (correction <= -delay) {
                correction = correction % delay;
            }
            delay += correction;
            expectedNextTick = System.currentTimeMillis() + delay;
            long tickDelay = Math.round(delay / 50.0);

            int id = redstoneChips.getServer().getScheduler().scheduleSyncDelayedTask(redstoneChips.getPlugin(), tickTask, tickDelay);

            if (id != -1) {
                taskId = id;
            } else {
                if (hasDebuggers()) {
                    debug("Tick task schedule failed!");
                }
            }

            currentState = !currentState;
        }

        private void tick() {
            if (onDuration > 0 && offDuration > 0) {
                sendBitSet(currentState ? onBits : offBits);
                if (transmitter != null) {
                    transmitter.transmit(currentState);
                }
            } else if (onDuration > 0) {
                sendBitSet(offBits);
                if (transmitter != null) {
                    transmitter.transmit(false);
                }
                sendBitSet(onBits);
                if (transmitter != null) {
                    transmitter.transmit(true);
                }
            } else if (offDuration > 0) {
                sendBitSet(onBits);
                if (transmitter != null) {
                    transmitter.transmit(true);
                }
                sendBitSet(offBits);
                if (transmitter != null) {
                    transmitter.transmit(false);
                }
            }
        }
    }
}
