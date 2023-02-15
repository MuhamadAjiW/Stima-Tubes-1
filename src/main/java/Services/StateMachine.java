package Services;

import Enums.*;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.Common.Response;
import Services.States.AttackState;
import Services.States.DefaultState;
import Services.States.DodgeState;
import Services.States.EscapeState;
import Services.States.StateBase;
import Services.Common.Tester;
import Services.Handlers.AttackHandler;

public class StateMachine{
    private static StateTypes CURRENTSTATE = StateTypes.DEFAULT_STATE;
    private Response response;

    public void changeState(StateTypes NewState){
        CURRENTSTATE = NewState;
        Tester.appendFile("Change: " + CURRENTSTATE, "testlog.txt");
    }

    public PlayerAction determineAction(GameState gameState, PlayerAction currentAction, GameObject self){
        Tester.appendFile("-------------------------------------------------------------", "testlog.txt");
        Tester.appendFile("Current Tick: " + gameState.world.currentTick, "testlog.txt");
        Tester.appendFile("Player Remaining: " + gameState.getPlayerGameObjects().size(), "testlog.txt");
        Tester.appendFile("Size: " + self.getSize(), "testlog.txt");
        Tester.appendFile("Supernova in inventory: " + self.SupernovaAvailable, "testlog.txt");
        
        StateBase.updateState(gameState, self, currentAction);
        
        Tester.appendFile("Teleporter ready: " + AttackHandler.teleporterPrepped, "testlog.txt");
        if(!AttackHandler.teleporterPrepped){
            response = DodgeState.detectTorpedoes();
            changeState(response.getNewState());
        }

        Tester.appendFile("Switch Action: " + CURRENTSTATE.name(), "testlog.txt");
        switch (CURRENTSTATE) {
            case ATTACK_STATE:
                Tester.appendFile("Attack", "testlog.txt");
                response = AttackState.runState();
                break;

            case ESCAPE_STATE:
                Tester.appendFile("Escape", "testlog.txt");
                response = EscapeState.runState();
                break;
        
            case DODGE_STATE:
                Tester.appendFile("Dodge", "testlog.txt");
                response = DodgeState.runState();
                break;
                
            default:
                Tester.appendFile("Default", "testlog.txt");
                response = DefaultState.runState();
                break;
        }
        
        changeState(response.getNewState());
        if(AttackHandler.teleporterFired){
            AttackState.detectTeleporter();
        }
        if(AttackHandler.supernovaFired){
            AttackState.detectSupernova();
        }
        Tester.appendFile("new state: " + response.getNewState(), "testlog.txt");
        return response.getNewAction();
    }
}
