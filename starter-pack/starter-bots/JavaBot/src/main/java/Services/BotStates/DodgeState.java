package Services.BotStates;

import Enums.StateTypes;
import Services.Response;

public class DodgeState extends StateBase {
    public static Response runState(){
        boolean defaultAction;
        
        defaultAction = true;
        
        if(defaultAction){
            System.out.println("Dodging torpedo");
            retval.assign(StateTypes.DODGE_STATE);
        }

        return retval;
    }
}
