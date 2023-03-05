package model.machines;

import model.food.Sandwich;

public class Packer {
    private volatile boolean isPacking;
    private int packingTime;
    private volatile String id;
    private volatile int sandwichPacked = 0;

    public void pack() {
        try {
            Thread.sleep(packingTime * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Packer(int packingTime, String id) {
        this.isPacking = false;
        this.packingTime = packingTime;
        this.id = id;
    }

    public boolean isPacking() {
        return isPacking;
    }

    public boolean startPacking(Sandwich sandwich) {
        pack();
        sandwich.setPacked(true);
        increaseSandwich();
        isPacking = false;
        return true;
    }

    public String getId() {
        return id;
    }

    public int getSandwichPacked() {
        return sandwichPacked;
    }

    public void increaseSandwich() {
        this.sandwichPacked = sandwichPacked + 1;
    }

    public void updateStatus() {
        isPacking = true;
    }
}
