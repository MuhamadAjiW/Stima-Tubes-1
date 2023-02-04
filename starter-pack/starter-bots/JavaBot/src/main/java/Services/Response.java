package Services;

import Enums.StateTypes;
import Models.PlayerAction;

public class Response {
    private StateTypes newState;
    private PlayerAction newAction;

    public void assign(StateTypes state, PlayerAction action){
        this.newState = state;
        this.newAction = action;
    }

    public void assign(StateTypes state){
        this.newState = state;
    }

    public void assign(PlayerAction action){
        this.newAction = action;
    }

    public PlayerAction getNewAction(){
        return this.newAction;
    }

    public StateTypes getNewState(){
        return this.newState;
    }
}
