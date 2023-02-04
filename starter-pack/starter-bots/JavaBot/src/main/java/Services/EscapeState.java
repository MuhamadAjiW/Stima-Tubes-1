package Services;

import Enums.StateTypes;
import Models.GameState;
import Models.PlayerAction;

public class EscapeState {
    private static Response retval;
    
    public static Response runState(GameState gameState, PlayerAction currentAction){
        retval.assign(StateTypes.ESCAPE_STATE, currentAction);

        return retval;
    }
}
