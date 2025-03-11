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
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board;

    // arrays containing players that are on conveyor belts but could not be moved right away
    private List<Player> conveyorMovementRetryQueue = new ArrayList<>();
    private ArrayList<Player> conveyorMovementRetryQueueCopy = new ArrayList<>();

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
     * Move a player to a given space, recursively pushing other players if necessary.
     *
     * @param pusher player that is moving
     * @param space space where the player wants to be
     * @param heading direction of movement
     * @throws ImpossibleMoveException
     */
    private void moveToSpace(@NotNull Player pusher, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(pusher.getSpace(), heading) == space;
        Player pushed = space.getPlayer();
        if (pushed != null) {
            Space nextSpace = board.getNeighbour(space, heading);
            if (nextSpace != null) {
                moveToSpace(pushed, nextSpace, heading);
                assert space.getPlayer() == null : "the space the player wants isn't free!";
            } else {
                throw new ImpossibleMoveException(pusher, space, heading);
            }
        }
        pusher.setSpace(space);
    }

    /**
     * Start the programming phase by clearing the programming fields
     * for all players and generating random command cards in their
     * card fields.
     */
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

    /**
     * Generate a random command card
     *
     * @return a random command card
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Finish the programming phase, moving to the activation
     * phase.
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    /**
     * Make program fields visible for the given register
     *
     * @param register the register in which to make program fields visible
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * Make all program fields for all players invisible.
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     *
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Execute a step and continue programs.
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * Continue execution of players' programs
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Execute the next command for the next player
     */
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
                        this.conveyorMovementRetryQueue.clear(); //
                        this.conveyorMovementRetryQueueCopy.clear();
                        // Looping through the players to get the actions of the space they are on.
                        for (int i = 0; i < board.getPlayersNumber(); i++) {
                            currentPlayer = board.getPlayer(i);
                            Space space = currentPlayer.getSpace();
                            // execute all the actions for the given space.
                            for (FieldAction action : space.getActions()) {
                                action.doAction(this, space);
                            }
                        }

                        while (!this.conveyorMovementRetryQueue.isEmpty()) {
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

                            boolean listUnchanged = this.conveyorMovementRetryQueue.containsAll(this.conveyorMovementRetryQueueCopy) && this.conveyorMovementRetryQueue.containsAll(this.conveyorMovementRetryQueueCopy);
                            // if the list is of players is the same as in last iteration terminate loop.
                            if (listUnchanged) {
                                break;
                            }

                            this.conveyorMovementRetryQueueCopy = new ArrayList<>(this.conveyorMovementRetryQueue);
                            this.conveyorMovementRetryQueue.clear();

                            // loop through the list of player that have not performed conveyor belt action.
                            for (int i = this.conveyorMovementRetryQueueCopy.size() - 1; i >= 0; i--) {
                                Player player = this.conveyorMovementRetryQueueCopy.get(i);
                                Space space = player.getSpace();

                                for (FieldAction action : space.getActions()) {
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

    /**
     * Execute a command for a given player.
     *
     * @param player  player to execute command on
     * @param command command to execute
     */
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

    /**
     * Move a player forward in the direction they're facing
     *
     * @param player player to move
     */
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

    /**
     * Move a player two spaces forward in the direction they're facing
     *
     * @param player player to move
     */
    public void moveFastForward (@NotNull Player player){
        moveForward(player);
        // bør vi tjekke om den første er null så vi ikke printer to gange, i tilfælde af en wall på første træk
        moveForward(player);
    }

    /**
     * Move a player three spaces forward in the direction they're facing
     *
     * @param player player to move
     */
    public void moveFastFastForward (@NotNull Player player){
        moveForward(player);
        // bør vi tjekke om den første er null så vi ikke printer to gange, i tilfælde af en wall på første træk
        moveForward(player);
        // bør vi tjekke om den første er null så vi ikke printer to gange, i tilfælde af en wall på første træk
        moveForward(player);
    }

    /**
     * Turn a player to the right
     *
     * @param player player to turn
     */
    public void turnRight (@NotNull Player player){
        player.setHeading(player.getHeading().next());
    }

    /**
     * Turn a player to the left
     *
     * @param player player to turn
     */
    public void turnLeft (@NotNull Player player){
        player.setHeading(player.getHeading().prev());
    }

    /**
     * Turn a player to the opposite heading
     *
     * @param player player to turn
     */
    public void uTurn (@NotNull Player player){
        player.setHeading(player.getHeading().opposite());
    }

    /**
     * Moves the player backwards
     *
     * @param player is moved backwards
     */
    public void backward (@NotNull Player player) {
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

    /**
     * not yet implemented
     */
    public void again (@NotNull Player player){
        // TODO implement this
    }

    /**
     * Moves a command card from one command card field to another.
     *
     * @param source command card field containing the card
     * @param target command card field that the card is moved to
     * @return true if successful, false otherwise
     */
    public boolean moveCards (@NotNull CommandCardField source, @NotNull CommandCardField target){
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
    public void notImplemented () {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    /**
     * Move function for the conveyor belt to moves a player in the direction of the conveyor belt.
     *
     * @param player  player to be moved by conveyor belt
     * @param heading heading of the conveyor belt
     * @return boolean true if moves was succes false otherwise
     */
    public boolean moveInDirection(@NotNull Player player, @NotNull Heading heading) {
        // TODO needs testing. <- Is this done?
        Space neighbourSpace = board.getNeighbour(player.getSpace(), heading);

        //Checks if board.getNeighbour might return null, which is the case, if there is a wall in the direction of "heading"
        if (neighbourSpace == null) {
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

    public void addToConveyorRetryQueue(Player player) {
        conveyorMovementRetryQueue.add(player);
    }

}
