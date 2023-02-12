package Services.States;

import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.Common.Response;
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
            retval.assign(NavigationHandler.dodgeEdge(self, gameState));
        }
        else{
            if (RadarHandler.detectThreat(gameState, self, Radarsize - 70)){
                retval.assign(NavigationHandler.dodgeEnemy());
            }
            retval.assign(NavigationHandler.dodgeGas(currentHeading, gameState, self));
            
        }
    }

    public static void fireTorpedoes(int direction){
        System.out.println("Firing torpedoes");
        retval.assign(PlayerActions.FIRETORPEDOES);
        retval.assign(direction);
        retval.assign(StateTypes.ESCAPE_STATE);
    }
}
