package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
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

    @Test
    void testGetPlayer() {
        Space zz = gameController.board.getSpace(0, 0);
        Player testPlayer = new Player(gameController.board, "black", "name");
        zz.setPlayer(testPlayer);
        Assertions.assertEquals(testPlayer, zz.getPlayer());
    }

    @Test
    void testSetPlayer() {
        Space zz = gameController.board.getSpace(0, 0);
        Player tp1 = new Player(gameController.board, "black", "name1");
        Player tp2 = new Player(gameController.board, "black", "name2");
        zz.setPlayer(tp1);
        Assertions.assertEquals(tp1, zz.getPlayer());

        zz.setPlayer(tp2);
        Assertions.assertNull(tp1.getSpace());
        Assertions.assertEquals(tp2, zz.getPlayer());
    }

    @Test
    void testGetActions() {
        // this is just a fucking getter, but i want 100% coverage so im testing it anyways lol
        Assertions.assertEquals(0, gameController.board.getSpace(0, 0).getActions().size());
    }

    @Test
    void testHasConveyorBelt() {
        Space zz = gameController.board.getSpace(0, 0);
        Assertions.assertFalse(zz.hasConveyorBelt());

        zz.getActions().add(new ConveyorBelt());
        Assertions.assertTrue(zz.hasConveyorBelt());
    }

    @Test
    void testGetConveyorBelt() {
        Space zz = gameController.board.getSpace(0, 0);
        Assertions.assertNull(zz.getConveyorBelt());

        zz.getActions().add(new ConveyorBelt());
        Assertions.assertInstanceOf(ConveyorBelt.class, zz.getConveyorBelt());
    }

    @Test
    void testToString() {
        Space zz = gameController.board.getSpace(0, 0);
        Assertions.assertEquals("(0,0)", zz.toString());
    }
}
