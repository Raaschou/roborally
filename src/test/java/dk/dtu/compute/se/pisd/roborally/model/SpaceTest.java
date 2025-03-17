package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpaceTest {

    private GameController gameController;

    @BeforeEach
    void setup() {
        int TEST_WIDTH = 8;
        int TEST_HEIGHT = 8;
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

    @Test
    void testHasWallInDirection() {
        Space zz = gameController.board.getSpace(0, 0);
        zz.getWalls().add(Heading.EAST);
        boolean result = zz.hasWallInDirection(Heading.EAST);
        Assertions.assertTrue(result, "Result was false even though the space has a wall in the given direction");

        result = zz.hasWallInDirection(Heading.NORTH);
        Assertions.assertFalse(result, "Result was true even though the space has no wall in the given direction");
    }

}
