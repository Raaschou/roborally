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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * This represents a RoboRally game board. Which gives access to
 * all the information of current state of the games. Note that
 * the terms board and game are used almost interchangeably.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;
    private final Space[][] spaces;
    private final List<Player> players = new ArrayList<>();
    private Integer gameId;
    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private int counter = 1;

    private int noOfCheckpoints = 0;

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;

        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;

            }
        }
        this.stepMode = false;
    }

    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    /**
     * Get the space with given coordinates
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return space with the given coordinate
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    /**
     * Get the total number of players
     *
     * @return the number of players
     */
    public int getPlayersNumber() {
        return players.size();
    }

    /**
     * Add a player to the board
     *
     * @param player the player to be added
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    /**
     * Get the player with a given playernumber
     *
     * @param i the number of the player
     * @return the player with the given number
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * Gets the current player
     *
     * @return current player
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Sets the current player
     *
     * @param player the param is the current and it is set
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * Get the current board phase
     *
     * @return the phase the board is in
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Set the phase the board is in
     *
     * @param phase the phase the board is changed to
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * Get the step the board is in.
     * Is used in the GameController to keep hold of what registers is executed
     *
     * @return current step
     */
    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * Current step mode.
     * Used for the GameController to determine to run in step mode one action card at a time
     * or in execute all action cards.
     *
     * @return current stepmode
     */
    public boolean isStepMode() {
        return stepMode;
    }

    /**
     * Set the step mode
     * true if in stepmode false if in execute all steps
     *
     * @param stepMode step mode
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * Get the number of a given player
     *
     * @param player to get the number of
     * @return the number of the given player
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space   the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        Space neighbour;
        switch (heading) {
            case SOUTH:
                neighbour = getSpace(x, (y + 1) % height);
                if (!space.hasWallInDirection(Heading.SOUTH) && !neighbour.hasWallInDirection(Heading.NORTH)) {
                    return neighbour;
                }
                break;
            case WEST:
                neighbour = getSpace((x + width - 1) % width, y);
                if (!space.hasWallInDirection(Heading.WEST) && !neighbour.hasWallInDirection(Heading.EAST)) {
                    return neighbour;
                }
                break;
            case NORTH:
                neighbour = getSpace(x, (y + height - 1) % height);
                if (!space.hasWallInDirection(Heading.NORTH) && !neighbour.hasWallInDirection(Heading.SOUTH)) {
                    return neighbour;
                }
                break;
            case EAST:
                neighbour = getSpace((x + 1) % width, y);
                if (!space.hasWallInDirection(Heading.EAST) && !neighbour.hasWallInDirection(Heading.WEST)) {
                    return neighbour;
                }
                break;
        }

        return null;
    }

    /**
     * Get a status message with information of the current player
     *
     * @return status message
     */
    public String getStatusMessage() {
        return getCurrentPlayer().getName() + ", your next checkpoint is " + getCurrentPlayer().getNextCheckpoint() + " | Current round: " + this.getCounter() + " | " + "Current register: " + this.getStep() + " | Phase: " + this.getPhase();
    }

    /**
     * Gets the counter
     *
     * @return counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Sets the counter
     *
     * @param counter it is the counter to set.
     */
    public void setCounter(int counter) {
        this.counter = counter;
        notifyChange();
    }

    /**
     * Get the next player.
     *
     * @return the next player
     */
    public Player getNextPlayer() {
        return getPlayer((getPlayerNumber(getCurrentPlayer()) + 1) % getPlayersNumber());
    }

    /**
     * Get the number of check points
     *
     * @return the number of check points
     */
    public int getNoOfCheckpoints() {
        return this.noOfCheckpoints;
    }

    /**
     * Set the number of check points
     *
     * @param number the number of check points
     */
    public void setNoOfCheckpoints(int number) {
        this.noOfCheckpoints = number;
    }
}
