package ljv;

public enum Direction {
    BT("BT"),
    LR("LR"),
    TB("TB"),
    RL("RL");

    private String direction;

    Direction(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return direction;
    }

}
