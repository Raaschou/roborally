
# Assignment 4a)
- [x] overview
- [x] git 
- [x] implement dummy move functionality
- [x] java docs
- [x] tested implementation - The test passed
- [x] implemented counter
- [x] tested counter
- [x] updated getStatusMessage()
- [x] overview of the game rules

## Getting overview
We have downloaded the assignment4.zip and gotten an overview of the core functionality of the controller and the model
packages.

## GitHub
We have made a GitHub repository from which we can make branches and commit the progress of the RoboRally 
implementation

## Implement counter
We have implemented counter and added a setter and getter for this, and we've updated the getStatusMessage() method to
show the number of steps the current player has made.

## Implement moveCurrentPlayerToSpace
We have implemented the method controller.GameController.moveCurrentPlayerToSpace():

```
public void moveCurrentPlayerToSpace(@NotNull Space space) {
    Player currentPlayer = board.getCurrentPlayer();

    if (space.getPlayer() == null) {
        currentPlayer.getSpace().setPlayer(null);
        space.setPlayer(currentPlayer);
        board.setCounter(board.getCounter() + 1);
        board.setCurrentPlayer(board.getNextPlayer());
    }
}
```


## Write documentation
We have written JavaDocs for the implemented methods and for the methods we've used

## Test implementations
We have tested the implemented move functionality using the already implemented test and tested manually


# Assignment 4b)
- [] added game board 
- [] made the new game board selectable
- [] made gui for walls
- [] made gui for conveyor belts
- [] made gui for check points
- [] implemented method view.SpaceView.updateView()
- [] written javaDocs for implementations and uses 


# Assignment 4c)
- [] implemented the existing command cards
  - [] moveForward
  - [] moveFastForward
  - [] turnRight
  - [] turnLeft
- [] implemented walls blocking player movement
- [] implemented buttons in playerView
  - [] Finish Programming
  - [] Execute Program
  - [] Execute Current Register
- [] added command cards
  - [] uTurn
  - [] moveBackwards
- [] implemented uTurn and moveBackwards functionality
- [] written javaDocs for implementations and uses
- [] tested implementations with tests and manually
- [] added tests for new functionality


# Assignment 4d)
- [] implemented bumping of other players
- [] implemented field actions - executed after command card execution
  - [] conveyor belt
  - [] check points
- [] updated the controller.GameController.executeNextStep() to execute field actions
- [] added status label with info of players current checkpoint
- [] written javaDocs for implementations and uses

# Assignment 4e) 
- [] implemented winning conditions
- [] added (interactive) command card "Turn Left or Right" 
- [] implemented interactive command cards functionality - interrupts the 'game loop'
- [] written javaDocs fro implementations and uses
- [] added tests for new functionality 