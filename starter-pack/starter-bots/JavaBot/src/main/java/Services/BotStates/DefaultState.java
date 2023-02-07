package Services.BotStates;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Services.Response;
import Services.Tools;

public class DefaultState extends StateBase{
    public static Response runState(){
        boolean defaultAction;
        List<GameObject> playerList;
        List<GameObject> foodList;
        
        defaultAction = true;
        playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        if (!playerList.isEmpty()){

            

            if (Tools.detectEnemy(playerList.get(1), self, Radarsize)){
                System.out.println("Enemy within radar");
                if(Tools.isBig(playerList.get(1), self.size.doubleValue() )){
                    System.out.println("Enemy is big, size: " + playerList.get(1).size);
                    retval.assign(StateTypes.ESCAPE_STATE);
                    defaultAction = false;
                }
                else{
                    if(Tools.isSmall(playerList.get(1), self.size.doubleValue() )){
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
                System.out.println("Ship is big, firing missles nonetheless");
                retval.assign(StateTypes.ATTACK_STATE);
                defaultAction = false;
            }
        }

        if (defaultAction){
            System.out.println("Hoarding food");
            foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

            // 

            retval.assign(PlayerActions.FORWARD);
            retval.assign(Tools.getHeadingBetween(foodList.get(0), self));
            retval.assign(StateTypes.DEFAULT_STATE);
        }

        pathfind(retval.getNewAction().heading);
        return retval;
    }
}
