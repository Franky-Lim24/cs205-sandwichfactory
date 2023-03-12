package model.machines;

import model.food.Sandwich;

public class SandwichConsumer {
    private final String sandwichId;
    private final int sandwichCost;
    private volatile boolean isSandwichConsuming = false;
    private volatile int sandwichCount = 0;

    public SandwichConsumer(int sandwichCost, String sandwichId) {
        this.sandwichId = sandwichId;
        this.sandwichCost = sandwichCost;
    }

    // This method creates a new sandwich object, sets the sandwich as packed, increments the sandwich count,
    // and sleeps for a specified amount of time to simulate the sandwich-consuming process.
    public void consumeSandwich(Sandwich sandwich) {
        try {
            Thread.sleep(sandwichCost * 1000L);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        sandwich.setPacked(true);
        isSandwichConsuming = false;
        this.sandwichCount = this.sandwichCount + 1;
    }

    public String getSandwichId() {
        return this.sandwichId;
    }

    public int getSandwichCount() {
        return this.sandwichCount;
    }

    public boolean getIsSandwichConsuming() {
        return this.isSandwichConsuming;
    }

    public void setIsSandwichConsuming() {
        this.isSandwichConsuming = true;
    }
}
