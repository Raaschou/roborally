/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a conveyor belt on a space.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3
public class ConveyorBelt extends FieldAction {

    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    /**
     * Implementation of the action of a conveyor belt.
     * Pushes players on the conveyor belt one space in the heading of the belt.
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {

        // this is a bit tricky since the order of doing it matter eg.
        // p1 p2 is on the same conveyor belt and p2 stands were p1 are to be
        // pushed to then p1 can't get pushed before p2 has been pushed but if for
        // some reason p2 can't move p1 can't move either.
        //
        // So we should check if the space p1 are to be move to (if not occupied)
        // Im pretty sure we can do it if we make a list of the players who have not
        // been moved but might are to be moved by the conveyor belt and then iterate
        // through the list and pop the players who are moved in later or if they
        // cannot be moved
        //
        // right now I can't see how we can do it otherwise since we can't bump players when
        // pushed on belts and we can have multiple players on the same space even though it's
        // only for a period..
        ArrayList<Space> conflictingSpaces = getConflictingConveyorSpaces(space);
        for (Space conflictingSpace : conflictingSpaces) {
            if (conflictingSpace.getPlayer() != null) {
                return false;
            }
        }

        Player currentPlayer = space.getPlayer();
        Heading heading = this.heading;
        boolean gotPushed = gameController.moveInDirection(currentPlayer, heading);
        if (!gotPushed) {
            gameController.addToConveyorRetryQueue(currentPlayer);
        }
        return false;
    }

    /**
     * Given a space with a conveyor belt on it find any other spaces with conveyor belts,
     * that point to the same target space.
     *
     * @param space A space for which to find conflicting spaces
     * @return a list of spaces with conveyors pointing to the same target as input space
     */
    private ArrayList<Space> getConflictingConveyorSpaces(Space space) {
        ArrayList<Space> conflictingSpaces = new ArrayList<>();
        ConveyorBelt belt = null;
        for (FieldAction action : space.getActions()) {
            if (action instanceof ConveyorBelt) {
                belt = (ConveyorBelt) action;
            }
        }
        if (belt == null) {
            // input space does not have a conveyor belt
            return conflictingSpaces;
        }
        Space target = space.board.getNeighbour(space, belt.getHeading());
        if (target == null) {
            // there is a wall between the conveyor space and target space, so there are
            // no conflicts with the input space, because it's conveyor does nothing
            return conflictingSpaces;
        }
        ArrayList<Heading> headingsToCheck = new ArrayList<Heading>(List.of(Heading.NORTH, Heading.EAST, Heading.SOUTH, Heading.WEST));
        headingsToCheck.remove(belt.getHeading().opposite());
        for (Heading heading : headingsToCheck) {
            Space potentiallyConflictingSpace = space.board.getNeighbour(target, heading);
            if (potentiallyConflictingSpace != null && potentiallyConflictingSpace.hasConveyorBelt()) {
                ConveyorBelt conflictingBelt = potentiallyConflictingSpace.getConveyorBelt();
                if (conflictingBelt.getHeading() == heading.opposite()) {
                    conflictingSpaces.add(potentiallyConflictingSpace);
                }
            }
        }
        return conflictingSpaces;
    }
}
