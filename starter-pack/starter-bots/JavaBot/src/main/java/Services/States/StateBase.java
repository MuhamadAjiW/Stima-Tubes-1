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
    public static void pathfind(int currentHeading){
        if (NavigationHandler.outsideBound(gameState, self)){
            System.out.println("Dodging Edge of map");
            int direction;


            direction = Tools.getHeadingBetween(self.getPosition(), gameState.world.getCenterPoint());
            System.out.println("Heading: " + self.currentHeading + ", should be: " + direction);
            direction = (direction + 180) % 360;
            retval.assign(direction);
            dodging = false;
        }
        else{
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
    private static int cachedHeading;
    private static boolean dodging = false;
    private static int temp;
    private static GameObject cachedGas;

    public static void dodgeGas(int currentHeading){
        boolean near;
        int newHeading;
        int i;
        List<GameObject> gasList;
        List<GameObject> playerList;

        near = false;
        newHeading = currentHeading;
        i = 0;

        gasList = gameState.getGameObjects()
                            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                            .sorted(Comparator
                                    .comparing(item -> Tools.getDistanceBetween(self, item)))
                            .collect(Collectors.toList());

        
        playerList = gameState.getPlayerGameObjects().stream()
                            .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                            .collect(Collectors.toList());

        if (!dodging){
            if (RadarHandler.detectEnemy(playerList.get(1), self, Radarsize - 70)){
                if(RadarHandler.isBig(playerList.get(1), self.size.doubleValue() )){
                    System.out.println("Bigger enemy getting too close, prioritizing escape, size: " + playerList.get(1).size);
                    retval.assign(StateTypes.ESCAPE_STATE);
                    dodging = false;
                }
            }
            else{
                while(!near && i < gasList.size()){
                    if (self.getSize() + gasList.get(i).getSize() + 20 > Tools.getDistanceBetween(self, gasList.get(i))){
                        System.out.println("Dodging Gas Clouds");
                        cachedGas = gasList.get(i);
                        
                        temp = NavigationHandler.decideTurnDir(currentHeading, self, gameState);
        
                        if (temp == 0){
                            System.out.println("Heading right");
                            newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) + 90) % 360;
                        }
                        else{
                            System.out.println("Heading left");
                            newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) - 90) % 360;
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
        }
        else if (dodging){
            if (RadarHandler.detectEnemy(playerList.get(1), self, Radarsize - 70)){
                if(RadarHandler.isBig(playerList.get(1), self.size.doubleValue() )){
                    System.out.println("Bigger enemy getting too close, prioritizing escape" + playerList.get(1).size);
                    retval.assign(StateTypes.ESCAPE_STATE);
                    dodging = false;
                }
            }
            else{
                System.out.println("Dodging Gas Clouds as Before");
                if (temp == 0){
                    System.out.println("Heading right");
                    newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) + 90) % 360;
                }
                else{
                    System.out.println("Heading left");
                    newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) - 90) % 360;
                }

                while(!near && i < gasList.size()){
                    if(!(gasList.get(i).position == cachedGas.position)){
                        if (self.getSize() + gasList.get(i).getSize() + 20 > Tools.getDistanceBetween(self, gasList.get(i))){
                            System.out.println("Different Gas found");
                            cachedGas = gasList.get(i);
            
                            if (temp == 0){
                                System.out.println("Heading right");
                                newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) + 90) % 360;
                            }
                            else{
                                System.out.println("Heading left");
                                newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) - 90) % 360;
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
            }   
            if (newHeading == (cachedHeading + 90)%360 || newHeading == (cachedHeading - 90)%360){
                dodging = false;
            }
        }
        retval.assign(newHeading);
    }
}
