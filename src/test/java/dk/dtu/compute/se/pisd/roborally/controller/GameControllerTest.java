package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testing the functionality of the move and turn functions in the GameController.
 * That is:
 *      move forwards, backwards and turning
 *      bumping other players, checkpoints and walls
 */
class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

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

        Assertions.assertEquals(player, board.getSpace(0, 4).getPlayer(), "Player " + player.getName()
                + " should be on Space (0,4)!");
    }

    //The following tests should be used later for assignment V2

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName()
                + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be "
                + player2.getName() + "!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.setSpace(board.getSpace(0, 0));
        current.setHeading(Heading.SOUTH);
        gameController.moveForward(current);
        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName()
                + " should be on Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player  " + current.getName()
                + "  should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0, 0) should be empty!");

        // Testing that players move correct across edges of the infinite board
        current.setSpace(board.getSpace(0, 0));
        current.setHeading(Heading.NORTH);
        gameController.moveForward(current);
        int expected_y_position = TEST_HEIGHT - 1;
        Assertions.assertEquals(current, board.getSpace(0, TEST_HEIGHT - 1).getPlayer(), "Player "
                + current.getName() + " should be on Space (0, " + expected_y_position + ")!");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player " + current.getName()
                + " should be heading NORTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0, 0) should be empty!");

        // You shall not pass! - walls nor Gandalf. Goes for players, Balrogs and beings in between.
        current.setSpace(board.getSpace(5, 5));
        current.setHeading(Heading.SOUTH);
        Space space = board.getSpace(5, 5);
        space.getWalls().add(Heading.SOUTH);
        gameController.moveForward(current);
        Assertions.assertEquals(current, board.getSpace(5, 5).getPlayer(), "Player " + current.getName()
                + " has moved should be on space (5, 5)");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player " + current.getName()
                + " should be heading SOUTH!");
        // testing walls from behind
        current.setSpace(board.getSpace(6, 5));
        current.setHeading(Heading.SOUTH);
        space = board.getSpace(6, 6);
        space.getWalls().add(Heading.NORTH);
        gameController.moveForward(current);
        Assertions.assertEquals(current, board.getSpace(6, 5).getPlayer(), "Player " + current.getName()
                + " has moved should be on space (6, 5)");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player " + current.getName()
                + " should be heading SOUTH!");

        /*
         * TODO: Write missing tests for moveForward():
         *      for bumping
         *      for chain bumping
         *      for bumping against walls
         *      for chain bumping against walls
         *      for bumping across game edge
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
        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName()
                + " should be on Space (0,2)!");
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
        Assertions.assertEquals(current, board.getSpace(0, 3).getPlayer(), "Player " + current.getName()
                + " should be on Space (0,3)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0, 0) should be empty!");
    }

    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnRight(current);
        Assertions.assertEquals(Heading.WEST, current.getHeading(), "Player 0 should be heading WEST!");
        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName()
                + " should be on Space (0,0)!");
    }

    @Test
    void turnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnLeft(current);
        Assertions.assertEquals(Heading.EAST, current.getHeading(), "Player 0 should be heading EAST!");
        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName()
                + " should be on Space (0,0)!");
    }

    @Test
    void uTurn() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.uTurn(current);
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player 0 should be heading NORTH!");
        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName()
                + " should be on Space (0,0)!");
    }

    @Test
    void backward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        current.setHeading(Heading.WEST);
        gameController.backward(current);
        Assertions.assertEquals(Heading.WEST, current.getHeading(), "Player 0 should be heading WEST!");
        Assertions.assertEquals(current, board.getSpace(1, 0).getPlayer(), "Player " + current.getName()
                + "should be on (1, 0");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0, 0) should be empty!");
    }

    @Test
    void again() {
        // needs implementation first.
    }

    // TODO and there should be more tests added for the different assignments eventually

}