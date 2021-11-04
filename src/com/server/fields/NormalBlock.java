package com.server.fields;

import com.server.Consts;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

public class NormalBlock extends Block {

    public NormalBlock(int x, int y, boolean destroyable, boolean empty) {
        super(x, y, destroyable);
        this.empty = empty;
        if ((this.isDestroyable()) && (!this.isEmpty())) {
            initDestroyableBLock();
        } else if ((!this.isDestroyable()) && (!this.isEmpty())) {
            this.name = "UnDestroyableBlock";
        } else {
            this.name = "DefaultBlock";
        }
    }

    private void initDestroyableBLock() {
        this.name = "DestroyableBlock";
        Random generator = new Random();
        if (generator.nextDouble() <= Consts.SPECIAL_BLOCK_PROB) {       //utworz bonus pod spodem
            this.fieldUnderDestryableField = new Bonus(this.x, this.y);
        } else {
            this.fieldUnderDestryableField = null;
        }
    }
}
