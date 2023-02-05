package Services.BotStates;

import Enums.StateTypes;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.Response;

public class DodgeState extends StateBase {
    public static Response runState(GameState gameState, PlayerAction currentAction, GameObject self){
        PlayerAction nextAction = currentAction;
        StateTypes nextState = StateTypes.DEFAULT_STATE;
        boolean defaultAction = true;

        
        if(defaultAction){
            System.out.println("Dodging torpedo");
            nextState = StateTypes.DODGE_STATE;
        }

        retval.assign(nextState, nextAction);
        return retval;
    }
}
