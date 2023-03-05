package model.food;

public class Bread {
    private int id;
    private boolean isToasted;
    private String toaster;

    public Bread(int id, String toaster) {
        this.id = id;
        this.isToasted = false;
        this.toaster = toaster;
    }

    public int getId() {
        return id;
    }

    public boolean isToasted() {
        return isToasted;
    }

    public void setToasted(boolean toasted) {
        isToasted = toasted;
    }

    public String getToaster() {
        return toaster;
    }
}
