package model.food;

public class Bread {
    private final int breadId;
    private boolean isBreadMade = false;
    private final String toaster;

    public Bread(int breadId, String toaster) {
        this.breadId = breadId;
        this.toaster = toaster;
    }

    public int getBreadId() {
        return this.breadId;
    }

    public boolean isBreadMade() {
        return this.isBreadMade;
    }

    public void setToasted(boolean isBreadMade) {
        this.isBreadMade = isBreadMade;
    }

    public String getToaster() {
        return this.toaster;
    }
}
