package model.food;

public class Sandwich {
    private final int sandwichId;
    private boolean isSandwichConsumed = false;

    public Sandwich(int sandwichId) {
        this.sandwichId = sandwichId;
    }

    public boolean isSandwichConsumed() {
        return this.isSandwichConsumed;
    }

    public void setPacked(boolean isSandwichConsumed) {
        this.isSandwichConsumed = isSandwichConsumed;
    }

}
