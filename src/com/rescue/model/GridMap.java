package com.rescue.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridMap {

    private final int size;
    private Cell[][] grid;

    private Robot robot;
    private ExitPoint exitPoint;
    private List<Victim> victims;

    public GridMap(int size) {
        this.size = size;
        this.grid = new Cell[size][size];
        this.victims = new ArrayList<>();
        createEmptyGrid();
    }

    private void createEmptyGrid() {
        grid = new Cell[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                grid[row][col] = new Cell(row, col);
            }
        }
    }

    public void generateMap() {
        createEmptyGrid();
        victims.clear();

        robot = new Robot(0, 0);
        exitPoint = new ExitPoint(size - 1, size - 1);

        grid[0][0].setType(CellType.ROBOT);
        grid[size - 1][size - 1].setType(CellType.EXIT);

        placeRandomObjects(CellType.OBSTACLE, 18);
        placeRandomObjects(CellType.DANGER, 8);
        placeVictims(3);
    }

    private void placeRandomObjects(CellType type, int count) {
        Random random = new Random();
        int placed = 0;

        while (placed < count) {
            int row = random.nextInt(size);
            int col = random.nextInt(size);

            if (isEmpty(row, col)) {
                grid[row][col].setType(type);
                placed++;
            }
        }
    }

    private void placeVictims(int count) {
        Random random = new Random();
        int placed = 0;

        while (placed < count) {
            int row = random.nextInt(size);
            int col = random.nextInt(size);

            if (isEmpty(row, col)) {
                Victim victim = new Victim(row, col);
                victims.add(victim);
                grid[row][col].setType(CellType.VICTIM);
                placed++;
            }
        }
    }

    public void moveRobotTo(int newRow, int newCol) {
        int oldRow = robot.getRow();
        int oldCol = robot.getCol();

        grid[oldRow][oldCol].setType(CellType.EMPTY);

        robot.setPosition(newRow, newCol);

        grid[newRow][newCol].setType(CellType.ROBOT);
    }

    public Victim getNearestVictim() {
        Victim nearest = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Victim victim : victims) {
            if (!victim.isRescued()) {
                int distance = Math.abs(robot.getRow() - victim.getRow())
                             + Math.abs(robot.getCol() - victim.getCol());

                if (distance < bestDistance) {
                    bestDistance = distance;
                    nearest = victim;
                }
            }
        }

        return nearest;
    }

    public void rescueVictimIfHere() {
        for (Victim victim : victims) {
            if (!victim.isRescued()
                    && victim.getRow() == robot.getRow()
                    && victim.getCol() == robot.getCol()) {
                victim.rescue();
            }
        }
    }

    public int getRescuedCount() {
        int count = 0;

        for (Victim victim : victims) {
            if (victim.isRescued()) {
                count++;
            }
        }

        return count;
    }

    public boolean isEmpty(int row, int col) {
        return isInside(row, col) && grid[row][col].getType() == CellType.EMPTY;
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public Robot getRobot() {
        return robot;
    }

    public ExitPoint getExitPoint() {
        return exitPoint;
    }

    public List<Victim> getVictims() {
        return victims;
    }

    public int getSize() {
        return size;
    }
}