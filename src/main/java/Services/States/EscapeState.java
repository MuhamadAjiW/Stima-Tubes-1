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
import Services.Handlers.RadarHandler;

public class EscapeState extends StateBase {
    public static Response runState(){
        boolean defaultAction;
        int enemyDirection;
        List<GameObject> playerList;
        
        defaultAction = true;
        enemyDirection = 0;
        playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        if (!playerList.isEmpty()){
            enemyDirection = Tools.getHeadingBetween(playerList.get(1), self);
            if (!RadarHandler.detectEnemy(playerList.get(1), self, Radarsize)){
                System.out.println("Enemy out of sight");
                retval.assign(StateTypes.DEFAULT_STATE);
                defaultAction = false;
            }
            else if(RadarHandler.isSmall(playerList.get(1), self.size.doubleValue() )){
                System.out.println("Smaller enemy detected");
                retval.assign(StateTypes.ATTACK_STATE);
                defaultAction = false;
            }
            else{
                System.out.println("Enemy distance: " + Tools.getDistanceBetween(self, playerList.get(1)));
                System.out.println("Torpedo count: " + self.TorpedoSalvoCount);
                if(self.TorpedoSalvoCount > 0){
                    fireTorpedoes(enemyDirection);
                    defaultAction = false;
                }

                if(defaultAction){
                    defaultAction(enemyDirection);
                }
            }
        }
        return retval;
    }

    //Sub Actions
    public static void defaultAction(int enemyDirection){
        System.out.println("Escaping enemy");
        List<GameObject> foodList;
        boolean notfoundFood;
        int i;

        foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        notfoundFood = true;
        i = 0;
        if(!foodList.isEmpty()){
            while (notfoundFood && i < 10){
                int closestFoodDirection;
                closestFoodDirection = Tools.getHeadingBetween(foodList.get(i), self);
                if(Tools.aroundDegrees(closestFoodDirection, (enemyDirection + 180)%360, 20)){
                    System.out.println("Running while fetching food");
                    System.out.println("Enemy direction is: " + enemyDirection + ", food direction is: " + closestFoodDirection);
                    notfoundFood = false;
                    retval.assign(closestFoodDirection);
                    break;
                }
                else{
                    i++;
                }
            }
        }
        
        if (notfoundFood){
            System.out.println("Just running");
            retval.assign((enemyDirection + 180)%360);
        }
        
        pathfind(retval.getNewAction().heading);
        retval.assign(PlayerActions.FORWARD);
        retval.assign(StateTypes.ESCAPE_STATE);
    }
}
