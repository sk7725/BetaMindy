package betamindy.ui;

import arc.graphics.g2d.*;

import static arc.Core.atlas;

public class AncientKoruh {
    public static TextureRegion[] icons = new TextureRegion[30];

    public static void load(){
        TextureRegion sheet = atlas.find("betamindy-koruh-font");
        TextureRegion sheetUI = atlas.find("betamindy-koruh-ui"); //sheetUI is in the ui folder and thus the UI atlas (idk)

        if(sheet.found()){
            TextureRegion[][] s = sheet.split(16, 16);
            for(int i = 0; i < s.length; i++){
                for(int j = 0; j < s[0].length; j++){ //if it isnt a perfect rectangle, it wont go well
                    icons[Math.min(j * s.length + i, 29)] = s[i][j];
                }
            }
        }

        //prevent crashing if the region is missing because i am never gonna push it yet :P
        for(int i = 0; i < 30; i++){
            if(icons[i] == null) icons[i] = atlas.white();
        }
    }

    public static TextureRegion eng(char c){
        if(c == '*') return icons[26];
        if(c == '@') return icons[27];
        int i = c >= 97 ? c - 97 : c - 65;
        if(i < 0 || i >= 26) return icons[29];
        return icons[i];
    }

    public static TextureRegion eng(String str, int index){
        return eng(str.charAt(index % str.length()));
    }
}
