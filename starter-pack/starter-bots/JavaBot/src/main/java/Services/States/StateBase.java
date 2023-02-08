package Services.States;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.Common.Response;
import Services.Common.Tools;
import Services.Handlers.NavigationHandler;
import Services.Handlers.RadarHandler;

public class StateBase {
    public static GameState gameState;
    public static GameObject self;
    public static PlayerAction currentAction;
    public static Response retval = new Response();

    public static double Radarsize = 100;
    public static double smallThreshold = 100;

    public static void updateState(GameState newGameState, GameObject bot, PlayerAction action){
        gameState = newGameState;
        self = bot;
        currentAction = action;
    }


    //Generic Actions
    private static int cachedHeading;
    private static boolean dodging = false;
    private static int temp;
    private static GameObject cachedGas;

    public static void pathfind(int currentHeading){
        if (NavigationHandler.outsideBound(gameState, self)){
            dodgeEdge();
        }
        else{
            if (RadarHandler.detectThreat(gameState, self, Radarsize - 70)){
                dodgeEnemy();
            }
            dodgeGas(currentHeading);
        }
    }

    public static void fireTorpedoes(int direction){
        System.out.println("Firing torpedoes");
        retval.assign(PlayerActions.FIRETORPEDOES);
        retval.assign(direction);
        retval.assign(StateTypes.ESCAPE_STATE);
    }


    //Generic Subfunctions
    public static void dodgeEnemy(){
        System.out.println("Bigger enemy getting too close, prioritizing escape");

        retval.assign(StateTypes.ESCAPE_STATE);
        dodging = false;
    }

    public static void dodgeEdge(){
        System.out.println("Dodging Edge of map");

        int direction;
        direction = Tools.getHeadingBetween(self.getPosition(), gameState.world.getCenterPoint());
        System.out.println("Heading: " + self.currentHeading + ", should be: " + direction);
        direction = (direction + 180) % 360;

        cachedHeading = direction;
        retval.assign(direction);
        dodging = false;
    }

    public static void dodgeGas(int currentHeading){
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
        retval.assign(newHeading);
    }
}
