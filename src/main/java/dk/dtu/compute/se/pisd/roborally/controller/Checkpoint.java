package dk.dtu.compute.se.pisd.roborally.controller;


import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a checkpoint on a space
 */
public class Checkpoint extends FieldAction {

    private final int sequence;

    public Checkpoint(int sequence) {
        this.sequence = sequence;
    }

    /**
     * Get the sequence for this checkpoint
     * @return the sequence
     */
    public int getSequence() {
        return sequence;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        // TODO A3: needs to be implemented
        // ...

        return false;
    }
}
