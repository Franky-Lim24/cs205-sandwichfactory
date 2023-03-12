package model.machines;

import model.food.Bread;

public class BreadProducer{
    private volatile String breadId;
    private int breadCost;
    private volatile boolean isBreadMaking = false;
    private volatile int breadCount = 0;

    public BreadProducer(int breadCost, String breadId) {
        this.breadId = breadId;
        this.breadCost = breadCost;
    }

    // This method creates a new bread object, sets the bread as toasted, increments the bread count,
    // and sleeps for a specified amount of time to simulate the bread-producing process.
    public void createBread(Bread bread) {
        try {
            Thread.sleep(breadCost * 1000L);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        bread.setToasted(true);
        isBreadMaking = false;
        this.breadCount = this.breadCount + 1;
    }

    public String getBreadId() {
        return this.breadId;
    }

    public int getBreadCount() {
        return this.breadCount;
    }
    public boolean getIsBreadMaking() {
        return this.isBreadMaking;
    }
    public void setIsBreadMaking() {
        this.isBreadMaking = true;
    }
}
