import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Astar extends Application {
    public static final int TILE_SIZE = 80;
    public static final int[][] DIRECTIONS = {
            {0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}
    };

    private Track track = new Track(20, 20);
    private int[][] grid = track.getTrack();

    public List<Vehicle> vehicles = new ArrayList<>();
    private Vehicle currentVehicle = null;
    private int currentVehicleIndex = -1;
    public boolean newVehicleAdded = false;

    private GridPane gridPane;

    Button startButton = new Button("Start");

    Image grass = new Image("file:/Users/kaanturkoglu/Desktop/grass20.png");
    Image sand = new Image("file:/Users/kaanturkoglu/Desktop/sandtile.png");
    Image water = new Image("file:/Users/kaanturkoglu/Desktop/water.gif");
    Image mountain = new Image("file:/Users/kaanturkoglu/Desktop/mountile.png");
    Image forest = new Image("file:/Users/kaanturkoglu/Desktop/lowObs.png");
    Image friendlyObs = new Image("file:/Users/kaanturkoglu/Desktop/friendsObs.png");
    Image enemyObs = new Image("file:/Users/kaanturkoglu/Desktop/enemysObs.png");

    @Override
    public void start(Stage primaryStage) {
        gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);

        updateGrid();

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Button addFriendlyTankButton = new Button("Add Friendly Tank");
        addFriendlyTankButton.setOnAction(event -> createVehicle(new FriendlyTank()));

        Button addEnemyTankButton = new Button("Add Enemy Tank");
        addEnemyTankButton.setOnAction(event -> createVehicle(new EnemyTank()));

        Button addFriendlyHelicopterButton = new Button("Add Friendly Helicopter");
        addFriendlyHelicopterButton.setOnAction(event -> createVehicle(new FriendlyHelicopter()));

        Button addEnemyHelicopterButton = new Button("Add Enemy Helicopter");
        addEnemyHelicopterButton.setOnAction(event -> createVehicle(new EnemyHelicopter()));

        startButton.setOnAction(event -> startVehicles());

        Button clearVehiclesButton = new Button("Clear Vehicles");
        clearVehiclesButton.setOnAction(event -> clearVehicles());

        HBox buttonsBox = new HBox(addFriendlyTankButton, addFriendlyHelicopterButton, addEnemyTankButton,
                addEnemyHelicopterButton, startButton, clearVehiclesButton);
        VBox root = new VBox(buttonsBox, scrollPane);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Grid Path Finder");
        primaryStage.show();
    }

    private void createVehicle(Vehicle vehicle) {
        if (currentVehicle == null) {
            currentVehicle = vehicle;
        } else {
            System.out.println("Finish placing the current vehicle first.");
        }
    }

    private void handleCellClick(int x, int y) {
        System.out.println("Clicked on cell: " + x + ", " + y);

        if (grid[x][y] > 0) {
            return; // Ignore clicks on obstacles
        }

        if (currentVehicle != null) {
            if (currentVehicle.getStartX() == -1 && currentVehicle.getStartY() == -1) {
                currentVehicle.setStartX(x);
                currentVehicle.setStartY(y);
                Rectangle startRect = new Rectangle(TILE_SIZE, TILE_SIZE, currentVehicle.getColor());
                gridPane.add(startRect, currentVehicle.getStartY(), currentVehicle.getStartX());
            } else if (currentVehicle.getEndX() == -1 && currentVehicle.getEndY() == -1) {
                currentVehicle.setEndX(x);
                currentVehicle.setEndY(y);
                vehicles.add(currentVehicle);
                currentVehicleIndex++;
                newVehicleAdded = true;
                updateGrid();
                currentVehicle = null;  // Reset current vehicle
            }
        }
    }

    private void updateGrid() {
        gridPane.getChildren().clear();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);
                rect.setStroke(Color.GRAY);

                if (grid[i][j] == 1) {
                    rect.setFill(new ImagePattern(mountain));
                } else if (grid[i][j] == 2) {
                    rect.setFill(new ImagePattern(forest));
                } else if (grid[i][j] == 3) {
                    rect.setFill(new ImagePattern(water));
                } else if (grid[i][j] == 4) {
                    rect.setFill(new ImagePattern(sand));
                } else if (grid[i][j] == 5) {
                    rect.setFill(new ImagePattern(friendlyObs));
                } else if (grid[i][j] == 6) {
                    rect.setFill(new ImagePattern(enemyObs));
                } else {
                    rect.setFill(new ImagePattern(grass));
                }
                int x = i;
                int y = j;
                rect.setOnMouseClicked(event -> handleCellClick(x, y));
                gridPane.add(rect, j, i);
            }
        }

        for (Vehicle vehicle : vehicles) {
            if (vehicle.getStartX() != -1 && vehicle.getStartY() != -1) {
                Rectangle startRect = new Rectangle(TILE_SIZE, TILE_SIZE, vehicle.getColor());
                startRect.setStroke(Color.BLACK);
                gridPane.add(startRect, vehicle.getStartY(), vehicle.getStartX());
            }

            if (vehicle.getEndX() != -1 && vehicle.getEndY() != -1) {
                Rectangle endRect = new Rectangle(TILE_SIZE, TILE_SIZE, vehicle.getColor());
                endRect.setStroke(Color.BLACK);
                gridPane.add(endRect, vehicle.getEndY(), vehicle.getEndX());
            }

            if (vehicle.getStartX() != -1 && vehicle.getStartY() != -1) {
                gridPane.getChildren().remove(vehicle.getIconView());
                gridPane.add(vehicle.getIconView(), vehicle.getStartY(), vehicle.getStartX());
            }
        }
    }

    private void clearVehicles() {
        for (int i = 0; i < vehicles.size(); i++) {
            vehicles.get(i).getPath().clear();
            vehicles.get(i).setCurrentX(-1);
            vehicles.get(i).setCurrentY(-1);  
        }
        vehicles.clear();
        currentVehicle = null;
        currentVehicleIndex = -1;
        startButton.setText("Start");
        updateGrid();
    }

    private void startVehicles() {
        if (!vehicles.isEmpty()) {
            updateGrid();
            startButton.setText("Restart");
            System.out.println("All vehicles: " + vehicles);
            System.out.println("New vehicle added: " + newVehicleAdded);
            int restartMode = 0;

            if (startButton.getText().equals("Restart")) {
                restartMode = 1;
            }

            for (Vehicle vehicle : vehicles) {
                vehicle.startMovement(grid, gridPane, newVehicleAdded, restartMode);
            }

        }

        System.out.println("Number of vehicles: " + vehicles.size());
        for (int i = 0; i < vehicles.size(); i++) {
            while (vehicles.get(i).getPath() == null) {
                // Wait for path calculation
            }
        }
        for (int i = 0; i < vehicles.size(); i++) {
            System.out.println("Vehicle " + vehicles.get(i).getIndex() + " path:");
            for (int j = 0; j < vehicles.get(i).getPath().size(); j++) {
                System.out.println(vehicles.get(i).getPath().get(j)[0] + " "
                        + vehicles.get(i).getPath().get(j)[1]);
            }
        }
        newVehicleAdded = false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
