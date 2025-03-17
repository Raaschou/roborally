package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ConveyorBeltTest {

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
    void testSetAndGetHeading() {
        ConveyorBelt belt = new ConveyorBelt();
        Assertions.assertNull(belt.getHeading());
        belt.setHeading(Heading.NORTH);
        Assertions.assertEquals(Heading.NORTH, belt.getHeading());
    }

    @Test
    void testDoAction() {
        Board b = gameController.board;
        Space zz = b.getSpace(0, 0);
        Player p = zz.getPlayer();
        ConveyorBelt belt = new ConveyorBelt();
        belt.setHeading(Heading.EAST);
        zz.getActions().add(belt);
        belt.doAction(gameController, zz);
        Assertions.assertNull(zz.getPlayer());
        Assertions.assertEquals(p, b.getSpace(1, 0).getPlayer());
    }

    @Test
    void testDoActionWithConflict() {
        Board b = gameController.board;
        Space s00 = b.getSpace(0, 0);
        Space s11 = b.getSpace(1, 1);
        ConveyorBelt b00 = new ConveyorBelt();
        b00.setHeading(Heading.EAST);
        s00.getActions().add(b00);
        ConveyorBelt b11 = new ConveyorBelt();
        b11.setHeading(Heading.NORTH);
        s11.getActions().add(b11);
        Player p00 = s00.getPlayer();
        Player p11 = s11.getPlayer();

        b00.doAction(gameController, s00);
        b11.doAction(gameController, s11);
        Assertions.assertEquals(p00, s00.getPlayer());
        Assertions.assertEquals(p11, s11.getPlayer());
        Assertions.assertNull(b.getSpace(1, 0).getPlayer());

        p00.setSpace(b.getSpace(3, 0));
        b00.doAction(gameController, s00);
        b11.doAction(gameController, s11);
        Assertions.assertNull(s11.getPlayer());
        Assertions.assertEquals(p00, b.getSpace(3, 0).getPlayer());
        Assertions.assertEquals(p11, b.getSpace(1, 0).getPlayer());
    }

    @Test
    void testGetConflictingConveyorSpaces() {
        Board b = gameController.board;
        Space zz = b.getSpace(0, 0);
        ConveyorBelt belt = new ConveyorBelt();
        belt.setHeading(Heading.EAST);
        Assertions.assertEquals(0, ConveyorBelt.getConflictingConveyorSpaces(zz).size());

        zz.getActions().add(belt);
        zz.getWalls().add(Heading.EAST);
        Assertions.assertEquals(0, ConveyorBelt.getConflictingConveyorSpaces(zz).size());

        Space oo = b.getSpace(1, 1);
        ConveyorBelt belt2 = new ConveyorBelt();
        belt2.setHeading(Heading.NORTH);
        oo.getActions().add(belt2);
        zz.getWalls().clear();
        Assertions.assertEquals(new ArrayList<>(List.of(oo)), ConveyorBelt.getConflictingConveyorSpaces(zz));
    }

    @Test
    void testDoActionAddingToRetryQueue() {
        Board b = gameController.board;
        Space zz = b.getSpace(0, 0);
        Space oz = b.getSpace(1, 0);
        ConveyorBelt bzz = new ConveyorBelt();
        bzz.setHeading(Heading.EAST);
        zz.getActions().add(bzz);
        ConveyorBelt boz = new ConveyorBelt();
        boz.setHeading(Heading.EAST);
        oz.getActions().add(boz);
        Player p1 = b.getSpace(0, 0).getPlayer();
        Player p2 = b.getSpace(1, 1).getPlayer();
        p1.setSpace(b.getSpace(0, 0));
        p2.setSpace(b.getSpace(1, 0));
        bzz.doAction(gameController, zz);
        boz.doAction(gameController, oz);

        // p1 should not have moved and p2 should have. the retry queue will move p1 later in an actual game
        Assertions.assertEquals(p2, b.getSpace(2, 0).getPlayer());
        Assertions.assertEquals(p1, b.getSpace(0, 0).getPlayer());
    }
}
