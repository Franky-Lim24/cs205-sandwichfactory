package model.food;

public class Egg {
    private int id;
    private boolean isScrambled;
    private String scrambler;

    public Egg(int id, String scrambler) {
        this.id = id;
        this.isScrambled = false;
        this.scrambler = scrambler;
    }

    public int getId() {
        return id;
    }

    public boolean isScrambled() {
        return isScrambled;
    }

    public void setScrambled(boolean scrambled) {
        isScrambled = scrambled;
    }

    public String getScrambler() {
        return scrambler;
    }
}
