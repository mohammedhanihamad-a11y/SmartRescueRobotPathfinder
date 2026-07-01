package com.rescue.ai;

import com.rescue.model.Cell;
import com.rescue.model.CellType;
import com.rescue.model.GridMap;

import java.util.*;

public class PathFinder {

    public List<Cell> findPath(GridMap map, int startRow, int startCol, int targetRow, int targetCol) {
        int size = map.getSize();
        Cell[][] grid = map.getGrid();

        boolean[][] visited = new boolean[size][size];
        Cell[][] parent = new Cell[size][size];

        Queue<Cell> queue = new LinkedList<>();

        Cell start = grid[startRow][startCol];
        queue.add(start);
        visited[startRow][startCol] = true;

        int[] dRow = {-1, 1, 0, 0};
        int[] dCol = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            if (current.getRow() == targetRow && current.getCol() == targetCol) {
                return buildPath(parent, grid[targetRow][targetCol]);
            }

            for (int i = 0; i < 4; i++) {
                int newRow = current.getRow() + dRow[i];
                int newCol = current.getCol() + dCol[i];

                if (map.isInside(newRow, newCol)
                        && !visited[newRow][newCol]
                        && grid[newRow][newCol].getType() != CellType.OBSTACLE) {

                    visited[newRow][newCol] = true;
                    parent[newRow][newCol] = current;
                    queue.add(grid[newRow][newCol]);
                }
            }
        }

        return new ArrayList<>();
    }

    private List<Cell> buildPath(Cell[][] parent, Cell target) {
        List<Cell> path = new ArrayList<>();
        Cell current = target;

        while (current != null) {
            path.add(current);
            current = parent[current.getRow()][current.getCol()];
        }

        Collections.reverse(path);
        return path;
    }
}