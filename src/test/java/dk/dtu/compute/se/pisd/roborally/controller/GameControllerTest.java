package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testing the functionality of the move and turn functions in the GameController.
 * That is:
 * move forwards, backwards and turning
 * bumping other players, checkpoints and walls
 */
class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;
    private ConveyorBelt conveyorBelt;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null, "Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    /**
     * Test for Assignment V1 (can be deleted later once V1 was shown to the teacher)
     */
    @Test
    void testV1() {
        Board board = gameController.board;

        Player player = board.getCurrentPlayer();
        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player, board.getSpace(0, 4).getPlayer(), "Player " + player.getName() + " should be on Space (0,4)!");
    }

    //The following tests should be used later for assignment V2

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() + "!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.setSpace(board.getSpace(0, 0));
        current.setHeading(Heading.SOUTH);
        gameController.moveForward(current);
        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should be on Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player  " + current.getName() + "  should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0, 0) should be empty!");

        // Testing that players move correct across edges of the infinite board
        current.setSpace(board.getSpace(0, 0));
        current.setHeading(Heading.NORTH);
        gameController.moveForward(current);
        int expected_y_position = TEST_HEIGHT - 1;
        Assertions.assertEquals(current, board.getSpace(0, TEST_HEIGHT - 1).getPlayer(), "Player " + current.getName() + " should be on Space (0, " + expected_y_position + ")!");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player " + current.getName() + " should be heading NORTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0, 0) should be empty!");

        // You shall not pass! - walls nor Gandalf. Goes for players, Balrogs and beings in between.
        current.setSpace(board.getSpace(5, 5));
        current.setHeading(Heading.SOUTH);
        Space space = board.getSpace(5, 5);
        space.getWalls().add(Heading.SOUTH);
        gameController.moveForward(current);
        Assertions.assertEquals(current, board.getSpace(5, 5).getPlayer(), "Player " + current.getName() + " has moved should be on space (5, 5)");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player " + current.getName() + " should be heading SOUTH!");
        // testing walls from behind
        current.setSpace(board.getSpace(6, 5));
        current.setHeading(Heading.SOUTH);
        space = board.getSpace(6, 6);
        space.getWalls().add(Heading.NORTH);
        gameController.moveForward(current);
        Assertions.assertEquals(current, board.getSpace(6, 5).getPlayer(), "Player " + current.getName() + " has moved should be on space (6, 5)");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player " + current.getName() + " should be heading SOUTH!");

        /*
         * TODO: Write missing tests for moveForward():
         *      for bumping X
         *      for chain bumping x
         *      for bumping against walls x
         *      for chain bumping against walls
         *      for bumping across game edge x
         */
    }

    // Uncomment these after merging with the implemented functions.
    @Test
    void moveFastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        // manually set player position and heading - not sure this is the correct way of doing it..
        current.setSpace(board.getSpace(0, 0));
        current.setHeading(Heading.SOUTH);
        gameController.moveFastForward(current);
        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should be on Space (0,2)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0, 0) should be empty!");
    }


    @Test
    void moveFastFastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.setSpace(board.getSpace(0, 0));
        current.setHeading(Heading.SOUTH);
        gameController.moveFastFastForward(current);
        Assertions.assertEquals(current, board.getSpace(0, 3).getPlayer(), "Player " + current.getName() + " should be on Space (0,3)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0, 0) should be empty!");
    }

    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnRight(current);
        Assertions.assertEquals(Heading.WEST, current.getHeading(), "Player 0 should be heading WEST!");
        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName() + " should be on Space (0,0)!");
    }

    @Test
    void turnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnLeft(current);
        Assertions.assertEquals(Heading.EAST, current.getHeading(), "Player 0 should be heading EAST!");
        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName() + " should be on Space (0,0)!");
    }

    @Test
    void uTurn() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.uTurn(current);
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player 0 should be heading NORTH!");
        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName() + " should be on Space (0,0)!");
    }

    @Test
    void backward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.setHeading(Heading.WEST);
        gameController.backward(current);
        Assertions.assertEquals(Heading.WEST, current.getHeading(), "Player 0 should be heading WEST!");
        Assertions.assertEquals(current, board.getSpace(1, 0).getPlayer(), "Player " + current.getName() + "should be on (1, 0");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0, 0) should be empty!");
    }

    @Test
    void again() {
        // needs implementation first.
    }

    @Test
    void bumpingMovingForward() {
        //Setting Players space and heading
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player neighbour = board.getNextPlayer();
        current.setSpace(board.getSpace(2, 2));
        current.setHeading(Heading.SOUTH);
        neighbour.setSpace(board.getSpace(2, 3));
        neighbour.setHeading(Heading.WEST);

        //Executing command "Forward"
        gameController.moveForward(current);

        //Checking Players new positions
        Assertions.assertEquals(current, board.getSpace(2, 3).getPlayer(), "Player " + current.getName() + " should be on Space (2,3)!");
        Assertions.assertEquals(neighbour, board.getSpace(2, 4).getPlayer(), "Player " + neighbour.getName() + " should be on Space (2,4)!");

        //Checking Players heading and initial space is empty
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player should be heading SOUTH!");
        Assertions.assertEquals(Heading.WEST, neighbour.getHeading(), "Neighbour should still be heading WEST!");
        Assertions.assertNull(board.getSpace(2, 2).getPlayer(), "Space (2, 2) should be empty!");
    }

    @Test
    void bumpingMovingBackwards() {
        //Setting Players space and heading
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player neighbour = board.getNextPlayer();
        current.setSpace(board.getSpace(6, 7));
        current.setHeading(Heading.SOUTH);
        neighbour.setSpace(board.getSpace(6, 6));
        neighbour.setHeading(Heading.NORTH);

        //Executing command "Backwards"
        gameController.backward(current);

        //Checking Players new positions
        Assertions.assertEquals(current, board.getSpace(6, 6).getPlayer(), "Player " + current.getName() + " should be on Space (6,6)!");
        Assertions.assertEquals(neighbour, board.getSpace(6, 5).getPlayer(), "Player " + neighbour.getName() + " should be on Space (6,5)!");

        //Checking Players heading and initial space is empty
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player should be heading SOUTH!");
        Assertions.assertEquals(Heading.NORTH, neighbour.getHeading(), "Neighbour should still be heading WEST!");
        Assertions.assertNull(board.getSpace(6, 7).getPlayer(), "Space (6, 7) should be empty!");
    }

    @Test
    void chainBumpingOverEdge() {
        //Setting Players space and heading
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player neighbour = board.getNextPlayer();
        Player neighboursNeighbour = new Player(board, null, "neighboursNeighbour");
        current.setSpace(board.getSpace(0, 2));
        current.setHeading(Heading.WEST);
        neighbour.setSpace(board.getSpace(7, 2));
        neighbour.setHeading(Heading.WEST);
        neighboursNeighbour.setSpace(board.getSpace(6, 2));
        neighboursNeighbour.setHeading(Heading.NORTH);

        //Executing command "Forward"
        gameController.moveForward(current);

        //Checking Players new positions
        Assertions.assertEquals(current, board.getSpace(7, 2).getPlayer(), "Player " + current.getName() + " should be on Space (7,2)!");
        Assertions.assertEquals(neighbour, board.getSpace(6, 2).getPlayer(), "Player " + neighbour.getName() + " should be on Space (6,2)!");
        Assertions.assertEquals(neighboursNeighbour, board.getSpace(5, 2).getPlayer(), "Player " + neighbour.getName() + " should be on Space (5,2)!");

        //Checking Players heading and initial space is empty
        Assertions.assertEquals(Heading.WEST, current.getHeading(), "Player should be heading WEST!");
        Assertions.assertEquals(Heading.WEST, neighbour.getHeading(), "Neighbour should still be heading WEST!");
        Assertions.assertEquals(Heading.NORTH, neighboursNeighbour.getHeading(), "Neighbours neighbour should still be heading NORTH!");
        Assertions.assertNull(board.getSpace(0, 2).getPlayer(), "Space (2, 2) should be empty!");
    }

    @Test
    void bumpingWithWall() {
        //Setting Players space, heading and setting wall
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player neighbour = board.getNextPlayer();
        Space wall = gameController.board.getSpace(6, 3);
        wall.getWalls().add(Heading.WEST);
        current.setSpace(board.getSpace(4, 3));
        current.setHeading(Heading.EAST);
        neighbour.setSpace(board.getSpace(5, 3));
        neighbour.setHeading(Heading.EAST);

        //Executing command "Forward"
        gameController.moveForward(current);

        //Checking Players new positions
        Assertions.assertEquals(current, board.getSpace(4, 3).getPlayer(), "Player " + current.getName() + " should still be on Space (4,3)!");
        Assertions.assertEquals(neighbour, board.getSpace(5, 3).getPlayer(), "Player " + neighbour.getName() + " should still be on Space (5,3)!");

        //Checking Players heading and checking space where Neighbour would be if not for a wall is still empty
        Assertions.assertEquals(Heading.EAST, current.getHeading(), "Player should be heading EAST!");
        Assertions.assertEquals(Heading.EAST, neighbour.getHeading(), "Neighbour should be heading EAST!");
        Assertions.assertNull(board.getSpace(6, 3).getPlayer(), "Space (6, 3) should be empty!");
    }

    @Test
    void fastFastForwardWithWall() {
        //Setting Players space, heading and setting wall
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Player neighbour = board.getNextPlayer();
        Space wall = gameController.board.getSpace(6, 4);
        wall.getWalls().add(Heading.EAST);
        current.setSpace(board.getSpace(3, 4));
        current.setHeading(Heading.EAST);
        neighbour.setSpace(board.getSpace(5, 4));
        neighbour.setHeading(Heading.SOUTH);

        // player 4 is at space (4,4) and is messing up this test, so we move that player to somewhere else
        board.getPlayer(4).setSpace(board.getSpace(0, 0));

        //Executing command "FastFastForward"
        gameController.moveFastFastForward(current);

        //Checking Players new positions
        Assertions.assertEquals(current, board.getSpace(5, 4).getPlayer(), "Player " + current.getName() + " should be on Space (5,4)!" + current.getSpace());
        Assertions.assertEquals(neighbour, board.getSpace(6, 4).getPlayer(), "Player " + neighbour.getName() + " should be on Space (6,4)!" + neighbour.getSpace());

        //Checking Players heading and checking space where Neighbour would be if not for a wall is still empty
        Assertions.assertEquals(Heading.EAST, current.getHeading(), "Player should be heading EAST!");
        Assertions.assertEquals(Heading.SOUTH, neighbour.getHeading(), "Neighbour should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(4, 4).getPlayer(), "Space (4, 4) should be empty! ");
        Assertions.assertNull(board.getSpace(7, 4).getPlayer(), "Space (7, 4) should be empty! ");
    }

    @Test
    void movingOnConveyorBelt() {
        // Setting player and heading
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(3, 4));
        current.setHeading(Heading.EAST);

        //Setting Conveyorbelt and executing action
        Space space = board.getSpace(3, 4);
        ConveyorBelt action = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);
        action.doAction(gameController, board.getSpace(3, 4));

        // Checking Players new position, heading and if old Space is empty
        Assertions.assertEquals(current, board.getSpace(2, 4).getPlayer(), "Player " + current.getName() + " should be on Space (2,4)!");
        Assertions.assertNull(board.getSpace(3, 4).getPlayer(), "Space (3, 4) should be empty!");
        Assertions.assertEquals(Heading.EAST, current.getHeading(), "Player should be heading EAST!");
    }


    @Test
    void conveyorbeltWithWall() {
        // Setting player and heading
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(6, 3));
        current.setHeading(Heading.NORTH);

        //Setting Wall to stop conveyorbelt action
        Space wall = gameController.board.getSpace(6, 3);
        wall.getWalls().add(Heading.NORTH);

        //Setting Conveyorbelt and executing action
        Space space = board.getSpace(6, 3);
        ConveyorBelt action = new ConveyorBelt();
        action.setHeading(Heading.NORTH);
        space.getActions().add(action);
        action.doAction(gameController, board.getSpace(6,3));

        // Checking Players position and heading
        Assertions.assertEquals(current, board.getSpace(6, 3).getPlayer(), "Player " + current.getName()
                + " should be on Space (6,3)!");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player should be heading EAST!");
    }

    @Test
    void testIncrementingChechpoint() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(3, 3));
        current.setHeading(Heading.NORTH);



        Space space = board.getSpace(3, 2);
        Checkpoint checkpoint1 = new Checkpoint(1);
        space.getActions().add(checkpoint1);

        space = board.getSpace(3, 1);
        Checkpoint checkpoint2 = new Checkpoint(2);
        space.getActions().add(checkpoint2);


        Assertions.assertEquals(1, current.getNextCheckpoint(), "Player " + current.getName()
                + " should be heading for first!");

        gameController.moveForward(current);

        checkpoint1.doAction(gameController, board.getSpace(3,2));

        Assertions.assertEquals(2, current.getNextCheckpoint(), "Player " + current.getName()
                + " should be heading for second!");

        gameController.moveForward(current);
        checkpoint2.doAction(gameController, board.getSpace(3,1));

        Assertions.assertEquals(3, current.getNextCheckpoint(), "Player " + current.getName()
                + " should be heading for third");

    }

    @Test
    void againTest(){
        Board board = gameController.board;
        Player current = board.getSpace(3,3).getPlayer();
        board.setCurrentPlayer(current);
        current.setHeading(Heading.NORTH);

        gameController.startProgrammingPhase();
        current.getProgramField(0).setCard(new CommandCard(Command.FORWARD));
        current.getProgramField(1).setCard(new CommandCard(Command.AGAIN));
        gameController.finishProgrammingPhase();
        gameController.executePrograms();



        Assertions.assertEquals(current, board.getSpace(3, 1).getPlayer(), "Player " + current.getName() + " should be on Space (3,1)!");

    }

    // TODO write tests for checkpoints. obs check points are checked before conveyor belt is executed.
    //      - probably not relevant for our case.

    // TODO and there should be more tests added for the different assignments eventually

}