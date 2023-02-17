package Services.States;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.Common.Response;
import Services.Common.Tools;
import Services.Handlers.AttackHandler;
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
        if(!AttackHandler.teleporterPrepped){
            if (NavigationHandler.outsideBound(gameState, self) && retval.getNewAction().action != PlayerActions.FIRETELEPORT){
                retval.assign(NavigationHandler.dodgeEdge(self, gameState));
                retval.assign(PlayerActions.FORWARD);
            }
            else{
                if (RadarHandler.detectThreat(gameState, self, Radarsize/2)){
                    retval.assign(NavigationHandler.dodgeEnemy());
                }
    
                if (retval.getNewAction().action != PlayerActions.FIRETORPEDOES){
                    List<GameObject> objectList;
                    objectList = gameState.getGameObjects()
                            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD || item.getGameObjectType() == ObjectTypes.WORMHOLE)
                            .sorted(Comparator
                                    .comparing(item -> Tools.getDistanceBetween(self, item)))
                            .collect(Collectors.toList());
    
    
                    retval.assign(NavigationHandler.dodgeObjects(currentHeading, gameState, self, objectList));
                }
            }
        }
    }

    public static void fireTorpedoes(int direction){
        retval.assign(PlayerActions.FIRETORPEDOES);
        retval.assign(direction);
    }
}
