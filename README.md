
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
- [x] added game board 
- [x] made the new game board selectable
- [ ] updated gui
  - [ ] made gui for walls
  - [x] made gui for conveyor belts
  - [x] made gui for check points
- [ ] implemented method view.SpaceView.updateView()
- [x] written javaDocs for implementations and uses 

## Added advanced game board
We have implemented and added an game board, 'advanced board'. 
When a game i started the players are prompted to select a game board after they chosen the number of players.
The 'advanced board' contains some walls, conveyor belts and check points.

## GUI update
We have added the visual of walls, conveyor belts and check points to the GUI. TODO: "Den der har lavet dette beskriv gerne hvordan".

### Field actions
We added two new methods to the SpaceView class for drawing the objects. 
As the conveyor belts and checkpoints both inherited the FieldAction class, and are contained within a given space's
list of field actions, we use this to draw the objects on the board. The belts are drawn as grey triangles, while the 
checkpoints are drawn as yellow circles with their corresponding number in the center. Because the field action list is
referring to an abstract class, we were not able to call the specific fields of the belts and checkpoints. This was 
solved by checking the object using the instanceof operator before casting it to the given type. After this was done,
we were able to use the specific fields, e.g. using conveyor belts heading to draw them pointing the correct direction.

### Walls

## Documentation
We have written JavaDocs for all the methods we have implemented and for the methods we have used.


# Assignment 4c)
- [x] implemented the existing command cards
  - [x] moveForward
  - [x] moveFastForward
  - [x] turnRight
  - [x] turnLeft
- [ ] implemented walls blocking player movement
- [x] implemented buttons in playerView TODO: Er det nok/for meget? De havde trods alt skrevet det meste af koden.
  - [x] Finish Programming
  - [x] Execute Program
  - [x] Execute Current Register
- [x] added command cards
  - [x] uTurn
  - [x] moveBackwards
- [x] implemented uTurn and moveBackwards functionality
- [ ] written javaDocs for implementations and uses
- [x] added tests for new functionality
- [x] tested implementations with tests and manually

## Implementing command cards
We have implemented the functionality of several command cards for the gameplay.
As well as adding new commands (moveBackwards and uTurn) and their functionality.
The moves moveForward and moveFastForward first checks the heading of the player.
It determines the next space in that direction using board.getNeighbour().
If a valid neighboring space exists, it attempts to move the player there using moveToSpace().
If the move is blocked by a wall, it catches and ignores the ImpossibleMoveException. If there is a
another player at the Neighbour space, it will check if that player can be pushed in the direction of the
player pushing. The move moveFastForward doesn't have its own functionality, rather it calls the command card
moveForward twice.
The moves turnRight and turnLeft works in roughly the same way.
The cards both use the player.setHeading and then calls the getHeading.next() or getHeading.prev().
As the heading is of the datatype enum we can call the previous or the next heading of the list and change it.
The same functionality applies to the moves uTurn and moveBackwards. The uTurn uses getHeading.opposite() and
turns the player 180 degrees. moveBackwards gets the heading of the player and turns it in the same way as the
uTurn. It then checks the same way as the moveForward if it can move in that direction. Importantly the method
player.setHeading isn't used as the player should not change heading when moving backwards.

## Implementation of buttons in playerView
We implemented the buttons, so the program is interactable and playable after laying the programming cards. The "Finish
Programming" button initialises the activation phase, which then requires the press of "Execute Program" or "Execute 
Current Register". The buttons respectively executes the entire program, going through all the cards sequentially, or 
executes the program one player register at a time. At any point it is possible to execute the rest of the program using
the "Execute Program" button.

## Added tests 
We have added tests in GameControllerTest that tests the implementations of the new functionality:
  - added tests for the moveForward()
  - added test for turnRight()
  - added test for turnLeft()
  - added test for uTurn()
  - added test for moveFastForward()
  - added test for moveFastFastForward
  - added test for moveBackwards()

## Testing 
Besides running (and passing) the implemented tests we have done manual testing of the newly implemented functionality.


# Assignment 4d)
- [x] implemented bumping of other players
- [x] implemented field actions - executed after command card execution
  - [x] conveyor belt
  - [x] check points
- [x] updated the controller.GameController.executeNextStep() to execute field actions
- [ ] added again commandCard
- [ ] added status label with info of players current checkpoint
- [x] written javaDocs for implementations and uses

## Implementation of bumping other players
For bumping other players the method moveToSpace() has been implemented. The method takes
a player, their heading and space. It then checks if the space it has to move to is free.
If the space is free the method simply moves the player to the new space. If the space is not free
the method then recursively calls itself to check whether the neighbour is able to move or not. If the
neighbour has a space free next to it, it will then move in the direction of the pusher.
The method throws an ImpossibleMoveException if the move is not able to be made in the case of a wall.


## Implementation of fieldActions
We have implemented the doAction() in both controller.Checkpoint and controller.ConveyorBelt  
the field actions is executed for all the space on which there is a player located and the field actions is executed 
after the all the command card in a turn has been executed. within the controller.GameController.executeNextStep() 
method.
The check points field action is executed before the conveyor belt's. This is done by adding check points to the first 
index of the space's fieldAction arrayList in the controller.BoardFactory. It's not really necessary since we haven't
made any boards that has both a conveyor belt and a check point at the same space.

The fieldActions is executed by the helper method controller.GameController.executeFieldActions()

### Check points
We have added a field 'nextCheckpoint' in the model.Player class that keeps track of the next check point the given
player needs to get. In the controller.CheckPoint the doAction is implemented by checking if the players nextCheckpoint
is equal to the 'sequence' of the check point on which the player is located. If so the the players nextCheckpoint is
incremented by the model.Player.incrementNextCheckpoint() method. 

### Conveyor Belts
The controller.ConveyorBelt.doAction() does a couple of things. First it performes a check if there are multiple players
that tries to get to the same space and if so neither of the players will get moved by the conveyor belts. 
secondary it moves players in the direction of the conveyor belt heading using 
controller.GameController.moveInDirection() method. the GameController also keeps track of players that couldn't get 
moved right away and add these players to a queue after doActions is performed for all the spaces that has a player 
located then the queue holding players on conveyor belts that has not moved and execute the conveyor belt doAction  once
again. then it check if the queue has changed is so it runs again till the queue is either empty or unchanged. This 
situation can occur if e.g. two players p1 and p2 are on the same conveyor belt and p1 is located right behind p2 then 
p1 won't be able to move initially p1 is added to the queue, p2 is moved then the queue is checked and p1 is then moved.
This makes sure the games conveyor belts behaves similar regardless of player's positioning. 

This functionality is implemented it the helper method controller.GameController.processBlockedConveyorPlayers(). 

## Updated GameController
We have updated the controller.GameController.executeNextStep() method to call the implemented helper methods
executeFieldActions() and processBlockedConveyorPlayers() which is called after the all the registers have been 
executed.

## Updated status label
The status label made in 4a was only incrementing on moving the players with mouse clicks. We changed this to show 
useful information pertaining to the current state of the game. It shows the current player and their next checkpoint,
the current round and what register is current, and lastly the phase of the game.

## Documentation
We have written JavaDocs for the implemented methods and for methods we have used that didn't already have JavaDocs.

## Testing
We have written tests for all the implemented functionality. That includes:
- Bumping other players when moving forward
- Bumping other players when moving backwards
- Bumping other players when moving fastForward
- Bumping other players with wall
- Conveyor belt movement 
- Check point incrementing is correct

Besides the tests is checked, they all passed we have tested the new game functionality manually.


# Assignment 4e) 
- [x] implemented winning conditions
- [x] added (interactive) command card "Turn Left or Right" 
- [x] implemented interactive command cards functionality - interrupts the 'game loop' 
- [x] written javaDocs for implementations and uses
- [x] added tests for new functionality 

## Winning conditions
For the win conditions, we chose add the noOfCheckpoints attribute to the board class. This is compared to the
players nextCheckpoint to see if the player potentially is a winner. This was done by comparing the nextCheckpoint to 
noOfCheckpoints + 1 in the helper method isPlayerAWinner(). This condition was added to the doAction of the checkpoints.
If this condition is true the startWinning() method is used to set the phase is set to FINISHED, as to not continue
the game after having a winner. A popup window is displayed with the name of the player who has won, and the final state
of the game is viewable. 

## Interactive card
We have implemented the card "Turn Left or Right" that is an interactive card where the current player is prompted to 
chose to turn in one of the two directions. 
We first added the command card "Turn Left or Right" to the enum model.Command.
We then implemented the functionality by switching the game phase to INTERACTIVE_PLAYER within the 
controller.GameController.executeCommand() method's switch statement. 
In the view.PlayerView this clears the programPane and add the buttons: "Left" and "Right". Besides we pause the 
"game loop" by returning from the executeNextStep method which we have split in two (executeNextStep() and
continueNextStep()) methods within the GameController. After the player has done the interaction - picked a direction. 
The newly implemented method controller.GameController.turnLeftOrRight() resets the game phase to Activation and resumes
the game by calling continueNextStep. After this the turnLeftOrRight() method check if the game was in stepMode and 
resumes the mode the game was in before the interactive card interrupted the "game loop".

## Documentation
We have written JavaDocs for the methods we have implemented and the ones we have used plus a couple we had missed. 

## Testing
We have implemented tests for all the classes, methods and lines in the controllers GameController ConveyorBelt and
CheckPoint classes, and we have aimed to cover all the edge cases we could think of.
We have also tested classes methods and lines in the models classes Board, Command, Phases, Player and Space WITH A FEW 
EXEPTIONS?? TODO: figure this out <<<--