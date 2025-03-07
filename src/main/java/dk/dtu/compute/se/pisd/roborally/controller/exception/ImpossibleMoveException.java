package dk.dtu.compute.se.pisd.roborally.controller.exception;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class ImpossibleMoveException extends Exception {
    public ImpossibleMoveException(Player pusher, Space space, Heading heading) {
        super(String.format("Player %s can't ...", pusher.getName()));
    }
}
