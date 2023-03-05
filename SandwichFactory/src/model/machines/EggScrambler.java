package model.machines;

import model.food.Egg;

public class EggScrambler {
    private volatile boolean isScrambling;
    private int scrambleTime;
    private volatile String id;
    private volatile int eggsScrambled = 0;

    public void scramble() {
        try {
            Thread.sleep(scrambleTime * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public EggScrambler(int scrambleTime, String id) {
        this.isScrambling = false;
        this.scrambleTime = scrambleTime;
        this.id = id;
    }

    public boolean isScrambling() {
        return isScrambling;
    }

    public boolean startScrambling(Egg egg) {
        scramble();
        egg.setScrambled(true);
        isScrambling = false;
        increaseEgg();
        return true;
    }

    public String getId() {
        return id;
    }

    public int getEggsScrambled() {
        return eggsScrambled;
    }

    public void increaseEgg() {
        this.eggsScrambled = eggsScrambled + 1;
    }

    public void updateStatus() {
        isScrambling = true;
    }
}
