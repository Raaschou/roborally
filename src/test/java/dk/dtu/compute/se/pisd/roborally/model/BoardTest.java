package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardTest {

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
    void testGetNeighbour() {
        // wall EAST
        Space zz = gameController.board.getSpace(0, 0);
        zz.getWalls().add(Heading.EAST);
        Space result = gameController.board.getNeighbour(zz, Heading.EAST);
        Assertions.assertNull(result, "Neighbour was not null even though a wall was in the way in the same space");

        zz.getWalls().clear();
        Space neighbour = gameController.board.getSpace(1, 0);
        neighbour.getWalls().add(Heading.WEST);
        result = gameController.board.getNeighbour(zz, Heading.EAST);
        Assertions.assertNull(result, "Neighbour was not null even though a wall was in the way on the neighbour space");

        neighbour.getWalls().clear();
        result = gameController.board.getNeighbour(zz, Heading.EAST);
        Assertions.assertNotNull(result, "Neighbour was null even though no walls exist");

        // wall WEST
        zz = gameController.board.getSpace(1, 0);
        zz.getWalls().add(Heading.WEST);
        result = gameController.board.getNeighbour(zz, Heading.WEST);
        Assertions.assertNull(result, "Neighbour was not null even though a wall was in the way in the same space");

        zz.getWalls().clear();
        neighbour = gameController.board.getSpace(0, 0);
        neighbour.getWalls().add(Heading.EAST);
        result = gameController.board.getNeighbour(zz, Heading.WEST);
        Assertions.assertNull(result, "Neighbour was not null even though a wall was in the way on the neighbour space");

        neighbour.getWalls().clear();
        result = gameController.board.getNeighbour(zz, Heading.WEST);
        Assertions.assertNotNull(result, "Neighbour was null even though no walls exist");

        // wall NORTH
        zz = gameController.board.getSpace(0, 1);
        zz.getWalls().add(Heading.NORTH);
        result = gameController.board.getNeighbour(zz, Heading.NORTH);
        Assertions.assertNull(result, "Neighbour was not null even though a wall was in the way in the same space");

        zz.getWalls().clear();
        neighbour = gameController.board.getSpace(0, 0);
        neighbour.getWalls().add(Heading.SOUTH);
        result = gameController.board.getNeighbour(zz, Heading.NORTH);
        Assertions.assertNull(result, "Neighbour was not null even though a wall was in the way on the neighbour space");

        neighbour.getWalls().clear();
        result = gameController.board.getNeighbour(zz, Heading.NORTH);
        Assertions.assertNotNull(result, "Neighbour was null even though no walls exist");

        // wall SOUTH
        zz = gameController.board.getSpace(0, 0);
        zz.getWalls().add(Heading.SOUTH);
        result = gameController.board.getNeighbour(zz, Heading.SOUTH);
        Assertions.assertNull(result, "Neighbour was not null even though a wall was in the way in the same space");

        zz.getWalls().clear();
        neighbour = gameController.board.getSpace(0, 1);
        neighbour.getWalls().add(Heading.NORTH);
        result = gameController.board.getNeighbour(zz, Heading.SOUTH);
        Assertions.assertNull(result, "Neighbour was not null even though a wall was in the way on the neighbour space");

        neighbour.getWalls().clear();
        result = gameController.board.getNeighbour(zz, Heading.SOUTH);
        Assertions.assertNotNull(result, "Neighbour was null even though no walls exist");
    }

    // very simple tests but we want the coverage😍
    @Test
    void testGetters() {
        Assertions.assertNull(gameController.board.getGameId(), "Game id should be null");
        gameController.board.setGameId(69);
        Assertions.assertEquals(69, gameController.board.getGameId(), "Game id should be 69");

        Assertions.assertInstanceOf(Space.class, gameController.board.getSpace(0, 0), "Space (0, 0) should not be null");
        Assertions.assertNull(gameController.board.getSpace(-1, 1), "Space (-1, 1) should be null");

        Assertions.assertEquals(6, gameController.board.getPlayersNumber(), "Amount of players should be 6");

        Assertions.assertNull(gameController.board.getPlayer(-1), "Player number -1 should be null");
        Assertions.assertInstanceOf(Player.class, gameController.board.getPlayer(0), "Player number 0 should be a player");

        Assertions.assertInstanceOf(Player.class, gameController.board.getCurrentPlayer(), "Current player should be a player");

        Assertions.assertEquals(Phase.INITIALISATION, gameController.board.getPhase(), "Board should be in INITIALISATION phase");

        Assertions.assertEquals(0, gameController.board.getStep(), "Step should be 0");

        Assertions.assertFalse(gameController.board.isStepMode(), "Board should not be in step mode");

        Assertions.assertInstanceOf(Integer.class, gameController.board.getPlayerNumber(gameController.board.getCurrentPlayer()));
        Assertions.assertEquals(0, gameController.board.getPlayerNumber(gameController.board.getCurrentPlayer()));
        Assertions.assertEquals(-1, gameController.board.getPlayerNumber(new Player(new Board(0, 0), null, "name")));

        Assertions.assertEquals("Player 0, your next checkpoint is 1 | Current round: 1 | Current register: 0 | Phase: INITIALISATION", gameController.board.getStatusMessage(), "Status message is not right");

        Assertions.assertEquals(1, gameController.board.getCounter(), "Counter is not 1");

        Assertions.assertInstanceOf(Player.class, gameController.board.getNextPlayer(), "Next player is not a player");
        gameController.board.setCurrentPlayer(gameController.board.getPlayer(5));
        Assertions.assertEquals("Player 0", gameController.board.getNextPlayer().getName(), "Next player is not player 0");

        Assertions.assertEquals(0, gameController.board.getNoOfCheckpoints(), "Number of checkpoints is not 0");
    }

    @Test
    void testSetGameId() {
        Assertions.assertNull(gameController.board.getGameId(), "Game id should be null at init time");
        gameController.board.setGameId(2);
        Assertions.assertEquals(2, gameController.board.getGameId(), "Game id should be set");
        Assertions.assertThrows(IllegalStateException.class, () -> gameController.board.setGameId(3), "Should not override game id");
    }

    @Test
    void testSetPhase() {
        gameController.board.setPhase(Phase.PROGRAMMING);
        Assertions.assertEquals(Phase.PROGRAMMING, gameController.board.getPhase(), "Phase should be updated to PROGRMAMING");
    }

    @Test
    void testSetStep() {
        gameController.board.setStep(69);
        Assertions.assertEquals(69, gameController.board.getStep());
        gameController.board.setStep(70);
        Assertions.assertEquals(70, gameController.board.getStep());
    }

    @Test
    void testSetStepMode() {
        gameController.board.setStepMode(false);
        Assertions.assertFalse(gameController.board.isStepMode());
        gameController.board.setStepMode(true);
        Assertions.assertTrue(gameController.board.isStepMode());
    }

    @Test
    void testSetCounter() {
        int old = gameController.board.getCounter();
        gameController.board.setCounter(old + 1);
        Assertions.assertNotEquals(old, gameController.board.getCounter());
        Assertions.assertEquals(old + 1, gameController.board.getCounter());
    }

    @Test
    void testSetNoOfCheckpoints() {
        int old = gameController.board.getNoOfCheckpoints();
        gameController.board.setNoOfCheckpoints(old + 1);
        Assertions.assertNotEquals(old, gameController.board.getNoOfCheckpoints());
        Assertions.assertEquals(old + 1, gameController.board.getNoOfCheckpoints());
    }
}
