package model.machines;

import model.food.Egg;

public class EggProducer {
    private volatile String eggId;
    private int eggCost;
    private volatile boolean isEggMaking = false;
    private volatile int eggCount = 0;

    public EggProducer(int eggCost, String eggId) {
        this.eggId = eggId;
        this.eggCost = eggCost;
    }

    // This method creates a new egg object, sets the egg as scrambled, increments the egg count,
    // and sleeps for a specified amount of time to simulate the egg-producing process.
    public void createEgg(Egg egg) {
        try {
            Thread.sleep(eggCost * 1000L);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        egg.setScrambled(true);
        isEggMaking = false;
        this.eggCount = this.eggCount + 1;
    }

    public String getEggId() {
        return this.eggId;
    }

    public int getEggCount() {
        return this.eggCount;
    }

    public boolean getIsEggMaking() {
        return this.isEggMaking;
    }

    public void setIsEggMaking() {
        this.isEggMaking = true;
    }
}
