/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 40; // 60; // 75;
    final public static int SPACE_WIDTH = 40;  // 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }
        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    /**
     * Draw the walls for this space.
     * Note that walls are drawn on the inside of the space meaning
     * if two adjacent spaces have walls on touching sides they will
     * have twice the width when drawn.
     */
    private void drawWalls() {
        final double WALL_WIDTH = 3;
        for (Heading heading : space.getWalls()) {
            Line wall = null;
            // we should probably change the rendering of walls to draw them in the middle
            // of the line between two spaces and give them some sort of z-index higher than the
            // spaces to the south, because if not given some higher z-index, the spaces to the south
            // will be drawn on top of half of the wall and south-facing walls will appear
            // half the width of other walls (since spaces are drawn top-to-bottom).
            switch (heading) {
                case NORTH -> {
                    wall = new Line(0, 0, SPACE_WIDTH, 0);
                    wall.setTranslateY(((double) -SPACE_HEIGHT / 2) + (WALL_WIDTH / 2));
                }
                case SOUTH -> {
                    wall = new Line(0, SPACE_HEIGHT, SPACE_WIDTH, SPACE_HEIGHT);
                    wall.setTranslateY(((double) SPACE_HEIGHT / 2) - (WALL_WIDTH / 2));
                }
                case EAST -> {
                    wall = new Line(SPACE_WIDTH, 0, SPACE_WIDTH, SPACE_HEIGHT);
                    wall.setTranslateX(((double) SPACE_WIDTH / 2) - (WALL_WIDTH / 2));
                }
                case WEST -> {
                    wall = new Line(0, 0, 0, SPACE_HEIGHT);
                    wall.setTranslateX(((double) -SPACE_WIDTH / 2) + (WALL_WIDTH / 2));
                }
            }
            if (wall != null) {
                wall.setStroke(Color.RED);
                wall.setStrokeWidth(WALL_WIDTH);
                this.getChildren().add(wall);
            }
        }
    }


    /**
     * Method that draws action fields, conveyor belts and checkpoint.
     * Draws conveyor belts as grey triangles.
     * Draws checkpoints as yellow circles.
     */
    private void drawActions() {
        List<FieldAction> action = space.getActions();
        //Goes through all action tiles for the current space
        for (FieldAction tile : action) {
            if (tile != null) {
                if (tile instanceof ConveyorBelt) {
                    Polygon belt = new Polygon(0.0, 0.0, 15.0, 30.0, 30.0, 0.0);
                    belt.setFill(Color.DIMGREY);
                    //Type casting tile to ConveyorBelt to use getHeading()
                    belt.setRotate((90 * ((ConveyorBelt) tile).getHeading().ordinal()) % 360);
                    this.getChildren().add(belt);
                }
                if (tile instanceof Checkpoint) {
                    Circle checkpoint = new Circle(30.0f, 30.0f, 20.0f);
                    checkpoint.setFill(Color.YELLOW);
                    Text sequence = new Text();
                    sequence.setText(Integer.toString(((Checkpoint) tile).getSequence()));
                    sequence.setFont(new Font(20));
                    sequence.setStyle("-fx-font-weight: bold ");
                    this.getChildren().add(checkpoint);
                    this.getChildren().add(sequence);
                }
            }
        }

    }

    /**
     * Updates the view of the player
     */
    private void updatePlayer() {
        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0, 10.0, 20.0, 20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }
    /**
     * Updates the view by drawing elements of the board, the walls, checkpoints, and conveyor belts,
     * and draws the players
     * .
     * @param subject the subject which is changed
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();
            drawWalls();
            drawActions();
            updatePlayer();
        }
    }

}
