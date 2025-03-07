package dk.dtu.compute.se.pisd.roborally.controller.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
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
    }

}
