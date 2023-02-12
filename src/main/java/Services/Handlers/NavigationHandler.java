package Services.Handlers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.StateTypes;
import Models.GameObject;
import Models.GameState;
import Services.Common.Tools;

public class NavigationHandler {
    private static int cachedHeading;
    private static boolean dodging = false;
    private static int temp;
    private static GameObject cachedGas;
    private static GameObject cachedObject;

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

    public static boolean decideTurnDir(int currentHeading, GameObject self, GameObject avoidObj){
        int directionToCentre;
        boolean direction;
        directionToObj = Tools.getHeadingBetween(self.getPosition(), avoidObj.getPosition());

        if(currentHeading >= 180){
            if (directionToObj < currentHeading && directionToObj > (currentHeading + 180)%360){
                direction = true; //1 artinya objek ada di kanan
            } else{
                direction = false; //0 artinya objek ada di kiri
            }
        }
        else{
            if (directionToObj < currentHeading || directionToObj > currentHeading + 180){
                direction = true; //1 artinya objek ada di kanan
            } else{
                direction = false; //0 artinya objek ada di kiri
            }

        return direction;
    }

    
    public static int dodgeEdge(GameObject self, GameState gameState){
        System.out.println("Dodging Edge of map");

        int direction;
        direction = Tools.getHeadingBetween(self.getPosition(), gameState.world.getCenterPoint());
        direction = (direction + 180) % 360;

        cachedHeading = direction;
        dodging = false;

        System.out.println("Heading: " + self.currentHeading + ", should be: " + direction);
        return direction;
    }

    public static StateTypes dodgeEnemy(){
        System.out.println("Bigger enemy getting too close, prioritizing escape");
        dodging = false;

        return StateTypes.ESCAPE_STATE;
    }

    public static int dodgeObjects(int currentHeading, GameState gameState, GameObject self, List<GameObject> objectList){
        boolean near;
        int newHeading;
        int i;
        
        near = false;
        newHeading = currentHeading;
        i = 0;


        if (!objectList.isEmpty()){
            if (!dodging){
                while(!near && i < objectList.size()){
                    if (self.getSize() + objectList.get(i).getSize() + 20 > Tools.getDistanceBetween(self, objectList.get(i))){
                        System.out.println("Dodging Gas Clouds");
                        cachedObject = objectList.get(i);
                        
                        temp = NavigationHandler.decideTurnDir(currentHeading, self, gameState);
        
                        if (temp == 1){
                            System.out.println("Heading right");
                            newHeading = (Tools.getHeadingBetween(self, objectList.get(i)) + 90) % 360;
                        }
                        else{
                            System.out.println("Heading left");
                            newHeading = (Tools.getHeadingBetween(self, objectList.get(i)) + 270) % 360;
                        }
                        near = true;
                        cachedHeading = currentHeading;
                        dodging = true;
                    }
                    else{
                        i++;
                    }
                }
            }
            else if (dodging){
                System.out.println("Dodging Gas Clouds as Before");
    
                if (temp == 1){
                    System.out.println("Heading right");
                    newHeading = (Tools.getHeadingBetween(self, objectList.get(i)) + 90) % 360;
                }
                else{
                    System.out.println("Heading left");
                    newHeading = (Tools.getHeadingBetween(self, objectList.get(i)) + 270) % 360;
                }
    
                while(!near && i < objectList.size()){
                    if(!(objectList.get(i).position == cachedObject.position)){
                        if (self.getSize() + objectList.get(i).getSize() + 20 > Tools.getDistanceBetween(self, objectList.get(i))){
                            System.out.println("Different Gas found");
                            cachedObject = objectList.get(i);
    
                            if (temp == 1){
                                System.out.println("Heading right");
                                newHeading = (Tools.getHeadingBetween(self, objectList.get(i)) + 90) % 360;
                            }
                            else{
                                System.out.println("Heading left");
                                newHeading = (Tools.getHeadingBetween(self, objectList.get(i)) + 270) % 360;
                            }
                            near = true;
                            cachedHeading = currentHeading;
                            dodging = true;
                        }
                        else{
                            i++;
                        }
                    }
                    else{
                        i++;
                    }
                }
    
                if (temp == 1){
                    if (Tools.aroundDegrees(currentHeading, (cachedHeading + 270)%360, 5)){
                        dodging = false;
                    }
                }
                else{
                    if (Tools.aroundDegrees(currentHeading, (cachedHeading + 90)%360, 5)){
                        dodging = false;
                    }
                }
            }
        }

        return newHeading;
    }

    public static int dodgeEdge(GameState gameState, GameObject bot){
        System.out.println("Dodging Edge of map");

        // Inisialisasi
        int newHeading;
        Double sizeSelf;
        List<GameObject> gasList;
        List<GameObject> playerList;
        sizeSelf = bot.getSize();
        gasList = gameState.getGameObjects()
                            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                            .sorted(Comparator
                                    .comparing(item -> Tools.getDistanceBetween(self, item)))
                            .collect(Collectors.toList());
        playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());
        
        // Dodge Gas
        if (!gasList.isEmpty || !playerList.isEmpty){
                if (getDistanceBetween(bot, playerList.get(1)) > getDistanceBetween(bot, gasList.get(1))){
                    System.out.println("Gas is closer than enemy.");
                    if(decideTurnDir(heading, bot, gasList[0])){
                    System.out.println("Gas is Detected on your right, Moving Left!");
                    newHeading = (heading + 90) % 360;
                    } else { 
                        System.out.println("Gas is Detected on your left, Moving Right!");
                        newHeading = (heading - 90) % 360; }
                } else {
                    System.out.println("Enemy is closer than gas.");
                    if(decideTurnDir(heading, bot, playerList.get(1))){
                    System.out.println("Enemy is Detected on your right, Moving Left!");
                    newHeading = (heading + 90) % 360;
                    } else { 
                        System.out.println("Enemy is Detected on your left, Moving Right!");
                        newHeading = (heading - 90) % 360; }
                } 
        }

        return newHeading;

    }

    // bs didelete kalo udah aman
    public static int dodgeGas(int currentHeading, GameState gameState, GameObject self){
        boolean near;
        int newHeading;
        int i;
        List<GameObject> gasList;
        
        near = false;
        newHeading = currentHeading;
        i = 0;

        gasList = gameState.getGameObjects()
                            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                            .sorted(Comparator
                                    .comparing(item -> Tools.getDistanceBetween(self, item)))
                            .collect(Collectors.toList());


        if (!gasList.isEmpty()){
            if (!dodging){
                while(!near && i < gasList.size()){
                    if (self.getSize() + gasList.get(i).getSize() + 20 > Tools.getDistanceBetween(self, gasList.get(i))){
                        System.out.println("Dodging Gas Clouds");
                        cachedGas = gasList.get(i);
                        
                        temp = NavigationHandler.decideTurnDir(currentHeading, self, gameState);
        
                        if (temp == 1){
                            System.out.println("Heading right");
                            newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) + 90) % 360;
                        }
                        else{
                            System.out.println("Heading left");
                            newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) + 270) % 360;
                        }
                        near = true;
                        cachedHeading = currentHeading;
                        dodging = true;
                    }
                    else{
                        i++;
                    }
                }
            }
            else if (dodging){
                System.out.println("Dodging Gas Clouds as Before");
    
                if (temp == 1){
                    System.out.println("Heading right");
                    newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) + 90) % 360;
                }
                else{
                    System.out.println("Heading left");
                    newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) + 270) % 360;
                }
    
                while(!near && i < gasList.size()){
                    if(!(gasList.get(i).position == cachedGas.position)){
                        if (self.getSize() + gasList.get(i).getSize() + 20 > Tools.getDistanceBetween(self, gasList.get(i))){
                            System.out.println("Different Gas found");
                            cachedGas = gasList.get(i);
    
                            if (temp == 1){
                                System.out.println("Heading right");
                                newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) + 90) % 360;
                            }
                            else{
                                System.out.println("Heading left");
                                newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) + 270) % 360;
                            }
                            near = true;
                            cachedHeading = currentHeading;
                            dodging = true;
                        }
                        else{
                            i++;
                        }
                    }
                    else{
                        i++;
                    }
                }
    
                if (temp == 1){
                    if (Tools.aroundDegrees(currentHeading, (cachedHeading + 270)%360, 5)){
                        dodging = false;
                    }
                }
                else{
                    if (Tools.aroundDegrees(currentHeading, (cachedHeading + 90)%360, 5)){
                        dodging = false;
                    }
                }
            }
        }

        return newHeading;
    }
}
