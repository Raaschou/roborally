package dk.dtu.compute.se.pisd.roborally.controller;


import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a checkpoint on a space
 */

public class Checkpoint extends FieldAction {

    //Sequence uses Integer wrapper, so it is able to use toString() method in SpaceView
    //Might be a better way to do this?
    private Integer sequence = 0;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }   
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        // TODO A3: needs to be implemented
        // ...

        return false;
    }
}
