package Services;

import Enums.*;
import Models.GameState;
import Models.PlayerAction;

public class StateMachine{
    private StateTypes CURRENTSTATE = StateTypes.DEFAULT_STATE;

    public void changeState(StateTypes NewState){
        CURRENTSTATE = NewState;
    }

    public PlayerAction determineAction(GameState gameState, PlayerAction currentAction){
        Response response;
        switch (CURRENTSTATE) {
            case ATTACK_STATE:
                response = AttackState.runState(gameState, currentAction);
                
            case ESCAPE_STATE:
                response = EscapeState.runState(gameState, currentAction);
        
            case DODGE_STATE:
                response = DodgeState.runState(gameState, currentAction);

            default:
                response = DefaultState.runState(gameState, currentAction);
        }
        changeState(response.getNewState());
        return response.getNewAction();
    }
}