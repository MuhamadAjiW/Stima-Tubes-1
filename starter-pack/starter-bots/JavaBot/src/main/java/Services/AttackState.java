package Services;

import Enums.StateTypes;
import Models.GameState;
import Models.PlayerAction;

public class AttackState {
    private static Response retval;
    
    public static Response runState(GameState gameState, PlayerAction currentAction){
        retval.assign(StateTypes.ATTACK_STATE, currentAction);

        return retval;
    }
}
