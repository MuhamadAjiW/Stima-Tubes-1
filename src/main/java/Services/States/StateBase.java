package Services.States;

import Enums.PlayerActions;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.Common.Response;
import Services.Common.Tester;
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
            if (retval.getNewAction().action == PlayerActions.FIRETORPEDOES) {
                Tester.appendFile("gajadi tpd di ujung ring", "attack.txt");
            }
            retval.assign(NavigationHandler.dodgeEdge(self, gameState));
            retval.assign(PlayerActions.FORWARD);
        }
        else{
            if (RadarHandler.detectThreat(gameState, self, Radarsize/2)){
                if (retval.getNewAction().action == PlayerActions.FIRETORPEDOES) {
                    Tester.appendFile("gajadi tpd di ujung ring", "attack.txt");
                }
                retval.assign(NavigationHandler.dodgeEnemy());
            }

            if (retval.getNewAction().action != PlayerActions.FIRETORPEDOES){
                retval.assign(NavigationHandler.dodgeGas(currentHeading, gameState, self));
            }
        }
    }

    public static void fireTorpedoes(int direction){
        retval.assign(PlayerActions.FIRETORPEDOES);
        retval.assign(direction);
    }
}
