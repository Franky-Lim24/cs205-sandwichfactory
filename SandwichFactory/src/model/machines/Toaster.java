package model.machines;

import model.food.Bread;

public class Toaster {
    private volatile boolean isToasting;
    private int toastTime;
    private volatile String id;
    private volatile int breadsToasted;

    public void toast() {
        try {
            Thread.sleep(toastTime * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Toaster(int toastTime, String id) {
        this.isToasting = false;
        this.toastTime = toastTime;
        this.id = id;
    }

    public boolean isToasting() {
        return isToasting;
    }

    public boolean startToasting(Bread bread) {
        isToasting = true;
        toast();
        bread.setToasted(true);
        isToasting = false;
        increaseBread();
        return true;
    }

    public String getId() {
        return id;
    }

    public int getBreadsToasted() {
        return breadsToasted;
    }

    public void increaseBread() {
        this.breadsToasted = breadsToasted + 1;
    }
}
