import javafx.scene.image.Image;

public abstract class Obstacle {

    // Image grass = new Image("file:/Users/kaanturkoglu/Desktop/grass20.png");
    // Image sand = new Image("file:/Users/kaanturkoglu/Desktop/sandtile.png");
    // Image water = new Image("file:/Users/kaanturkoglu/Desktop/water.gif");
    // Image mountain = new Image("file:/Users/kaanturkoglu/Desktop/mountile.png");
    // Image forest = new Image("file:/Users/kaanturkoglu/Desktop/lowObs.png");
    // Image friendlyObs = new Image("file:/Users/kaanturkoglu/Desktop/friendsObs.png");
    // Image enemyObs = new Image("file:/Users/kaanturkoglu/Desktop/enemysObs.png");

    // Image grass = new Image("file:/Users/kaanturkoglu/Desktop/grass20.png");
    // Image sand = new Image("file:/Users/kaanturkoglu/Desktop/sandtile.png");
    // Image water = new Image("file:/Users/kaanturkoglu/Desktop/water.gif");
    // Image mountain = new Image("file:/Users/kaanturkoglu/Desktop/mountile.png");
    // Image forest = new Image("file:/Users/kaanturkoglu/Desktop/lowObs.png");
    // Image friendlyObs = new Image("file:/Users/kaanturkoglu/Desktop/friendsObs.png");
    // Image enemyObs = new Image("file:/Users/kaanturkoglu/Desktop/enemysObs.png");

    Image grass = new Image(getClass().getResourceAsStream("/Assets/grass20.png"));
    Image sand = new Image(getClass().getResourceAsStream("/Assets/sandtile.png"));
    Image water = new Image(getClass().getResourceAsStream("/Assets/water.gif"));
    Image mountain = new Image(getClass().getResourceAsStream("/Assets/mountile.png"));
    Image forest = new Image(getClass().getResourceAsStream("/Assets/lowObs.png"));
    Image friendlyObs = new Image(getClass().getResourceAsStream("/Assets/friendsObs.png"));
    Image enemyObs = new Image(getClass().getResourceAsStream("/Assets/enemysObs.png"));

    private String obstacleType; // 0 for clear path, 1 for mountain, 2 for forest, 3 for water, 4 for sand, 5
                              // for friendly, 6 for enemy
    private int height;
    private String obsSide; // neutral, friendly, enemy
    Image obstacleImage;

    public Obstacle(String obstacleType, int height, String obsSide) {
        this.obstacleType = obstacleType;
        this.height = height;
        this.obsSide = obsSide;
    }

    public Obstacle() {

    }

    public String getObstacleType() {
        return obstacleType;
    }

    public void setObstacleType(String obstacleType) {
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

class Grass extends Obstacle {
    public Grass() {
        super();
        this.setHeight(100);
        this.setObstacleType("grass");
        this.setObsSide("neutral");
        this.obstacleImage = grass;
    }
}

class Mountain extends Obstacle {
    public Mountain() {
        super();
        this.setHeight(5137);
        this.setObstacleType("mountain");
        this.setObsSide("neutral");
        this.obstacleImage = mountain;
    }
}

class Forest extends Obstacle {
    public Forest() {
        super();
        this.setHeight(2117);
        this.setObstacleType("forest");
        this.setObsSide("neutral");
        this.obstacleImage = forest;
    }
}


class Water extends Obstacle {
    public Water() {
        super();
        this.setHeight(0);
        this.setObstacleType("water");
        this.setObsSide("neutral");
        this.obstacleImage = water;
    }
}

class Sand extends Obstacle {
    public Sand() {
        super();
        this.setHeight(0);
        this.setObstacleType("sand");
        this.setObsSide("neutral");
        this.obstacleImage = sand;
    }
}

class FriendlyObstacle extends Obstacle {
    public FriendlyObstacle() {
        super();
        this.setHeight(0);
        this.setObstacleType("friendlyObs");
        this.setObsSide("friendly");
        this.obstacleImage = friendlyObs;
    }
}

class EnemyObstacle extends Obstacle {
    public EnemyObstacle() {
        super();
        this.setHeight(0);
        this.setObstacleType("enemyObs");
        this.setObsSide("enemy");
        this.obstacleImage = enemyObs;
    }
}
