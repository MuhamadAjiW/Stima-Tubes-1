package Services;

import Enums.*;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.BotStates.AttackState;
import Services.BotStates.DefaultState;
import Services.BotStates.DodgeState;
import Services.BotStates.EscapeState;

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

        switch (CURRENTSTATE) {
            case ATTACK_STATE:
                System.out.println("Attack");
                response = AttackState.runState(gameState, currentAction, self);
                break;

            case ESCAPE_STATE:
                System.out.println("Escape");
                response = EscapeState.runState(gameState, currentAction, self);
                break;
        
            case DODGE_STATE:
                System.out.println("Dodge");
                response = DodgeState.runState(gameState, currentAction, self);
                break;
                
            default:
                System.out.println("Default");
                response = DefaultState.runState(gameState, currentAction, self);
                break;
        }
        changeState(response.getNewState());
        System.out.println("new state: " + response.getNewState());
        return response.getNewAction();
    }
}