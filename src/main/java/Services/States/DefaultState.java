package Services.States;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Services.Common.Response;
import Services.Common.Tools;
import Services.Handlers.NavigationHandler;
import Services.Handlers.RadarHandler;

public class DefaultState extends StateBase{
    public static Response runState(){
        boolean defaultAction;
        List<GameObject> playerList;
        
        defaultAction = true;
        playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        if (!playerList.isEmpty()){

            

            if (RadarHandler.detectEnemy(playerList.get(1), self, Radarsize)){
                System.out.println("Enemy within radar");
                if(RadarHandler.isBig(playerList.get(1), self.size.doubleValue() )){
                    System.out.println("Enemy is big, size: " + playerList.get(1).size);
                    retval.assign(StateTypes.ESCAPE_STATE);
                    defaultAction = false;
                }
                else{
                    if(RadarHandler.isSmall(playerList.get(1), self.size.doubleValue() )){
                        System.out.println("Enemy is small, size: " + playerList.get(1).size);
                        retval.assign(StateTypes.ATTACK_STATE);
                        defaultAction = false;
                    }
                    else{
                        System.out.println("Enemy is uncertain, size: " + playerList.get(1).size);
                        //TODO: Gimana kalo nanggung sizenya?
                        defaultAction = true;
                    }
                }
            }
            else if (self.TorpedoSalvoCount > 0 && self.size > 100){
                if(!NavigationHandler.outsideBound(gameState, self)){
                    System.out.println("Ship is big, firing missles nonetheless");
                    retval.assign(StateTypes.ATTACK_STATE);
                    defaultAction = false;
                }
            }
        }

        if (defaultAction){
            defaultAction();
        }

        pathfind(retval.getNewAction().heading);
        return retval;
    }

    public static void defaultAction(){
        System.out.println("Hoarding food");

        List<GameObject> foodList;
        foodList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                .sorted(Comparator
                        .comparing(item -> Tools.getDistanceBetween(self, item)))
                .collect(Collectors.toList());

        // 

        retval.assign(PlayerActions.FORWARD);
        if(!foodList.isEmpty()){
            retval.assign(Tools.getHeadingBetween(foodList.get(0), self));
        }
        else{
            retval.assign(Tools.getHeadingBetween(self.getPosition(), gameState.world.getCenterPoint()));
        }
        retval.assign(PlayerActions.FORWARD);
    }
}
