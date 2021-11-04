package com.client;

import com.client.gui.ClientConsts;
import com.client.gui.ClientMainStage;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

class ClientMap extends Parent {
    
    private HashMap<Integer, String> fieldImages;
    private ClientMainStage mainStage;
    private Pane spaceForMap;
    private Pane spaceForScores;
    
    ClientMap(ClientMainStage mainStage) throws IOException {
        fieldImages = new HashMap<>();
        this.mainStage = mainStage;
        fillHashMap();
        makeMap();
    }
    
    private void makeMap() throws IOException {
        HBox mapGrids = new HBox(10);
        mapGrids.setTranslateX(27);
        mapGrids.setTranslateY(27);
        
        this.spaceForMap = ClientMainStage.gameController.getGameMapPane();
        this.spaceForScores = ClientMainStage.gameController.getGameScoresPane();
        mapGrids.getChildren().addAll(this.spaceForMap, this.spaceForScores);
        getChildren().addAll(mapGrids);
        this.mainStage.getRootElem().getChildren().addAll(this);
    }
    
    void printEntireMap(JSONObject jObject) {
        String mapp = jObject.getString("fields");
//        System.out.println(mapp);
        for (int i = 0; i < ClientConsts.DIMENSION; i++) {
            for (int j = 0; j < ClientConsts.DIMENSION; j++) {
                int field = Integer.parseInt(mapp.substring(0, 1));
                mapp = mapp.substring(1);
                if (field == 3) {                    //rysuj pod graczem ziemie
                    printOneField(i, j, 0);
                    field = Integer.parseInt(mapp.substring(0, 1));
                    mapp = mapp.substring(1);
                    printOneField(i, j, 30 + field);
                    continue;
                }
                printOneField(i, j, field);
            }
        }
    }
    
    void printOneField(int x, int y, int index) {
        String temp = fieldImages.get(index);
        Image img = new Image("file:" + "src/com/client/gui/images/Blocks/" + temp);
        ImageView imgView = new ImageView(img);
        imgView.setX(x * ClientConsts.PIXEL_SIZE);
        imgView.setY(y * ClientConsts.PIXEL_SIZE);
        this.spaceForMap.getChildren().addAll(imgView);
    }
    
    private void fillHashMap() {
        fieldImages.put(0, "defaultBlock.png");
        fieldImages.put(1, "destroyableBlock.png");
        fieldImages.put(2, "fireblock1.png");
        fieldImages.put(31, "playerBlock1.png");
        fieldImages.put(32, "playerBlock2.png");
        fieldImages.put(33, "playerBlock3.png");
        fieldImages.put(34, "playerBlock4.png");
        fieldImages.put(4, "unDestroyableBlock.png");
        fieldImages.put(5, "bomb/bomb.gif");
        fieldImages.put(6, "../Bonuses/haste.png");
        fieldImages.put(7, "../Bonuses/incrange.png");
        fieldImages.put(8, "../Bonuses/incbombs.png");
    }
}
