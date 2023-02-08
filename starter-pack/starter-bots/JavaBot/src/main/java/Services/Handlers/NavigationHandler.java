package Services.GenericClass;

import Models.GameObject;
import Models.GameState;
import Services.Tools;

public class DirectionHandler {
    public static boolean outsideBound(GameState gameState, GameObject obj){
        boolean out;
        double dst;
        out = false;
        dst = (gameState.world.radius - obj.getSize() - 20) - Math.sqrt((obj.getPosition().x*obj.getPosition().x) + (obj.getPosition().y*obj.getPosition().y));
        System.out.println("Distance to outer rings: " + dst);
        if(dst < 0){
            out = true;
        }
        return out;
    }

    public static int decideTurnDir(int currentHeading, GameObject obj, GameState gameState){
        int directionToCentre;
        int direction;
        directionToCentre = Tools.getHeadingBetween(obj.getPosition(), gameState.world.getCenterPoint());


        if(currentHeading >= 180){
            if (directionToCentre < currentHeading && directionToCentre > (currentHeading + 180)%360){
                direction = 1; //1 artinya kanan
            } else{
                direction = 0; //0 artinya kiri
            }
        }
        else{
            if (directionToCentre < currentHeading || directionToCentre > currentHeading + 180){
                direction = 1; //1 artinya kanan
            } else{
                direction = 0; //0 artinya kiri
            }
        }

        return direction;
    }
}
