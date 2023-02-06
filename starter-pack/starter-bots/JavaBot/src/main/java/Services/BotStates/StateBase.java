package Services.BotStates;

import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.Response;

public class StateBase {
    public static GameState gameState;
    public static GameObject self;
    public static PlayerAction currentAction;
    public static Response retval = new Response();

    public static double Radarsize = 500;
    
    public static double smallThreshold = 100;

    public static void updateState(GameState newGameState, GameObject bot, PlayerAction action){
        gameState = newGameState;
        self = bot;
        currentAction = action;
    }
}
