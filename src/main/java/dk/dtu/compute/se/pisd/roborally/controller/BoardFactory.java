package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.List;

/**
 * A factory for creating boards. The factory itself is implemented as a singleton.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
// XXX A3: might be used for creating a first slightly more interesting board.
public class BoardFactory {

    private final static String SIMPLE_BOARD_NAME = "Simple board";
    private final static String ADVANCED_BOARD_NAME = "Advanced board";
    private static final List<String> boardNames = List.of(SIMPLE_BOARD_NAME, ADVANCED_BOARD_NAME);
    /**
     * The single instance of this class, which is lazily instantiated on demand.
     */
    static private BoardFactory instance = null;
    private final int SIMPLE_BOARD_WIDTH = 8;
    private final int SIMPLE_BOARD_HEIGHT = 8;
    private final int ADVANCED_BOARD_WIDTH = 15;
    private final int ADVANCED_BOARD_HEIGHT = 8;

    /**
     * Constructor for BoardFactory. It is private in order to make the factory a singleton.
     */
    private BoardFactory() {
    }

    /**
     * Returns the single instance of this factory. The instance is lazily
     * instantiated when requested for the first time.
     *
     * @return the single instance of the BoardFactory
     */
    public static BoardFactory getInstance() {
        if (instance == null) {
            instance = new BoardFactory();
        }
        return instance;
    }

    /**
     * Get the list of possible board names.
     *
     * @return unmodifiable list of board names
     */
    public static List<String> getBoardNames() {
        return boardNames;
    }

    /**
     * Creates a new board of given name of a board, which indicates
     * which type of board should be created. For now the name is ignored.
     *
     * @param name the given name board
     * @return the new board corresponding to that name
     */
    public Board createBoard(String name) {
        Board board;
        switch (name) {
            case (SIMPLE_BOARD_NAME):
                board = new Board(SIMPLE_BOARD_WIDTH, SIMPLE_BOARD_HEIGHT, SIMPLE_BOARD_NAME);
                setupSimpleBoard(board);
                break;
            case (ADVANCED_BOARD_NAME):
                board = new Board(ADVANCED_BOARD_WIDTH, ADVANCED_BOARD_HEIGHT, ADVANCED_BOARD_NAME);
                setupAdvancedBoard(board);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized board name");
        }

        return board;
    }

    /**
     * Set up board objects for the simple board type.
     *
     * @param board the board instance to modify
     */
    private void setupSimpleBoard(Board board) {
        // Currently this is just an empty board.
        // We might add some objects on the board later.
    }

    /**
     * Set up board objects for the advanced board type.
     *
     * @param board the board instance to modify
     */
    private void setupAdvancedBoard(Board board) {
        // add some walls, actions and checkpoints to some spaces
        Space space = board.getSpace(0, 0);
        space.getWalls().add(Heading.SOUTH);
        ConveyorBelt action = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(1, 0);
        space.getWalls().add(Heading.NORTH);
        action = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(1, 1);
        space.getWalls().add(Heading.WEST);
        action = new ConveyorBelt();
        action.setHeading(Heading.NORTH);
        space.getActions().add(action);

        space = board.getSpace(5, 5);
        space.getWalls().add(Heading.SOUTH);
        action = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(6, 5);
        action = new ConveyorBelt();
        action.setHeading(Heading.WEST);
        space.getActions().add(action);

        space = board.getSpace(7, 5);
        Checkpoint act = new Checkpoint(1);
        space.getActions().add(act);

        space = board.getSpace(4, 4);
        act = new Checkpoint(2);
        space.getActions().add(act);
    }
}
