package model.food;

public class Sandwich {
    private int id;
    private boolean isPacked;
    private Bread bottom;
    private Bread top;
    private Egg egg;

    public Sandwich(int id, Bread bottom, Bread top, Egg egg) {
        this.id = id;
        this.isPacked = false;
    }

    public int getId() {
        return id;
    }

    public boolean isPacked() {
        return isPacked;
    }

    public void setPacked(boolean packed) {
        isPacked = packed;
    }

    public Bread getBottom() {
        return bottom;
    }

    public Bread getTop() {
        return top;
    }

    public Egg getEgg() {
        return egg;
    }
}
