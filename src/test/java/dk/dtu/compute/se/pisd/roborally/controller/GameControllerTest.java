package dk.dtu.compute.se.pisd.roborally.controller;

import com.sun.javafx.logging.jfr.JFRInputEvent;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.application.Platform;
import org.junit.jupiter.api.*;

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

    @BeforeAll
    static void beforeAll() {
        // start up java fx to allow unit tests for the "you've won" alert
        Platform.startup(() -> {
        });
    }

    @BeforeEach
    void beforeEach() {
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
     * Execute a runnable in the JavaFX thread, which allows us to test cases where the player wins.
     * @apiNote if not for this, testing a situation where a player wins would cause an exception, because
     * the "You've won" alert could not be displayed on the main thread.
     * @param runnable runnable to execute on the JavaFX thread
     */
    private void runWithInitializedJavaFX(Runnable runnable) {
        Platform.runLater(runnable);

        // give JavaFX time to process the thing and then move on
        // if not for this, the runnable will not be executed in time for the assertions
        // and tests will fail
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
    }

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
    void chainBumpingWithWalls() {
        Board board = gameController.board;
        Player player0 = gameController.board.getPlayer(0);
        Player player1 = gameController.board.getPlayer(1);
        Player player2 = gameController.board.getPlayer(2);
        player0.setHeading(Heading.NORTH);
        player1.setHeading(Heading.NORTH);
        player2.setHeading(Heading.NORTH);
        Space space = gameController.board.getSpace(7,7);
        player0.setSpace(space);
        space = gameController.board.getSpace(7,6);
        player1.setSpace(space);
        space = gameController.board.getSpace(7,5);
        player2.setSpace(space);
        space = gameController.board.getSpace(7,4);
        space.getWalls().add(Heading.NORTH);

        // test chain bumping.
        gameController.moveForward(player0);
        Assertions.assertEquals(board.getSpace(7, 6).getPlayer(), player0,  "player 0 should be at space (7, 6)!");
        Assertions.assertEquals(board.getSpace(7, 5).getPlayer(), player1,  "player 1 should be at space (7, 5)!");
        Assertions.assertEquals(board.getSpace(7, 4).getPlayer(), player2,  "player 2 should be at space (7, 4)!");
        // test chain bumping against wall
        gameController.moveFastForward(player0); // trying t bumping against wall.
        Assertions.assertEquals(board.getSpace(7, 6).getPlayer(), player0,  "player 0 should be at space (7, 6)!");
        Assertions.assertEquals(board.getSpace(7, 5).getPlayer(), player1,  "player 1 should be at space (7, 5)!");
        Assertions.assertEquals(board.getSpace(7, 4).getPlayer(), player2,  "player 2 should be at space (7, 4)!");
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
        action.doAction(gameController, board.getSpace(6, 3));

        // Checking Players position and heading
        Assertions.assertEquals(current, board.getSpace(6, 3).getPlayer(), "Player " + current.getName() + " should be on Space (6,3)!");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player should be heading EAST!");
    }

    @Test
    void checkpointIncrementPlayerNextCheckpoint() {
        //Creates board with 1 checkpoint
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(4, 4));
        current.setHeading(Heading.NORTH);

        //Creates checkpoint and does checkpoint action on the player,
        //which should increment nextCheckpoint attribute
        Space space = board.getSpace(4, 4);
        Checkpoint point = new Checkpoint(1);
        space.getActions().add(point);
        runWithInitializedJavaFX(() -> {
            point.doAction(gameController, board.getSpace(4, 4));
        });

        Assertions.assertEquals(2, current.getNextCheckpoint(), current.getName() + "next checkpoint should be 2");
    }

    @Test
    void checkpointCheckpointBeforeConveyor() {
        //Creates board with 1 checkpoint
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(4, 4));
        current.setHeading(Heading.NORTH);

        // creates a conveyor belt then a check point at the same space. execute doActions and check that check point
        // is correct. Obviously it is since we use addFirst for to add the check point to the FieldActions..
        Space space = board.getSpace(4, 4);
        Checkpoint point = new Checkpoint(1);
        ConveyorBelt belt = new ConveyorBelt();
        belt.setHeading(Heading.EAST);
        space.getActions().add(belt);
        space.getActions().addFirst(point);
        runWithInitializedJavaFX(() -> {
            for (FieldAction action : space.getActions()) {
                action.doAction(gameController, space);
            }
        });

        Assertions.assertEquals(2, current.getNextCheckpoint(), current.getName() + "next checkpoint should be 2");
        Assertions.assertEquals(current, board.getSpace(5, 4).getPlayer(), " " + current.getName() + " should be at space (5, 4)" );
    }

    @Test
    void tooEarlyOnCheckpoint() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(6, 4));
        current.setHeading(Heading.NORTH);
        board.setNoOfCheckpoints(3);

        Space space = board.getSpace(6, 3);
        Checkpoint point1 = new Checkpoint(1);
        space.getActions().add(point1);
        gameController.moveForward(current);
        point1.doAction(gameController, board.getSpace(6, 3));

        Assertions.assertEquals(2, current.getNextCheckpoint(), current.getName() + "next checkpoint should be 2");

        space = board.getSpace(6, 2);
        Checkpoint point2 = new Checkpoint(3);
        space.getActions().add(point2);
        gameController.moveForward(current);
        point2.doAction(gameController, board.getSpace(6, 2));
        Assertions.assertEquals(2, current.getNextCheckpoint(), current.getName() + "next checkpoint should still be 2");
        Assertions.assertFalse(gameController.isPlayerAWinner(current), "isPlayerAWinner should be false");

        space = board.getSpace(6, 1);
        Checkpoint point3 = new Checkpoint(2);
        space.getActions().add(point3);
        gameController.moveForward(current);
        point3.doAction(gameController, board.getSpace(6, 1));

        Assertions.assertEquals(3, current.getNextCheckpoint(), current.getName() + "next checkpoint should be 3");

        gameController.backward(current);
        runWithInitializedJavaFX(() -> {
            point2.doAction(gameController, board.getSpace(6, 2));
        });

        Assertions.assertEquals(4, current.getNextCheckpoint(), current.getName() + "next checkpoint should be 4");
        Assertions.assertTrue(gameController.isPlayerAWinner(current), "isPlayerAWinner should be true");
        Assertions.assertEquals(4, current.getNextCheckpoint(), current.getName() + "next checkpoint should be 4");
        Assertions.assertTrue(gameController.isPlayerAWinner(current), "isPlayerAWinner should be true");
        Assertions.assertSame(Phase.FINISHED, board.getPhase(), "Board should be in finished phase");
    }

    @Test
    void testStartWinning() {
        Assertions.assertNotEquals(Phase.FINISHED, gameController.board.getPhase(), "Phase should not be FINISHED before winning");
        runWithInitializedJavaFX(() -> {
            gameController.startWinning(gameController.board.getCurrentPlayer());
        });
        Assertions.assertEquals(Phase.FINISHED, gameController.board.getPhase(), "Phase should be FINISHED after winning");
    }

    @Test
    void gameWin() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(4, 3));
        current.setHeading(Heading.NORTH);
        board.setNoOfCheckpoints(2);

        Space space = board.getSpace(4, 3);
        final Checkpoint cp1 = new Checkpoint(1);
        space.getActions().add(cp1);

        runWithInitializedJavaFX(() -> {
            cp1.doAction(gameController, board.getSpace(4, 3));
        });

        //Creates another checkpoint and moves the player there and does checkpoint action
        space = board.getSpace(6, 6);
        final Checkpoint cp2 = new Checkpoint(2);
        space.getActions().add(cp2);
        current.setSpace(board.getSpace(6, 6));

        runWithInitializedJavaFX(() -> {
            cp2.doAction(gameController, board.getSpace(6, 6));
        });

        Assertions.assertEquals(3, current.getNextCheckpoint(), current.getName() + "next checkpoint should be 3");
        Assertions.assertTrue(gameController.isPlayerAWinner(current), "isPlayerAWinner should be true");
        Assertions.assertSame(Phase.FINISHED, board.getPhase(), "Board should be in finished phase");
    }

    @Test
    void testIncrementingCheckpoint() {
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

        Assertions.assertEquals(1, current.getNextCheckpoint(), "Player " + current.getName() + " should be heading for first!");

        gameController.moveForward(current);
        runWithInitializedJavaFX(() -> {
            checkpoint1.doAction(gameController, board.getSpace(3, 2));
        });

        Assertions.assertEquals(2, current.getNextCheckpoint(), "Player " + current.getName() + " should be heading for second!");

        gameController.moveForward(current);
        runWithInitializedJavaFX(() -> {
            checkpoint2.doAction(gameController, board.getSpace(3, 1));
        });

        Assertions.assertEquals(3, current.getNextCheckpoint(), "Player " + current.getName() + " should be heading for third");
    }

    @Test
    void testAgain() {
        Board board = gameController.board;
        Player current = board.getSpace(3, 3).getPlayer();
        board.setCurrentPlayer(current);
        current.setHeading(Heading.NORTH);

        gameController.startProgrammingPhase();
        current.getProgramField(0).setCard(new CommandCard(Command.FORWARD));
        current.getProgramField(1).setCard(new CommandCard(Command.AGAIN));
        gameController.finishProgrammingPhase();
        gameController.executePrograms();
        Assertions.assertEquals(current, board.getSpace(3, 1).getPlayer(), "Player " + current.getName() + " should be on Space (3,1)!");
    }

    @Test
    void testAgainAgain() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(5, 3));
        current.setHeading(Heading.NORTH);

        gameController.startProgrammingPhase();
        current.getProgramField(0).setCard(new CommandCard(Command.FORWARD));
        current.getProgramField(1).setCard(new CommandCard(Command.AGAIN));
        gameController.finishProgrammingPhase();
        Assertions.assertEquals(current, board.getSpace(5, 3).getPlayer(), "Player " + current.getName() + " should be on Space (5,3)!");
        gameController.executeStep();
        Assertions.assertEquals(current, board.getSpace(5, 2).getPlayer(), "Player " + current.getName() + " should be on Space (5,2)!");
        gameController.executeStep();//player 2
        gameController.executeStep();//player 3
        gameController.executeStep();//player 4
        gameController.executeStep();//player 5
        gameController.executeStep();//player 6
        gameController.executeStep();//player 1
        Assertions.assertEquals(current, board.getSpace(5, 1).getPlayer(), "Player " + current.getName() + " should be on Space (5,1)!");
    }

    @Test
    void testTwoPlayersOnAdjacentConveyors() {
        // ensure that when two adjacent conveyors with the same heading are occupied by two players,
        // both players are moved
        Board board = gameController.board;
        board.setNoOfCheckpoints(50); // prevent winning which causes an exception because the "YOU WON" alert has no parent window in tests
        Player p1 = board.getPlayer(0);
        Player p2 = board.getPlayer(1);
        Space s1 = board.getSpace(2, 4);
        Space s2 = board.getSpace(3, 4);
        ConveyorBelt c1 = new ConveyorBelt();
        c1.setHeading(Heading.WEST);
        ConveyorBelt c2 = new ConveyorBelt();
        c2.setHeading(Heading.WEST);

        //Put conveyor belts and players on their respective spaces
        s1.getActions().add(c1);
        s2.getActions().add(c2);
        p1.setSpace(s1);
        p2.setSpace(s2);

        gameController.startProgrammingPhase();
        gameController.finishProgrammingPhase();
        gameController.executePrograms();

        Assertions.assertEquals(p1, board.getSpace(1, 4).getPlayer(), p1.getName() + " should be on (1,4)");
        Assertions.assertEquals(p2, board.getSpace(2, 4).getPlayer(), p2.getName() + " should be on (2,4)");
    }

    @Test
    void callRestOfTheExecuteCommand() {
        Board board = gameController.board;
        board.setNoOfCheckpoints(999);
        Player current = board.getCurrentPlayer();
        current.setSpace(board.getSpace(0, 0));
        current.setHeading(Heading.SOUTH);

        gameController.startProgrammingPhase();
        current.getProgramField(0).setCard(new CommandCard(Command.RIGHT));
        current.getProgramField(1).setCard(new CommandCard(Command.LEFT));
        current.getProgramField(2).setCard(new CommandCard(Command.FAST_FORWARD));
        current.getProgramField(3).setCard(new CommandCard(Command.FAST_FAST_FORWARD));
        current.getProgramField(4).setCard(new CommandCard(Command.U_TURN));
        gameController.finishProgrammingPhase();
        gameController.executePrograms();

        //Assert if player has moved as expected
        Assertions.assertEquals(board.getSpace(0, 5), current.getSpace(), "Player should've moved two spaces south");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player should've turned around and be facing north");

        //Another round to get check last command card, backward
        gameController.startProgrammingPhase();
        current.getProgramField(0).setCard(new CommandCard(Command.BACKWARD));
        current.getProgramField(1).setCard(new CommandCard(Command.RIGHT_OR_LEFT));
        gameController.turnRightOrLeft(current, "Left");
        current.getProgramField(2).setCard(new CommandCard(Command.RIGHT_OR_LEFT));
        gameController.turnRightOrLeft(current, "Right");
        gameController.finishProgrammingPhase();
        gameController.executePrograms();

        Assertions.assertEquals(board.getSpace(0, 6), current.getSpace(), "Player should've moved one space back");
    }

    @Test
    void lineOfConveyorBeltsAndPlayers() {
        Board board = gameController.board;
        board.setNoOfCheckpoints(999);
        Player p1 = board.getPlayer(0);
        Player p2 = board.getPlayer(1);
        Player p3 = board.getPlayer(2);
        Player p4 = board.getPlayer(3);
        Space s1 = board.getSpace(1, 0);
        Space s2 = board.getSpace(2, 0);
        Space s3 = board.getSpace(3, 0);
        Space s4 = board.getSpace(4, 0);
        ConveyorBelt c1 = new ConveyorBelt();
        c1.setHeading(Heading.WEST);
        ConveyorBelt c2 = new ConveyorBelt();
        c2.setHeading(Heading.WEST);
        ConveyorBelt c3 = new ConveyorBelt();
        c3.setHeading(Heading.WEST);
        ConveyorBelt c4 = new ConveyorBelt();
        c4.setHeading(Heading.WEST);

        s1.getActions().add(c1);
        s2.getActions().add(c2);
        s3.getActions().add(c3);
        s4.getActions().add(c4);
        p1.setSpace(s4);
        p2.setSpace(s2);
        p3.setSpace(s3);
        p4.setSpace(s1);
        //Get all conveyor belts to run
        gameController.startProgrammingPhase();
        gameController.finishProgrammingPhase();
        gameController.executePrograms();

        Assertions.assertEquals(p1, board.getSpace(3, 0).getPlayer(), "Player 1 should be on (3,0)!");
        Assertions.assertEquals(p2, board.getSpace(1, 0).getPlayer(), "Player 2 should be on (1,0)!");
        Assertions.assertEquals(p3, board.getSpace(2, 0).getPlayer(), "Player 3 should be on (3,0)!");
        Assertions.assertEquals(p4, board.getSpace(0, 0).getPlayer(), "Player should be on (0,0)!");
    }
    //TODO Test for interactive card
    //TODO Test for retry queue if possible


    @Test
    void assertStatementsInExecuteNextStepWrongPhase() {
        gameController.board.setPhase(Phase.PROGRAMMING); // sets the phase to be wrong for executeNextStep.

        Assertions.assertThrows(AssertionError.class, () -> {
            gameController.executeStep();  // We call executeStep since we can't call executeNextStep (private)
        });
    }

    @Test
    void assertStatementInExecuteNextStepWrongStep() {
        gameController.board.setStep(-10); // sets the step to make no sense for executeNextStep
        //  Player player = gameController.board.getCurrentPlayer();
        gameController.board.setPhase(Phase.ACTIVATION);

        Assertions.assertThrows(AssertionError.class, () -> {
            gameController.executeStep();  // We call executeStep since we can't call executeNextStep (private)
        });
    }

    // TODO and there should be more tests added for the different assignments eventually

}