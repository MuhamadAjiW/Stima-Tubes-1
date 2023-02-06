package Services.BotStates;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.Response;
import Services.Tools;

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

    public static void pathfind(int currentHeading){
        if (outsideBound()){
            System.out.println("Dodging Edge of map");
            var direction = Tools.toDegrees(Math.atan2(gameState.world.centerPoint.y - self.getPosition().y,
                                                        gameState.world.centerPoint.x - self.getPosition().x));
            direction = (direction + 360) % 360;
            retval.assign(direction);
        }
        else{
            dodgeGas(currentHeading);
        }
    }


    //Generic Subfunctions
    private static int tickLimit = 0;
    private static int cachedHeading;
    private static boolean dodging = false;

    public static void dodgeGas(int currentHeading){
        boolean near = false;
        int newHeading = currentHeading;
        int i = 0;

        var gasList = gameState.getGameObjects()
                            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                            .sorted(Comparator
                                    .comparing(item -> Tools.getDistanceBetween(self, item)))
                            .collect(Collectors.toList());

        if (gameState.world.currentTick < tickLimit){
            while(!near && i < gasList.size()){
                if (self.getSize() + gasList.get(i).getSize() + 20 > Tools.getDistanceBetween(self, gasList.get(i))){
                    System.out.println("Dodging Gas Clouds");
                    
                    Random randomNum = new Random();
                    int temp = randomNum.nextInt(2);
    
                    if (temp == 0){
                        System.out.println("Heading right");
                        newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) + 90) % 360;
                    }
                    else{
                        System.out.println("Heading left");
                        newHeading = (Tools.getHeadingBetween(self, gasList.get(i)) - 90) % 360;
                    }
                    near = true;
                    tickLimit = gameState.world.currentTick + gasList.get(i).getSize()/3;
                    cachedHeading = newHeading;
                    dodging = true;
                }
                else{
                    i++;
                }
            }
        }
        else if (dodging){
            System.out.println("Dodging Gas Clouds as Before, Current Tick: " + gameState.world.currentTick + ", target tick: " + tickLimit);
            newHeading = cachedHeading;
            dodging = false;
        }
        
        retval.assign(newHeading);
    }

    public static boolean outsideBound(){
        boolean out = false;
        if(Math.sqrt(self.getPosition().x*self.getPosition().x + self.getPosition().y*self.getPosition().y) > gameState.world.radius - self.getSize() - 20){
            out = true;
        }
        return out;
    }

    public static void fireTorpedoes(int direction){
        System.out.println("Firing torpedoes");
        retval.assign(PlayerActions.FIRETORPEDOES);
        retval.assign(direction);
        retval.assign(StateTypes.ESCAPE_STATE);
    }
}
