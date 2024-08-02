import javafx.scene.image.Image;

public abstract class Obstacle {

    Image grass = new Image("file:/Users/kaanturkoglu/Desktop/grass20.png");
    Image sand = new Image("file:/Users/kaanturkoglu/Desktop/sandtile.png");
    Image water = new Image("file:/Users/kaanturkoglu/Desktop/water.gif");
    Image mountain = new Image("file:/Users/kaanturkoglu/Desktop/mountile.png");
    Image forest = new Image("file:/Users/kaanturkoglu/Desktop/lowObs.png");
    Image friendlyObs = new Image("file:/Users/kaanturkoglu/Desktop/friendsObs.png");
    Image enemyObs = new Image("file:/Users/kaanturkoglu/Desktop/enemysObs.png");

    private int obstacleType; // 0 for clear path, 1 for mountain, 2 for forest, 3 for water, 4 for sand, 5
                              // for friendly, 6 for enemy
    private int height;
    private String obsSide; // neutral, friendly, enemy
    Image obstacleImage;

    public Obstacle(int obstacleType, int height, String obsSide) {
        this.obstacleType = obstacleType;
        this.height = height;
        this.obsSide = obsSide;
    }

    public Obstacle() {

    }

    public int getObstacleType() {
        return obstacleType;
    }

    public void setObstacleType(int obstacleType) {
        this.obstacleType = obstacleType;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getObsSide() {
        return obsSide;
    }

    public void setObsSide(String obsSide) {
        this.obsSide = obsSide;
    }

    public Image getObstacleImage() {
        return obstacleImage;
    }

    public void setObstacleImage(Image obstacleImage) {
        this.obstacleImage = obstacleImage;
    }
}

class Mountain extends Obstacle {
    public Mountain() {
        super();
        this.setHeight(5137);
        this.setObstacleType(1);
        this.setObsSide("neutral");
        this.obstacleImage = mountain;
    }
}

class Forest extends Obstacle {
    public Forest() {
        super();
        this.setHeight(2117);
        this.setObstacleType(2);
        this.setObsSide("neutral");
        this.obstacleImage = forest;
    }
}

class Grass extends Obstacle {
    public Grass() {
        super();
        this.setHeight(100);
        this.setObstacleType(0);
        this.setObsSide("neutral");
        this.obstacleImage = grass;
    }
}

class Water extends Obstacle {
    public Water() {
        super();
        this.setHeight(0);
        this.setObstacleType(3);
        this.setObsSide("neutral");
        this.obstacleImage = water;
    }
}

class Sand extends Obstacle {
    public Sand() {
        super();
        this.setHeight(0);
        this.setObstacleType(4);
        this.setObsSide("neutral");
        this.obstacleImage = sand;
    }
}

class FriendlyObstacle extends Obstacle {
    public FriendlyObstacle() {
        super();
        this.setHeight(0);
        this.setObstacleType(5);
        this.setObsSide("friendly");
        this.obstacleImage = friendlyObs;
    }
}

class EnemyObstacle extends Obstacle {
    public EnemyObstacle() {
        super();
        this.setHeight(0);
        this.setObstacleType(6);
        this.setObsSide("enemy");
        this.obstacleImage = enemyObs;
    }
}
