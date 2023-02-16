package Services.States;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Services.Common.Response;
import Services.Common.Tester;
import Services.Common.Tools;
import Services.Handlers.DodgeHandler;
import Services.Handlers.NavigationHandler;

public class DodgeState extends StateBase {
    public static int cachedHeading;
    public static int cachedDirection;

    public static Response runState(){
        
        boolean defaultAction;
        List<GameObject> torpedoList;

        defaultAction = true;
        torpedoList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDOSALVO)
                    .sorted(Comparator
                            .comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        Tester.appendFile("Hitstate: " + DodgeHandler.hit, "testlog.txt");
        if(!DodgeHandler.hit && self.size > 100){
            retval.assign(StateTypes.DEFAULT_STATE);
            retval.assign(PlayerActions.FORWARD);
            DodgeHandler.dodging = false;
        }
        else if(defaultAction){
            NavigationHandler.dodging = false;

            Tester.appendFile("Dodging state: " + DodgeHandler.dodging + " , Cached dir is " + cachedHeading, "testlog.txt");
            Tester.appendFile("Criticality: " + DodgeHandler.critical, "testlog.txt");
            //TODO: IMPLEMENTASI DODGE STATE
            Tester.appendFile("Dodging torpedo", "testlog.txt");
            Tester.appendFile("Shield count: " + self.ShieldCount, "testlog.txt");
            if(DodgeHandler.critical && self.ShieldCount > 0 && self.size > 25 && torpedoList.size() > 2){
                retval.assign(PlayerActions.ACTIVATESHIELD);
                Tester.appendFile("Shields deployed", "testlog.txt");
            }
            else{
                if(!NavigationHandler.outsideBound(gameState, self) && self.size < 100){  
                    if(!NavigationHandler.dodging){
                    
                        if (!DodgeHandler.dodging){
                            Tester.appendFile("Evasive Manoeuvre starting", "testlog.txt");
                            
                            cachedDirection = NavigationHandler.decideTurnDir(retval.getHeading(), self, gameState);
                            cachedHeading = retval.getHeading();
                            if (cachedDirection == 1){
                                Tester.appendFile("Heading right", "testlog.txt");
                                retval.assign((cachedHeading + 300)%360);
                            }
                            else{
                                Tester.appendFile("Heading left", "testlog.txt");
                                retval.assign((cachedHeading + 60)%360);
                            }
                            DodgeHandler.dodging = true;
                        }

                        else{       
                            Tester.appendFile("Evasive Manoeuvre Continuation", "testlog.txt");
                            if (cachedDirection == 1){
                                Tester.appendFile("Heading right", "testlog.txt");
                                retval.assign((cachedHeading + 300)%360);
                            }
                            else{
                                Tester.appendFile("Heading left", "testlog.txt");
                                retval.assign((cachedHeading + 60)%360);
                            }    
                        }

                        retval.assign(PlayerActions.FORWARD);
                        if(!DodgeHandler.hit){
                            retval.assign(StateTypes.DEFAULT_STATE);
                            DodgeHandler.dodging = false;
                        }
                    }
                    else{
                        retval.assign(PlayerActions.FORWARD);
                    }
                }
                else{
                    retval.assign(PlayerActions.FORWARD);
                }
            }
        }
        pathfind(retval.getHeading());
        return retval;
    }

    //sub
    public static Response detectTorpedoes(){
        List<GameObject> torpedoList;
        torpedoList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDOSALVO)
                    .sorted(Comparator
                            .comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());
        


        if(self.size > 100){
            if (DodgeHandler.inTrajectory(self, torpedoList)){
                Tester.appendFile("Big and in a torpedo trajectory", "testlog.txt");
                if(DodgeHandler.critical && self.ShieldCount > 0 && self.size > 25 && torpedoList.size() > 2){
                    retval.assign(StateTypes.DODGE_STATE);
                }
                else{
                    // kek biasa aja?
                }
            }
        }
        else{
            if (DodgeHandler.inTrajectory(self, torpedoList)){
                Tester.appendFile("In a torpedo trajectory", "testlog.txt");
                retval.assign(StateTypes.DODGE_STATE);
            }
        }


        return retval;
    }
}
