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

import dk.dtu.compute.se.pisd.roborally.controller.exception.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        Player currentPlayer = board.getCurrentPlayer();

        if (space.getPlayer() == null) {
            currentPlayer.getSpace().setPlayer(null);
            space.setPlayer(currentPlayer);
            board.setCounter(board.getCounter() + 1);
            board.setCurrentPlayer(board.getNextPlayer());
        }
    }

    /**
     * Method for pushing player
     */
    private void moveToSpace(@NotNull Player pusher, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(pusher.getSpace(), heading) == space;
        Player pushed= space.getPlayer();
        if (pushed!= null) {
            Space nextSpace= board.getNeighbour(space, heading);
            if (nextSpace!= null) {
                moveToSpace(pushed, nextSpace, heading);
                assert space.getPlayer() == null : "the space the player wants isn't free!";
            } else {
                throw new ImpossibleMoveException(pusher, space, heading);
            }
        }
       pusher.setSpace(space);
    }



    // XXX V2
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX V2
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    // XXX V2
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX V2
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX V2
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    // XXX V2
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    // XXX V2
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX V2
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        // Looping through the players to get the actions of the space they are on.
                        for (int i = 0; i < board.getPlayersNumber(); i++) {
                            currentPlayer = board.getPlayer(i);
                            Space space = currentPlayer.getSpace();
                            // execute all the actions for the given space.
                            for (FieldAction action: space.getActions()) {
                                action.doAction(this, space);
                            }
                        }
                        // Slet løs hvis du har en løsning :)
                        if (!ConveyorBelt.playersThatHaveNotMoved.isEmpty()) {
                            // Jeg mangler at greje hvordan listen med players der ikke er blevet rykket skal
                            // bladres i gennem til den er tom og samtidigt sikre at den Bliver tom på et tidspunkt!
                            //
                            // run through the list till it's empty.
                            // not sure how to yet.
                            for (int i = ConveyorBelt.playersThatHaveNotMoved.size() - 1; i >= 0; i--) {
                                Player player = ConveyorBelt.playersThatHaveNotMoved.get(i);
                                // TODO Heading is to be changed to the heading of the conveyor belt.
                                boolean gotPushed = this.pushInDirection(player, Heading.WEST); // space.ConveyorBelt.heading??
                                if (gotPushed) {
                                    ConveyorBelt.playersThatHaveNotMoved.remove(i); // Now safe to remove during iteration
                                }
                            }
                        }
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.moveFastForward(player);
                    break;
                case FAST_FAST_FORWARD:
                    this.moveFastFastForward(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                case BACKWARD:
                    this.backward(player);
                    break;
                case AGAIN:
                    this.again(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    // TODO V2
    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Heading heading = player.getHeading();
            Space neighbour = board.getNeighbour(player.getSpace(), heading);
            if (neighbour != null) {
                try {
                    moveToSpace(player, neighbour, heading);
                } catch (ImpossibleMoveException e) {
                    //empty... Overstående bliver implementeret om lidt...
                }
            }
        }
    }

    // TODO V2
    public void moveFastForward(@NotNull Player player) {
        moveForward(player);
        // bør vi tjekke om den første er null så vi ikke printer to gange, i tilfælde af en wall på første træk
        moveForward(player);
    }

    public void moveFastFastForward(@NotNull Player player) {
        moveForward(player);
        // bør vi tjekke om den første er null så vi ikke printer to gange, i tilfælde af en wall på første træk
        moveForward(player);
        // bør vi tjekke om den første er null så vi ikke printer to gange, i tilfælde af en wall på første træk
        moveForward(player);
    }

    // TODO V2
    public void turnRight(@NotNull Player player) {
        player.setHeading(player.getHeading().next());
    }

    // TODO V2
    public void turnLeft(@NotNull Player player) {
        player.setHeading(player.getHeading().prev());
    }

    public void uTurn(@NotNull Player player) {
        player.setHeading(player.getHeading().opposite());
    }

    /**
     * Moves the player backwards
     * @param player is moved backwards
     */
    public void backward(@NotNull Player player) {
        // august siger ok, jeg siger for dovent.
        Heading heading = player.getHeading().opposite();
        Space neighbour = board.getNeighbour(player.getSpace(), heading);
        if (neighbour != null) {
            try {
                moveToSpace(player, neighbour, heading);
            } catch (ImpossibleMoveException e) {
                //empty
            }
        }
    }

    // TODO slet min kommentar...
    // Den her venter vi lige med...
    public void again(@NotNull Player player) {

    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet.
     * This should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    /**
     * Push function for the conveyor belt to push a player in the direction of the conveyor belt.
     * @param player player to be pushed by conveyor belt
     * @param heading heading of the conveyor belt
     * @return boolean true if moves was succes false otherwise
     */
    public boolean pushInDirection(@NotNull Player player,@NotNull Heading heading) {
        Space neighbourSpace = board.getNeighbour(player.getSpace(), heading);
        // Slet løs hvis du har en løsning :)
        if (neighbourSpace.getPlayer() == null) {
            player.getSpace().setPlayer(null);
            neighbourSpace.setPlayer(player);
            return true;
        } else {
            return false;
        }
    }

}
