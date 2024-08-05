import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class VehicleTask implements Runnable {
    private final Vehicle vehicle;
    private final Obstacle[][] grid;
    private final GridPane gridPane;
    private List<int[]> path;
    private boolean reRouted;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition cellFree = lock.newCondition();
    private static final int MAX_WAIT_CYCLES = 5;
    private static Map<String, Integer> deadlockFrequencies = new HashMap<>();
    private static final int DEADLOCK_PENALTY_THRESHOLD = 5;
    private static List<Vehicle> allVehicles = new ArrayList<>();
    public int mode;
    int currentPositionIndex = 0;

    int count = 0;

    public VehicleTask(Vehicle vehicle, Obstacle[][] grid, GridPane gridPane) {
        this.vehicle = vehicle;
        this.grid = grid;
        this.gridPane = gridPane;
        this.mode = 0;
        this.reRouted = false;
        synchronized (allVehicles) {
            allVehicles.add(vehicle);
        }
    }

    public VehicleTask(Vehicle vehicle, Obstacle[][] grid, GridPane gridPane, List<int[]> path) {
        this.vehicle = vehicle;
        this.grid = grid;
        this.gridPane = gridPane;
        this.path = path;
        this.mode = 1;
        synchronized (allVehicles) {
            allVehicles.add(vehicle);
        }
    }

    @Override
    public void run() {
        if (!reRouted && mode == 0) {
            System.out.println("First time path calculation");
            path = aStar(grid, vehicle.getStartX(), vehicle.getStartY(), vehicle.getEndX(), vehicle.getEndY(),
                    new ArrayList<>());
            vehicle.setPath(path);
        }

        if (vehicle.getPath() == null || vehicle.getPath().isEmpty()) {
            System.out.println("No possible path found");
            showAlert("No Path Found", "No possible path could be found for vehicle " + vehicle.getIndex());
            return;
        }

        while (currentPositionIndex < vehicle.getPath().size()) {
            int[] pos = vehicle.getPath().get(currentPositionIndex);
            int waitCycles = 0;

            lock.lock();
            try {
                while (getVehicleAt(pos[0], pos[1]) != null) {
                    Vehicle otherVehicle = getVehicleAt(pos[0], pos[1]);

                    if (vehicle.getType().equals(otherVehicle.getType())
                            && !vehicle.getVehicleType().equals(otherVehicle.getVehicleType())) {
                        break; // Friendly-friendly or enemy-enemy encounter when vehicle types are different
                    }

                    if (shouldReroute(otherVehicle, waitCycles)) {
                        System.out.println("Vehicle " + vehicle.getIndex() + " rerouting..." + pos[0] + ", " + pos[1]);
                        lock.unlock();
                        reRoute(currentPositionIndex); // Update path from current position
                        return;
                    }

                    waitCycles++;

                    cellFree.awaitNanos(1000000L);
                }

                vehicle.setCurrentX(pos[0]); // Move the vehicle to the new position
                vehicle.setCurrentY(pos[1]);

                int finalPositionIndex = currentPositionIndex; // Update UI
                Platform.runLater(() -> updateVehiclePositionStep(finalPositionIndex));

                cellFree.signalAll(); // Notify other threads waiting on the lock

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                lock.unlock();
            }

            try {
                Thread.sleep(300); // Adjust this value to control animation speed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            currentPositionIndex++;
        }
    }

    private void reRoute(int atIndex) {
        lock.lock();
        try {
            List<int[]> newPath = new ArrayList<>(vehicle.getPath().subList(0, atIndex - 1));
            path.clear();
            vehicle.setArrived(false);
            List<int[]> deadlockCells = new ArrayList<>();
            for (Vehicle v : allVehicles) {
                if (v != vehicle && v.getCurrentX() != vehicle.getEndX() && v.getCurrentY() != vehicle.getEndY()) {
                    deadlockCells.add(new int[] { v.getCurrentX(), v.getCurrentY() });
                }
            }
            List<int[]> tempPath = aStar(grid, vehicle.getCurrentX(), vehicle.getCurrentY(), vehicle.getEndX(),
                    vehicle.getEndY(), deadlockCells);
            if (tempPath.isEmpty()) {
                System.out.println("No new path found, vehicle stalled");
                showAlert("Rerouting Failed",
                        "No new path could be found during rerouting for vehicle " + vehicle.getIndex());
                return;
            }
            newPath.addAll(tempPath);
            this.path = newPath;
            vehicle.setPath(newPath);
            this.mode = 1;
            currentPositionIndex = atIndex;
        } finally {
            lock.unlock();
        }
        this.reRouted = true;

        run();
    }

    private List<int[]> aStar(Obstacle[][] grid, int startX, int startY, int endX, int endY, List<int[]> fullCells) {
        int rows = grid.length;
        int cols = grid[0].length;

        long start = System.nanoTime();

        if (grid[startX][startY].getObstacleType() == "mountain" || grid[endX][endY].getObstacleType() == "mountain") {
            return Collections.emptyList();
        }

        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(Node::getF));
        Map<String, Node> allNodes = new HashMap<>();

        Node startNode = new Node(startX, startY, null, 0, calculateHeuristic(startX, startY, endX, endY));
        openList.add(startNode);
        allNodes.put(startX + "," + startY, startNode);

        boolean[][] visited = new boolean[rows][cols];

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            int x = current.x;
            int y = current.y;

            if (x == endX && y == endY) {
                vehicle.setArrived(true);
                return constructPath(current);
            }

            visited[x][y] = true;

            for (int[] direction : Astar.DIRECTIONS) {
                int newX = x + direction[0];
                int newY = y + direction[1];

                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols
                        && !isObstacleArea(newX, newY, grid, vehicle.getVehicleType())
                        && !visited[newX][newY]
                        && !isCellFull(newX, newY, fullCells)
                        && !isProhibitedArea(newX, newY, grid, vehicle.getType())) { // Add this check

                    double gCost = current.g + ((direction[0] == 0 || direction[1] == 0) ? 1 : Math.sqrt(2));
                    double hCost = calculateHeuristic(newX, newY, endX, endY);
                    gCost += getDeadlockPenalty(newX, newY);
                    Node neighbor = new Node(newX, newY, current, gCost, hCost);

                    String key = newX + "," + newY;
                    if (!allNodes.containsKey(key) || gCost < allNodes.get(key).g) {
                        openList.add(neighbor);
                        allNodes.put(key, neighbor);
                    }
                }
            }
        }
        long finish = System.nanoTime();
        long timeElapsed = finish - start;
        System.out.println("Time elapsed: " + timeElapsed / 1000000 + " ms");
        return Collections.emptyList();
    }

    private boolean isObstacleArea(int x, int y, Obstacle[][] grid, String vehicleType) {
        if (vehicle.getVehicleType().equals("Tank")) {
            if (grid[x][y].getObstacleType() == "mountain" || grid[x][y].getObstacleType() == "forest"
                    || grid[x][y].getObstacleType() == "water" || grid[x][y].getObstacleType() == "friendlyObs"
                    || grid[x][y].getObstacleType() == "enemyObs") {
                return true;
            }
        } else if (vehicle.getVehicleType().equals("Helicopter")) {
            if (grid[x][y].getObstacleType() == "mountain" || grid[x][y].getObstacleType() == "friendlyObs"
                    || grid[x][y].getObstacleType() == "enemyObs") {
                return true;
            }
        }
        return false;
    }

    private boolean isProhibitedArea(int x, int y, Obstacle[][] grid, String vehicleType) {
        if (vehicleType.equals("Friendly")) {
            return isNearEnemyObstacle(x, y, grid);
        } else if (vehicleType.equals("Enemy")) {
            return isNearFriendlyObstacle(x, y, grid);
        }

        return false;
    }

    private boolean isNearFriendlyObstacle(int x, int y, Obstacle[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = Math.max(0, x - 1); i <= Math.min(rows - 1, x + 1); i++) {
            for (int j = Math.max(0, y - 1); j <= Math.min(cols - 1, y + 1); j++) {
                if (grid[i][j].getObstacleType() == "friendlyObs") { // Friendly obstacle
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isNearEnemyObstacle(int x, int y, Obstacle[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = Math.max(0, x - 1); i <= Math.min(rows - 1, x + 1); i++) {
            for (int j = Math.max(0, y - 1); j <= Math.min(cols - 1, y + 1); j++) {
                if (grid[i][j].getObstacleType() == "enemyObs") { // Enemy obstacle
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isCellFull(int x, int y, List<int[]> fullCells) {
        lock.lock();
        try {
            Vehicle otherVehicle = getVehicleAt(x, y);
            if (otherVehicle == null) {
                return false;
            } else if (vehicle.getType().equals("Friendly") && otherVehicle.getType().equals("Enemy")) {
                return true;
            } else if (vehicle.getType().equals("Friendly") && otherVehicle.getType().equals("Friendly")
                    && !vehicle.getVehicleType().equals(otherVehicle.getVehicleType())) {
                return false;

            } else if (vehicle.getType().equals("Enemy") && otherVehicle.getType().equals("Friendly")) {
                return false;
            } else if (vehicle.getType().equals("Enemy") && otherVehicle.getType().equals("Enemy")
                    && !vehicle.getVehicleType().equals(otherVehicle.getVehicleType())) {
                return false;
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    private double calculateHeuristic(int x1, int y1, int x2, int y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }

    private List<int[]> constructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        Node current = node;
        while (current != null) {
            path.add(new int[] { current.x, current.y });
            current = current.parent;
        }
        Collections.reverse(path);
        vehicle.setPath(path);
        return path;
    }

    private void updateVehiclePositionStep(int index) {
        if (index < 0 || index >= path.size())
            return;

        int[] currentPos = path.get(index);
        GridPane.setRowIndex(vehicle.getIconView(), currentPos[0]);
        GridPane.setColumnIndex(vehicle.getIconView(), currentPos[1]);

        if (index == path.size() - 1) {
            vehicle.setArrived(true);
            // for (int i = 0; i < vehicle.getPath().size(); i++) {
            // System.out.println("Vehicle " + vehicle.getIndex() + " path: " +
            // vehicle.getPath().get(i)[0] + ", "
            // + vehicle.getPath().get(i)[1]);
            // }
        }

        if (index > 0) {
            int[] prevPos = path.get(index - 1);
            updateVehicleOrientation(prevPos, currentPos);

            Rectangle pathRect = new Rectangle(Astar.TILE_SIZE - 1, Astar.TILE_SIZE - 1,
                    vehicle.getColor().deriveColor(1, 1, 1, 0.5));
            pathRect.setStroke(vehicle.getColor());
            pathRect.setStrokeWidth(2);
            gridPane.add(pathRect, prevPos[1], prevPos[0]);
        }
    }

    private void updateVehicleOrientation(int[] prevPos, int[] currentPos) {
        ImageView vehicleIcon = vehicle.getIconView();
        String vehicleType = vehicle.getVehicleType();

        if (prevPos[0] < currentPos[0] && prevPos[1] < currentPos[1]) {
            vehicleIcon.setRotate(vehicleType.equals("Tank") ? 135 : 180);
        } else if (prevPos[0] < currentPos[0] && prevPos[1] > currentPos[1]) {
            vehicleIcon.setRotate(vehicleType.equals("Tank") ? 225 : -90);
        } else if (prevPos[0] > currentPos[0] && prevPos[1] < currentPos[1]) {
            vehicleIcon.setRotate(vehicleType.equals("Tank") ? 45 : 90);
        } else if (prevPos[0] > currentPos[0] && prevPos[1] > currentPos[1]) {
            vehicleIcon.setRotate(vehicleType.equals("Tank") ? 315 : 0);
        } else if (prevPos[0] < currentPos[0]) {
            vehicleIcon.setRotate(vehicleType.equals("Tank") ? 180 : 225);
        } else if (prevPos[0] > currentPos[0]) {
            vehicleIcon.setRotate(vehicleType.equals("Tank") ? 0 : 45);
        } else if (prevPos[1] < currentPos[1]) {
            vehicleIcon.setRotate(vehicleType.equals("Tank") ? 90 : 135);
        } else if (prevPos[1] > currentPos[1]) {
            vehicleIcon.setRotate(vehicleType.equals("Tank") ? 270 : 315);
        }

        vehicle.setIconView(vehicleIcon);
    }

    private double getDeadlockPenalty(int x, int y) {
        String key = x + "," + y;
        return deadlockFrequencies.getOrDefault(key, 0) * DEADLOCK_PENALTY_THRESHOLD;
    }

    private Vehicle getVehicleAt(int x, int y) {
        lock.lock();
        try {
            for (Vehicle v : allVehicles) {
                if (v.getCurrentX() == x && v.getCurrentY() == y && this.vehicle != v) {
                    return v;
                }
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    private boolean shouldReroute(Vehicle otherVehicle, int waitCycles) {
        if (otherVehicle == null) {
            return false;
        }

        if (otherVehicle.getType().equals(vehicle.getType())) {
            if (!vehicle.getVehicleType().equals(otherVehicle.getVehicleType())) {
                return false;
            }
            if (waitCycles > MAX_WAIT_CYCLES) {
                return true;
            }
        } else {
            if (vehicle.getType().equals("Friendly")) {
                return true;
            }
            return false;
        }

        return false;
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private static class Node {
        int x, y;
        Node parent;
        double g, h;

        Node(int x, int y, Node parent, double g, double h) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.g = g;
            this.h = h;
        }

        double getF() {
            return g + h;
        }
    }
}
