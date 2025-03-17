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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Space extends Subject {

    public final Board board;

    public final int x;
    public final int y;
    private final List<Heading> walls = new ArrayList<>();
    private final List<FieldAction> actions = new ArrayList<>();
    private Player player;

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
    }

    /**
     * Gets the player in this space
     *
     * @return Player in this space
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player in this space
     *
     * @param player to set in this space
     */
    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer && (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    /**
     * Returns the walls (actually their direction) on this space.
     * Note that clients may change this list; this should, however,
     * be done only during the setup of the game (not while the game
     * is running).
     *
     * @return the list of walls on this space
     */
    public List<Heading> getWalls() {
        return walls;
    }

    /**
     * Evaluates whether this space has a wall in a given direction
     *
     * @param heading the direction to check
     * @return true if this space has a wall in the given direction, false otherwise
     */
    public boolean hasWallInDirection(Heading heading) {
        return getWalls().contains(heading);
    }

    /**
     * Returns the list of field actions on this space.
     * Note that clients may change this list; this should, however,
     * be done only during the setup of the game (not while the game
     * is running).
     *
     * @return the list of field actions on this space
     */
    public List<FieldAction> getActions() {
        return actions;
    }

    public boolean hasConveyorBelt() {
        for (FieldAction action : getActions()) {
            if (action instanceof ConveyorBelt) {
                return true;
            }
        }
        return false;
    }

    public ConveyorBelt getConveyorBelt() {
        if (!hasConveyorBelt()) {
            return null;
        }
        for (FieldAction action : getActions()) {
            if (action instanceof ConveyorBelt) {
                return (ConveyorBelt) action;
            }
        }
        return null;
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    /**
     * Get a string representation of this space
     *
     * @return a string representation of this space
     */
    public String toString() {
        return String.format("(%d,%d)", x, y);
    }
}
