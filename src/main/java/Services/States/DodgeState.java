package Services.States;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.StateTypes;
import Models.GameObject;
import Services.Common.Response;
import Services.Common.Tools;
import Services.Handlers.DodgeHandler;

public class DodgeState extends StateBase {
    public static Response runState(){
        boolean defaultAction;
        defaultAction = true;


        if(defaultAction){
            //TODO: IMPLEMENTASI DODGE STATE
            System.out.println("Dodging torpedo");
            retval.assign(StateTypes.DEFAULT_STATE);
        }
        return retval;
    }

    public static Response detectTorpedoes(){
        List<GameObject> torpedoList;
        torpedoList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDOSALVO)
                    .sorted(Comparator
                            .comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());
        if (DodgeHandler.inTrajectory(self, torpedoList)){
            System.out.println("In a torpedo trajectory");
            retval.assign(StateTypes.DODGE_STATE);
        }
        return retval;
    }
}
