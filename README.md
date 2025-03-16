# Temp. README - to remember what to include in it and what we've done..
This Readme is intended for TA's - at somepoint.. for now more of a reminder.

# Game phases

# Movement

# Game Boards 
    - we have made some gameboards 

## Checkpoint
    - checkpoints checked after all progamming phase?? is done.. 
    - checkpoints are executed as the first action. that means they have priority over eg. conveyor belts.
        - this means that only one player will be able to get a given checkpoint in a turn
    - TODO implement winning player - the first player to collect all checkpoints.
    - TODO write tests for checkpoints.

## Conveor Belt
    - conveyor belt cannot bump players.
    - if the end of an conveyor belts can be blocked by players 
    - conveyor belts movement are blocked by walls.


# Tests
    - The functionalities of the gamecontroller is tested thorughly 
    - We have runned manually end to end tests. <- at some point I think.


# Assignment 4a)
- overview
- git 
- implement move.. 
- java docs
- tested implementation - The test passed
- implemented counter
- tested counter
- updated getStatusMessage()
- overview of the game rules

We have downloaded the assignment4.zip and gotten an overview of the controller and the model packages.
We have made a github repository 
We have implemented the method moveCurrentPlayerToSpace()
We have implemented counter and updated the getStatusMessage() method.
We have written JavaDocs for the implemented methods and the methods used
We have tested using test and manually

# Assignment 4b)
- added game board 
- made the new game board selectable
- made gui for walls
- made gui for conveyor belts
- made gui for check points
- implemented method view.SpaceView.updateView()
- written javaDocs for implementations and uses 


# Assignment 4c)
- implemented the existing command cards
  - moveForward
  - moveFastForward
  - turnRight
  - turnLeft
- implemented walls blocking player movement
- implemented buttons in playerView
  - Finish Programming
  - Execute Program
  - Execute Current Register
- added command cards
  - uTurn
  - moveBackwards
- implemented uTurn and moveBackwards functionality
- written javaDocs for implementations and uses
- tested implementations with tests and manually
- added tests for new functionality


# Assignment 4d)
- implemented bumping of other players
- implemented field actions - executed after command card execution
  - conveyor belt
  - check points
- updated the controller.GameController.executeNextStep() to execute field actions
- added status label with info of players current checkpoint
- written javaDocs for implementations and uses
- 

# Assignment 4e) 
- implemented winning conditions
- added (interactive) command card "Turn Left or Right" 
- implemented interactive command cards functionality - interrupts the 'game loop'
- written javaDocs fro implementations and uses
- added tests for new functionality 