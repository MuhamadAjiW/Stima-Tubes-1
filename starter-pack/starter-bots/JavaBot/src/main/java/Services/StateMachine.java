package Services;

import Enums.*;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.BotStates.AttackState;
import Services.BotStates.DefaultState;
import Services.BotStates.DodgeState;
import Services.BotStates.EscapeState;
import Services.BotStates.StateBase;

public class StateMachine{
    private static StateTypes CURRENTSTATE = StateTypes.DEFAULT_STATE;
    private Response response;

    public void changeState(StateTypes NewState){
        CURRENTSTATE = NewState;
        System.out.println("Change: " + CURRENTSTATE);
    }

    public PlayerAction determineAction(GameState gameState, PlayerAction currentAction, GameObject self){
        System.out.println("-------------------------------------------------------------");
        System.out.println("Switch Action: " + CURRENTSTATE.name());

        StateBase.updateState(gameState, self, currentAction);
        switch (CURRENTSTATE) {
            case ATTACK_STATE:
                System.out.println("Attack");
                response = AttackState.runState();
                break;

            case ESCAPE_STATE:
                System.out.println("Escape");
                response = EscapeState.runState();
                break;
        
            case DODGE_STATE:
                System.out.println("Dodge");
                response = DodgeState.runState();
                break;
                
            default:
                System.out.println("Default");
                response = DefaultState.runState();
                break;
        }
        changeState(response.getNewState());
        System.out.println("new state: " + response.getNewState());
        return response.getNewAction();
    }
}