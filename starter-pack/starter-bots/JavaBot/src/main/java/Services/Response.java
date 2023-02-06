package Services;

import Enums.PlayerActions;
import Enums.StateTypes;
import Models.PlayerAction;

public class Response {
    private StateTypes newState;
    private PlayerAction newAction = new PlayerAction();

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
    public void assign(int direction){
        this.newAction.heading = direction;
    }
    public void assign(PlayerActions actions){
        this.newAction.action = actions;
    }
    public PlayerAction getNewAction(){
        return this.newAction;
    }
    public StateTypes getNewState(){
        return this.newState;
    }
    public int getHeading(){
        return this.newAction.heading;
    }
}
