import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.List;

import javafx.scene.image.Image;

abstract class Vehicle {
    Color[] friendlyColors = { Color.LIGHTCORAL, Color.LIGHTBLUE, Color.LIGHTGREEN, Color.LIGHTYELLOW,
            Color.LIGHTSALMON, Color.LIGHTPINK, Color.LIGHTCYAN, Color.LIGHTSKYBLUE, Color.LIGHTSEAGREEN,
            Color.LIGHTGOLDENRODYELLOW };
    Color[] enemyColors = { Color.DARKRED, Color.DARKBLUE, Color.DARKGREEN, Color.DARKORANGE, Color.DARKVIOLET,
            Color.DARKMAGENTA, Color.DARKCYAN, Color.DARKSLATEBLUE, Color.DARKSEAGREEN, Color.DARKGOLDENROD };

    Image tank = new Image("file:/Users/kaanturkoglu/Desktop/aaaaa.png");
    Image enemyTank = new Image("file:/Users/kaanturkoglu/Desktop/enemyTank.png");
    Image heli = new Image("file:/Users/kaanturkoglu/Desktop/helitepeden.png");
    Image enemyHeli = new Image("file:/Users/kaanturkoglu/Desktop/enemyHelicopter.png");

    private int startX = -1, startY = -1, endX = -1, endY = -1;
    private String type;
    private ImageView iconView;
    private Color color;
    private Thread thread;
    private String vehicleType;
    private List<int[]> path;
    private boolean isArrived = false;
    private int index = 1;

    // New fields to track current position
    private int currentX = startX;
    private int currentY = startY;

    // Getters and setters for current position
    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public ImageView getIconView() {
        return iconView;
    }

    public void setIconView(ImageView iconView) {
        this.iconView = iconView;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void startMovement(int[][] grid, GridPane gridPane, boolean newVehicleAdded, int mode) {

        if (this.getPath() != null && !newVehicleAdded && mode == 1) {
            System.out.println("aaaaaa");
            thread = new Thread(new VehicleTask(this, grid, gridPane, path));
            thread.start();
        } else {
            thread = new Thread(new VehicleTask(this, grid, gridPane));
            thread.start();
        }
    }

    public Color[] getFriendlyColors() {
        return friendlyColors;
    }

    public Color[] getEnemyColors() {
        return enemyColors;
    }

    public Image getTank() {
        return tank;
    }

    public Image getHeli() {
        return heli;
    }

    public Thread getThread() {
        return thread;
    }

    public List<int[]> getPath() {
        return path;
    }

    public void setFriendlyColors(Color[] friendlyColors) {
        this.friendlyColors = friendlyColors;
    }

    public void setEnemyColors(Color[] enemyColors) {
        this.enemyColors = enemyColors;
    }

    public void setTank(Image tank) {
        this.tank = tank;
    }

    public void setHeli(Image heli) {
        this.heli = heli;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void setPath(List<int[]> path) {
        this.path = path;
    }

    public boolean isArrived() {
        return isArrived;
    }

    public void setArrived(boolean isArrived) {
        this.isArrived = isArrived;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

class EnemyTank extends Vehicle {
    EnemyTank() {
        super();
        setType("Enemy");
        setVehicleType("Tank");
        setIconView(new ImageView(enemyTank));
        getIconView().setFitWidth(Astar.TILE_SIZE * 1 / 2);
        getIconView().setFitHeight(Astar.TILE_SIZE * 3 / 5);
        getIconView().setTranslateX((Astar.TILE_SIZE - getIconView().getFitWidth()) / 2);
        setColor(enemyColors[(int) (Math.random() * enemyColors.length)]);
    }
}

class FriendlyTank extends Vehicle {
    FriendlyTank() {
        super();
        setType("Friendly");
        setVehicleType("Tank");
        setIconView(new ImageView(tank));
        getIconView().setFitWidth(Astar.TILE_SIZE * 1 / 2);
        getIconView().setFitHeight(Astar.TILE_SIZE * 3 / 5);
        getIconView().setTranslateX((Astar.TILE_SIZE - getIconView().getFitWidth()) / 2);
        // getIconView().setTranslateY((Astar.TILE_SIZE - getIconView().getFitHeight())
        // / 2);
        setColor(friendlyColors[(int) (Math.random() * friendlyColors.length)]);
    }
}

class EnemyHelicopter extends Vehicle {
    EnemyHelicopter() {
        super();
        setType("Enemy");
        setVehicleType("Helicopter");
        setIconView(new ImageView(enemyHeli));
        getIconView().setFitWidth(Astar.TILE_SIZE * 2 / 3);
        getIconView().setFitHeight(Astar.TILE_SIZE * 2 / 3);
        getIconView().setTranslateX((Astar.TILE_SIZE - getIconView().getFitWidth()) / 2);
        setColor(enemyColors[(int) (Math.random() * enemyColors.length)]);
    }
}

class FriendlyHelicopter extends Vehicle {
    FriendlyHelicopter() {
        super();
        setType("Friendly");
        setVehicleType("Helicopter");
        setIconView(new ImageView(heli));
        getIconView().setFitWidth(Astar.TILE_SIZE * 2 / 3);
        getIconView().setFitHeight(Astar.TILE_SIZE * 2 / 3);
        getIconView().setTranslateX((Astar.TILE_SIZE - getIconView().getFitWidth()) / 2);
        setColor(friendlyColors[(int) (Math.random() * friendlyColors.length)]);
    }
}