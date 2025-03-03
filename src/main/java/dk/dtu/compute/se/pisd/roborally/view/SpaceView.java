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
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
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
     * Method that draws action fields, conveyor belts and checkpoint.
     * Draws conveyor belts as grey triangles.
     * Draws checkpoints as yellow circles.
     */

    private void drawActions(){
        List<FieldAction> action = space.getActions();
        //Goes through all action tiles for the current space
        for(FieldAction tile : action){
            if(tile != null){
                if(tile instanceof ConveyorBelt){
                    Polygon belt = new Polygon(0.0, 0.0,
                            15.0, 30.0,
                            30.0, 0.0 );
                    belt.setFill(Color.DIMGREY);
                    //Type casting tile to ConveyorBelt to use getHeading()
                    belt.setRotate((90*((ConveyorBelt) tile).getHeading().ordinal())%360);
                    this.getChildren().add(belt);
                }
                if (tile instanceof Checkpoint){
                    Circle checkpoint = new Circle(30.0f, 30.0f, 20.0f);
                    checkpoint.setFill(Color.YELLOW);
                    Text sequence = new Text();
                    sequence.setText(((Checkpoint) tile).getSequence().toString());
                    this.getChildren().add(checkpoint);
                    this.getChildren().add(sequence);
                }
            }
        }

    }

    private void updatePlayer() {
        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            this.getChildren().clear();

            // XXX A3: drawing walls and action on the space (could be done
            //         here); it would be even better if fixed things on
            //         spaces  are only drawn once (and not on every update)

            // Not sure if drawActions() can be placed another place to not get updated every click,
            // but haven't gotten it to draw elsewhere
            drawActions();
            updatePlayer();
        }
    }

}
