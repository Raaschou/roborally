package dk.dtu.compute.se.pisd.roborally.controller;


import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Player;
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

    /**
     * if the space has a player that has this checkpoint as the next checkpoint
     * to get the given players nextCheckPoint gets incremented
     * I have no idea how the true false is relevant
     * and im am pretty sure this is not the optimal way of doing it.
     * Consider refactoring the doActions when we find a better solution.
     *
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return true if player checkpoint number is incremented false otherwise
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player currentPlayer = space.getPlayer();
        if (currentPlayer.getNextCheckpoint() == this.getSequence()) {
            currentPlayer.incrementNextCheckpoint();
            return true;
        }
        return false;
    }
}
