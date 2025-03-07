package dk.dtu.compute.se.pisd.roborally.controller.exception;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * thrown when a move is not possible.
 */
public class ImpossibleMoveException extends Exception {
    public ImpossibleMoveException(Player pusher, Space space, Heading heading) {
        super(String.format("%s can't move in direction %s to %s", pusher.getName(), heading, space));
    }
}
