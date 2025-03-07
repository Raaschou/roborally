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
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents a conveyor belt on a space.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
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
     * Implementation of the action of a conveyor belt. Needs to be implemented for A3.
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {

        // TODO A3: needs to be implemented
        // this is a bit tricky since the order of doing it matter eg.
        // p1 p2 is on the same conveyor belt and p2 stands were p1 tries to
        // move to then p1 can get moved before p2 has been moved but if for
        // some reason p2 can't move p1 can't move either.
        //
        // So we should check if the space p1 are to be move to (if not occupied)
        // Im pretty sure we can do it if we make a list of the players who have not
        // been moved but might are to be moved by the conveyor belt and then iterate
        // through the list and pop the players who are moved in later or if they
        // cannot be moved
        //
        // i dont know how we can do it otherwise since we can bump players on belts
        // and we can have multiple players on the same space even though its only
        // for a period..

        Player currentPlayer = space.getPlayer();
        Heading heading = this.heading;
        gameController.moveInDirection(currentPlayer, heading); // <- Not yet implemented.

        return false;
    }

}
