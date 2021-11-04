package com.server.fields;
import com.server.Consts;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Field {
    protected int x;
    protected int y;
    protected boolean destroyable;
    protected boolean empty;
    protected String name;
    protected Bonus fieldUnderDestryableField;

    public Field(int x, int y, boolean destroyable) {
        this.x = x;
        this.y = y;
        this.destroyable = destroyable;
        this.empty = false;
    }

    public String getName() {
        return name;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public Bonus getFieldUnderDestryableField() {
        return fieldUnderDestryableField;
    }

    public boolean isEmpty() { return empty; }
    public boolean isDestroyable(){
        return destroyable;
    }

}
