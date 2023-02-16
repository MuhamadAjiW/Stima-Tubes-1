package Services.Handlers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.StateTypes;
import Models.GameObject;
import Models.GameState;
import Models.Position;
import Services.Common.Tester;
import Services.Common.Tools;

public class NavigationHandler {
    public static boolean dodging = false;
    private static int cachedHeading;
    private static int temp;
    private static GameObject cachedObject;

    public static boolean outsideBound(GameState gameState, GameObject obj){
        boolean out;
        double dst;
        out = false;
        dst = (gameState.world.radius - obj.getSize() - 20 - obj.getSpeed()) - Math.sqrt((obj.getPosition().x*obj.getPosition().x) + (obj.getPosition().y*obj.getPosition().y));
        Tester.appendFile("Distance to outer rings: " + dst, "testlog.txt");
        if(dst < 0){
            out = true;
        }
        return out;
    }

    public static boolean outsideBound(GameState gameState, Position toBe, GameObject obj){
        boolean out;
        double dst;
        out = false;
        dst = (gameState.world.radius - obj.getSize() - 20 - obj.getSpeed()) - Math.sqrt((toBe.x*toBe.x) + (toBe.y*toBe.y));
        Tester.appendFile("Distance to outer rings: " + dst, "testlog.txt");
        if(dst < 0){
            out = true;
        }
        return out;
    }
    
    public static int decideTurnDir(int currentHeading, GameObject obj, GameState gameState){
        int directionToCentre;
        int direction;
        directionToCentre = Tools.getHeadingBetween(gameState.world.getCenterPoint(), obj.getPosition());


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
        int directionToObj;
        boolean direction;
        directionToObj = Tools.getHeadingBetween(avoidObj.getPosition(), self.getPosition());

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
        }
        return direction;
    }

    public static StateTypes dodgeEnemy(){
        Tester.appendFile("Bigger enemy getting too close, prioritizing escape", "testlog.txt");
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
                        Tester.appendFile("Dodging Gas Clouds", "testlog.txt");
                        cachedObject = objectList.get(i);
                        
                        temp = NavigationHandler.decideTurnDir(currentHeading, self, gameState);
        
                        if (temp == 1){
                            Tester.appendFile("Heading right", "testlog.txt");
                            newHeading = (Tools.getHeadingBetween(objectList.get(i), self) + 270) % 360;
                        }
                        else{
                            Tester.appendFile("Heading left", "testlog.txt");
                            newHeading = (Tools.getHeadingBetween(objectList.get(i), self) + 90) % 360;
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
                Tester.appendFile("Dodging Gas Clouds as Before", "testlog.txt");
    
                if (temp == 1){
                    Tester.appendFile("Heading right", "testlog.txt");
                    newHeading = (Tools.getHeadingBetween(objectList.get(i), self) + 270) % 360;
                }
                else{
                    Tester.appendFile("Heading left", "testlog.txt");
                    newHeading = (Tools.getHeadingBetween(objectList.get(i), self) + 90) % 360;
                }
    
                while(!near && i < objectList.size()){
                    if(!(objectList.get(i).position == cachedObject.position)){
                        if (self.getSize() + objectList.get(i).getSize() + 20 > Tools.getDistanceBetween(self, objectList.get(i))){
                            Tester.appendFile("Different Object found", "testlog.txt");
                            cachedObject = objectList.get(i);
    
                            if (temp == 1){
                                Tester.appendFile("Heading right", "testlog.txt");
                                newHeading = (Tools.getHeadingBetween(objectList.get(i), self) + 270) % 360;
                            }
                            else{
                                Tester.appendFile("Heading left", "testlog.txt");
                                newHeading = (Tools.getHeadingBetween(objectList.get(i), self) + 90) % 360;
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
    
                List<GameObject> foodlist;
                foodlist = gameState.getGameObjects()
                                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                                    .sorted(Comparator
                                            .comparing(item -> Tools.getDistanceBetween(self, item)))
                                    .collect(Collectors.toList());
                
                if(!foodlist.isEmpty()){
                    int foodDirection;
                    int objectDirection;
                    foodDirection = Tools.getHeadingBetween(foodlist.get(0), self);
                    objectDirection = Tools.getHeadingBetween(cachedObject, self);
                    Tester.appendFile("Food Heading: " + foodDirection, "testlog.txt");
                    if(Tools.aroundDegrees(foodDirection, (objectDirection + 180)%360, 60)){
                        newHeading = foodDirection;
                        dodging = false;
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

    public static int dodgeEdge(GameObject bot, GameState gameState){
        Tester.appendFile("Dodging Edge of map", "testlog.txt");
    
        // Inisialisasi
        /*
        int newHeading;
        int heading;
        List<GameObject> gasList;
        List<GameObject> playerList;

        heading = bot.currentHeading;
        newHeading = heading;
        gasList = gameState.getGameObjects()
                            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                            .sorted(Comparator
                                    .comparing(item -> Tools.getDistanceBetween(bot, item)))
                            .collect(Collectors.toList());
        playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());
        
        // Dodge Gas
        if (!gasList.isEmpty() || !playerList.isEmpty()){
            if (RadarHandler.isBig(playerList.get(1), bot.size.doubleValue())){
                if (Tools.getDistanceBetween(bot, playerList.get(1)) > Tools.getDistanceBetween(bot, gasList.get(1))){
                    System.out.println("Gas is closer than enemy.");
                    Tester.appendFile("In pursuit", "testlog.txt");
                    if(decideTurnDir(heading, bot, gasList.get(0))){
                        System.out.println("Gas is Detected on your right, Moving Left!");
                        Tester.appendFile("In pursuit", "testlog.txt");
                        newHeading = (heading + 90) % 360;
                    } else { 
                        System.out.println("Gas is Detected on your left, Moving Right!");
                        Tester.appendFile("In pursuit", "testlog.txt");
                        newHeading = (heading - 90) % 360; }
                } else {
                    System.out.println("Enemy is closer than gas.");
                    if(decideTurnDir(heading, bot, playerList.get(1))){
                        System.out.println("Enemy is Detected on your right, Moving Left!");
                        Tester.appendFile("In pursuit", "testlog.txt");
                        newHeading = (heading + 90) % 360;
                    } else { 
                        System.out.println("Enemy is Detected on your left, Moving Right!");
                        Tester.appendFile("In pursuit", "testlog.txt");
                        newHeading = (heading - 90) % 360; }
                } 
            }   
        }
         */

        return Tools.getHeadingBetween(gameState.world.getCenterPoint(), bot.position);
    }
}