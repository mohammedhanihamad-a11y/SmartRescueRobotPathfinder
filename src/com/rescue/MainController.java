package com.rescue;

import com.rescue.ai.PathFinder;
import com.rescue.model.Cell;
import com.rescue.model.CellType;
import com.rescue.model.GridMap;
import com.rescue.model.Victim;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

public class MainController {

    @FXML private GridPane gridPane;
    @FXML private Label scoreLabel;
    @FXML private Label stepsLabel;
    @FXML private Label victimsLabel;
    @FXML private Label statusLabel;

    private final int SIZE = 10;

    private GridMap gridMap;
    private PathFinder pathFinder;

    private List<Cell> currentPath;
    private int pathIndex;
    private int steps;
    private int score;

    private Timeline autoSolveTimeline;

    @FXML
    public void initialize() {
        gridMap = new GridMap(SIZE);
        pathFinder = new PathFinder();

        steps = 0;
        score = 0;
        pathIndex = 0;

        drawEmptyGrid();
        updateLabels();

        statusLabel.setText("System ready. Click Generate Map.");
    }

    private void drawEmptyGrid() {
        gridPane.getChildren().clear();

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                addCell(row, col, CellType.EMPTY);
            }
        }
    }

    private void drawMap() {
        gridPane.getChildren().clear();

        Cell[][] grid = gridMap.getGrid();

        for (int row = 0; row < gridMap.getSize(); row++) {
            for (int col = 0; col < gridMap.getSize(); col++) {
                addCell(row, col, grid[row][col].getType());
            }
        }
    }

    private void addCell(int row, int col, CellType type) {
        Rectangle rect = new Rectangle(50, 50);
        rect.setStroke(Color.LIGHTGRAY);

        Text text = new Text();
        text.setStyle("-fx-font-size: 22px;");

        switch (type) {
            case ROBOT:
                rect.setFill(Color.LIGHTBLUE);
                text.setText("🤖");
                break;
            case VICTIM:
                rect.setFill(Color.LIGHTGREEN);
                text.setText("👤");
                break;
            case OBSTACLE:
                rect.setFill(Color.DARKGRAY);
                text.setText("■");
                break;
            case DANGER:
                rect.setFill(Color.ORANGE);
                text.setText("🔥");
                break;
            case EXIT:
                rect.setFill(Color.LIGHTPINK);
                text.setText("🚪");
                break;
            default:
                rect.setFill(Color.WHITE);
                text.setText("");
                break;
        }

        StackPane cellPane = new StackPane(rect, text);
        gridPane.add(cellPane, col, row);
    }

    @FXML
    private void handleGenerateMap() {
        stopAutoSolve();

        gridMap.generateMap();

        currentPath = null;
        pathIndex = 0;
        steps = 0;
        score = 0;

        drawMap();
        updateLabels();

        statusLabel.setText("Map generated successfully.");
    }

    @FXML
    private void handleNextStep() {
        if (gridMap.getRobot() == null) {
            statusLabel.setText("Please generate a map first.");
            return;
        }

        Victim target = gridMap.getNearestVictim();

        if (target == null) {
            statusLabel.setText("All victims rescued successfully!");
            stopAutoSolve();
            return;
        }

        if (currentPath == null || pathIndex >= currentPath.size()) {
            currentPath = pathFinder.findPath(
                    gridMap,
                    gridMap.getRobot().getRow(),
                    gridMap.getRobot().getCol(),
                    target.getRow(),
                    target.getCol()
            );

            pathIndex = 1;

            if (currentPath.isEmpty() || currentPath.size() <= 1) {
                statusLabel.setText("No path found to victim.");
                stopAutoSolve();
                return;
            }
        }

        Cell nextCell = currentPath.get(pathIndex);

        gridMap.moveRobotTo(nextCell.getRow(), nextCell.getCol());
        steps++;

        int beforeRescue = gridMap.getRescuedCount();
        gridMap.rescueVictimIfHere();
        int afterRescue = gridMap.getRescuedCount();

        if (afterRescue > beforeRescue) {
            score += 100;
            currentPath = null;
            pathIndex = 0;
            statusLabel.setText("Victim rescued! Robot will search for next victim.");
        } else {
            score -= 1;
            pathIndex++;
            statusLabel.setText("Robot moved using BFS.");
        }

        drawMap();
        updateLabels();

        if (gridMap.getRescuedCount() == gridMap.getVictims().size()) {
            score += 200;
            updateLabels();
            statusLabel.setText("All victims rescued successfully!");
            stopAutoSolve();
        }
    }

    @FXML
    private void handleAutoSolve() {
        if (gridMap.getRobot() == null) {
            statusLabel.setText("Please generate a map first.");
            return;
        }

        stopAutoSolve();

        autoSolveTimeline = new Timeline(
                new KeyFrame(Duration.millis(350), event -> handleNextStep())
        );

        autoSolveTimeline.setCycleCount(Timeline.INDEFINITE);
        autoSolveTimeline.play();

        statusLabel.setText("Auto Solve started...");
    }

    @FXML
    private void handleReset() {
        stopAutoSolve();

        gridMap = new GridMap(SIZE);

        currentPath = null;
        pathIndex = 0;
        steps = 0;
        score = 0;

        drawEmptyGrid();
        updateLabels();

        statusLabel.setText("Game reset.");
    }

    private void stopAutoSolve() {
        if (autoSolveTimeline != null) {
            autoSolveTimeline.stop();
            autoSolveTimeline = null;
        }
    }

    private void updateLabels() {
        scoreLabel.setText("Score: " + score);
        stepsLabel.setText("Steps: " + steps);

        if (gridMap == null || gridMap.getVictims() == null) {
            victimsLabel.setText("Victims: 0 / 0");
        } else {
            victimsLabel.setText(
                    "Victims: " + gridMap.getRescuedCount() + " / " + gridMap.getVictims().size()
            );
        }
    }
}