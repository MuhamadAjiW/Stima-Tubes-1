package Services.Handlers;

import Models.GameObject;
import Services.Common.Tools;

public class RadarHandler {
    public static boolean detectEnemy(GameObject otherObject, GameObject bot, Double size){
        boolean detected;
        detected = false;
        if(size + bot.getSize() + otherObject.getSize() > Tools.getDistanceBetween(otherObject, bot)){
            detected = true;
        }
        return detected;
    }

    public static boolean isSmall(GameObject otherObject, Double threshold){
        boolean small;
        small = false;
        if(otherObject.size < threshold){
            small = true;
        }
        return small;
    }

    public static boolean isBig(GameObject otherObject, Double threshold){
        boolean big;
        big = false;
        if(otherObject.size > threshold){
            big = true;
        }
        return big;
    }
}
