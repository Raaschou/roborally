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

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {

    final public static int NUMBER_OF_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;

    private String name;
    private String color;

    private Space space;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] cards;

    private int nextCheckpoint;
    private Command lastCommand;

    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.nextCheckpoint = 1;
        this.space = null;
        this.lastCommand =null;

        program = new CommandCardField[NUMBER_OF_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    /**
     * Gets a player's last command
     *
     * @return the last command of the player
     */
    public Command getLastCommand() {
        return lastCommand;
    }

    /**
     * Sets the last command of a player
     *
     * @param command the last command of the player
     */
    public void setLastCommand(Command command){
        this.lastCommand = command;
    }

    /**
     * Get the player's name
     *
     * @return name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of a player
     *
     * @param name the name the player is set to
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Gets the player's color
     *
     * @return the player color
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the color of the player
     *
     * @param color the color the player color is set to
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * Gets the player's current space
     * @return Player's current space
     */
    public Space getSpace() {
        return space;
    }

    /**
     * Update the player's current space
     * @param space space to set the player on
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    /**
     * Gets the player's current heading
     *
     * @return Player's current heading
     */
    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Get the programming field with a given index
     *
     * @param i index of the programming field
     * @return the programming field with index i
     */
    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    /**
     * Get the card in a programming field
     *
     * @param i index of the programming field
     * @return the card placed in the programming field with index i
     */
    public CommandCardField getCardField(int i) {
        return cards[i];
    }
    
    /**
     * Gets what checkpoint the player should move to
     *
     * @return nextCheckpoint attribute
     */
    public int getNextCheckpoint() {
        return nextCheckpoint;
    }

    /**
     * Increments the nextCheckpoint attribute
     */
    public void incrementNextCheckpoint() {
        this.nextCheckpoint++;
    }
}
