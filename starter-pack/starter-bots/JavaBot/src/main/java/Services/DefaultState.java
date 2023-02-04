package Services;

import Enums.StateTypes;
import Models.GameState;
import Models.PlayerAction;

public class DefaultState{
    private static Response retval;
    
    public static Response runState(GameState gameState, PlayerAction currentAction){
        retval.assign(StateTypes.DEFAULT_STATE, currentAction);

        return retval;
    }
}
