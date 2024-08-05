import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Here is a Java class that generates an N x M array with random obstructions.
 * In this array,
 * 0s represent clear path cells and 1s represent obstructions
 */

public class Track {

    private Obstacle[][] track;
    private Obstacle mountain = new Mountain();
    private Obstacle forest = new Forest();
    private Obstacle water = new Water();
    private Obstacle grass = new Grass();
    private Obstacle sand = new Sand();
    private Obstacle friendlyObs = new FriendlyObstacle();
    private Obstacle enemyObs = new EnemyObstacle();

    private int rows;
    private int cols;

    public Track(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.track = new Obstacle[rows][cols];
        generateTrack();
    }

    private void generateTrack() {

        // Initialize the track with clear paths
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                track[i][j] = grass; // Initialize with grass
            }
        }

        int totalCells = rows * cols;
        int numObstructions = (int) (totalCells * 0.3);
        int numMountains = (int) (numObstructions * 0.65);
        int numWaterCells = (int) (totalCells * 0.05); // Example percentage for water
        int numFriendlyObs = (int) (numObstructions * 0.05);
        int numEnemyObs = (int) (numObstructions * 0.05);

        // Place mountains and obstructions randomly
        placeRandomCells(numMountains, mountain);
        placeRandomCells(numFriendlyObs, friendlyObs);
        placeRandomCells(numEnemyObs, enemyObs);

        // Generate water clusters
        generateWaterClusters(numWaterCells);
        generateForestClusters(numObstructions - numMountains);

        // Place sand around water
        placeSandAroundWater();

        for (int i = 0; i < track.length; i++) {
            for (int j = 0; j < track[0].length; j++) {
                System.out.println(track[i][j].getObstacleType());
            }
        }
    }

    private void generateForestClusters(int numForest) {
        Random rand = new Random();
        int forestPlaced = 0;

        while (forestPlaced < numForest) {
            int startX = rand.nextInt(rows);
            int startY = rand.nextInt(cols);

            // Use BFS to create clusters of water
            Queue<int[]> queue = new LinkedList<>();
            queue.add(new int[] { startX, startY });
            int cellsToPlace = Math.min(7 + rand.nextInt(7), numForest - forestPlaced); // Ensure at least 3 water
                                                                                        // cells per cluster

            while (!queue.isEmpty() && cellsToPlace > 0) {
                int[] cell = queue.poll();
                int x = cell[0];
                int y = cell[1];

                if (x >= 0 && x < rows && y >= 0 && y < cols && track[x][y] == grass) {
                    track[x][y] = forest;
                    forestPlaced++;
                    cellsToPlace--;

                    queue.add(new int[] { x + 1, y });
                    queue.add(new int[] { x - 1, y });
                    queue.add(new int[] { x, y + 1 });
                    queue.add(new int[] { x, y - 1 });
                }
            }
        }
    }

    private void placeRandomCells(int count, Obstacle obstacle) {
        Random rand = new Random();
        while (count > 0) {
            int x = rand.nextInt(rows);
            int y = rand.nextInt(cols);
            if (track[x][y] == grass) { // Place only in empty cells
                track[x][y] = obstacle;
                count--;
            }
        }
    }

    private void generateWaterClusters(int numWaterCells) {
        Random rand = new Random();
        int waterPlaced = 0;

        while (waterPlaced < numWaterCells) {
            int startX = rand.nextInt(rows);
            int startY = rand.nextInt(cols);

            // Use BFS to create clusters of water
            Queue<int[]> queue = new LinkedList<>();
            queue.add(new int[] { startX, startY });
            int cellsToPlace = Math.min(3 + rand.nextInt(3), numWaterCells - waterPlaced); // Ensure at least 3 water
                                                                                           // cells per cluster

            while (!queue.isEmpty() && cellsToPlace > 0) {
                int[] cell = queue.poll();
                int x = cell[0];
                int y = cell[1];

                if (x >= 0 && x < rows && y >= 0 && y < cols && track[x][y] == grass) {
                    track[x][y] = water;
                    waterPlaced++;
                    cellsToPlace--;

                    queue.add(new int[] { x + 1, y });
                    queue.add(new int[] { x - 1, y });
                    queue.add(new int[] { x, y + 1 });
                    queue.add(new int[] { x, y - 1 });
                }
            }
        }
    }

    private void placeSandAroundWater() {
        // Iterate through each cell in the track
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Check if the current cell is water
                if (track[i][j] == water) {
                    // Try to place sand in all 8 surrounding directions
                    tryPlaceSand(i - 1, j); // Up
                    tryPlaceSand(i + 1, j); // Down
                    tryPlaceSand(i, j - 1); // Left
                    tryPlaceSand(i, j + 1); // Right
                    tryPlaceSand(i - 1, j - 1); // Top-left
                    tryPlaceSand(i - 1, j + 1); // Top-right
                    tryPlaceSand(i + 1, j - 1); // Bottom-left
                    tryPlaceSand(i + 1, j + 1); // Bottom-right
                }
            }
        }
    }

    private void tryPlaceSand(int x, int y) {
        // Check if the cell is within bounds and currently a clear path or grass
        if (x >= 0 && x < rows && y >= 0 && y < cols && (track[x][y] == grass || track[x][y] == grass)) {
            track[x][y] = sand;
        }
    }

    public Obstacle[][] getTrack() {
        return track;
    }

    public void printTrack() {
        // ANSI escape code for red text
        final String ANSI_RED = "\u001B[31m";
        // ANSI escape code for green text
        final String ANSI_GREEN = "\u001B[32m";
        // ANSI escape code to reset to default text color
        final String ANSI_RESET = "\u001B[0m";

        // Print column headers
        System.out.print("  ");
        for (int j = 0; j < cols; j++) {
            System.out.print(j + " ");
        }
        System.out.println();

        // Print rows with row headers
        for (int i = 0; i < rows; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < cols; j++) {
                if (track[i][j] == mountain) {
                    System.out.print(ANSI_RED + track[i][j] + ANSI_RESET + " ");
                } else if (track[i][j] == grass) {
                    System.out.print(ANSI_GREEN + track[i][j] + ANSI_RESET + " ");
                } else {
                    System.out.print(track[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

}
