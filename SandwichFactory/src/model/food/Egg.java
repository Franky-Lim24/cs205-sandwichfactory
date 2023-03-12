package model.food;

public class Egg {
    private final int eggId;
    private boolean isEggMade = false;
    private final String scrambler;

    public Egg(int eggId, String scrambler) {
        this.eggId = eggId;
        this.scrambler = scrambler;
    }

    public int getEggId() {
        return this.eggId;
    }

    public boolean isEggMade() {
        return this.isEggMade;
    }

    public void setScrambled(boolean isEggMade) {
        this.isEggMade = isEggMade;
    }

    public String getScrambler() {
        return scrambler;
    }
}
