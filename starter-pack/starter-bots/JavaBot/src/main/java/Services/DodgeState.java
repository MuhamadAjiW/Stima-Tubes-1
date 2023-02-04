package Services;

import Enums.StateTypes;
import Models.GameState;
import Models.PlayerAction;

public class DodgeState {
    private static Response retval;
    
    public static Response runState(GameState gameState, PlayerAction currentAction){
        retval.assign(StateTypes.DODGE_STATE, currentAction);

        return retval;
    }
}
