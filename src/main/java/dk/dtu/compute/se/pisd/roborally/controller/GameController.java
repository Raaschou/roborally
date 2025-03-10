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

import java.util.ArrayList;

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
                        ConveyorBelt.playersThatHaveNotMoved.clear(); //
                        ConveyorBelt.copyPlayersThatHaveNotMoved.clear();
                        // Looping through the players to get the actions of the space they are on.
                        for (int i = 0; i < board.getPlayersNumber(); i++) {
                            currentPlayer = board.getPlayer(i);
                            Space space = currentPlayer.getSpace();
                            // execute all the actions for the given space.
                            for (FieldAction action: space.getActions()) {
                                action.doAction(this, space);
                            }
                        }

//                        int iter = 0; // til test slet har ikke slettet endnu skal måske bruges igen.
                        while (!ConveyorBelt.playersThatHaveNotMoved.isEmpty()) {
                            /*
                             * This loop makes sure that players that were initially blocked while conveyor belt tried
                             * to move them get another chance to be moved.
                             * The ConveyorBelt class creates a list of the players that couldn't be moved along with
                             * a copy that is used to check loop conditions.
                             * If the list of player that hasn't been moved is unchanged the loop terminates,
                             * otherwise it keeps running till there is no more players on conveyor belts
                             *
                             * We should consider outsourcing to helper functions
                             */

//                            // til test kan bare slettes hvis det virker.
//                            iter++;
//                            for (Player player: ConveyorBelt.playersThatHaveNotMoved) {
//                                System.out.print(player.getName() + " ");
//                                System.out.print(" iteration: " + iter);
//                            }
//                            System.out.println();  // test slut
                            boolean listUnchanged = ConveyorBelt.playersThatHaveNotMoved.containsAll(ConveyorBelt.copyPlayersThatHaveNotMoved) && ConveyorBelt.copyPlayersThatHaveNotMoved.containsAll(ConveyorBelt.playersThatHaveNotMoved);
                            // if the list is of players is the same as in last iteration terminate loop.
                            if (listUnchanged) {
                                break;
                            }
                            ConveyorBelt.copyPlayersThatHaveNotMoved = new ArrayList<>(ConveyorBelt.playersThatHaveNotMoved);
                            ConveyorBelt.playersThatHaveNotMoved.clear();

                            // loop through the list of player that have not performed conveyor belt action.
                            for (int i = ConveyorBelt.copyPlayersThatHaveNotMoved.size() - 1; i >= 0; i--) {
                                Player player = ConveyorBelt.copyPlayersThatHaveNotMoved.get(i);
                                Space space = player.getSpace();

                                // TODO figure a way to only do conveyor belt action.
                                // I don't think it's a problem for now since the space is the same as executed
                                // earlier and we dont have actions that 'stack' - yet.
                                for (FieldAction action: space.getActions()) {
                                    action.doAction(this, space);
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
     * Move function for the conveyor belt to moves a player in the direction of the conveyor belt.
     * @param player player to be moved by conveyor belt
     * @param heading heading of the conveyor belt
     * @return boolean true if moves was succes false otherwise
     */
    public boolean moveInDirection(@NotNull Player player,@NotNull Heading heading) {
        // TODO fix so that it handles walls blocking conveyor belts!
        // TODO needs testing.
        Space neighbourSpace = board.getNeighbour(player.getSpace(), heading);

        //Checks if board.getNeighbour might return null, which is the case, if there is a wall in the direction of "heading"
        if(neighbourSpace == null) {
            return false;
        }

        if (neighbourSpace.getPlayer() == null) {
            player.getSpace().setPlayer(null);
            neighbourSpace.setPlayer(player);
            return true;
        } else {
            return false;
        }
    }

}
