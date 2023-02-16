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
import Services.Handlers.RadarHandler;
import Services.Handlers.AttackHandler;

public class EscapeState extends StateBase {
    public static Response runState(){
        boolean defaultAction;
        int enemyDirection;
        List<GameObject> playerList;
        
        defaultAction = true;
        playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        if (!playerList.isEmpty()){

            enemyDirection = Tools.getHeadingBetween(playerList.get(1), self);

            if (!RadarHandler.detectEnemy(playerList.get(1), self, Radarsize)){
                Tester.appendFile("Enemy out of sight", "testlog.txt");
                retval.assign(StateTypes.DEFAULT_STATE);
                retval.assign(PlayerActions.FORWARD);
                defaultAction = false;
            }
            else if(RadarHandler.isSmall(playerList.get(1), self.size.doubleValue() )){
                Tester.appendFile("Smaller enemy detected", "testlog.txt");
                retval.assign(StateTypes.ATTACK_STATE);
                retval.assign(PlayerActions.FORWARD);
                defaultAction = false;
            }
            else{
                Tester.appendFile("Enemy distance: " + Tools.getDistanceBetween(self, playerList.get(1)), "testlog.txt");
                Tester.appendFile("Torpedo count: " + self.TorpedoSalvoCount, "testlog.txt");
                if(self.TorpedoSalvoCount > 0 && self.size > 10){
                    fireTorpedoes(AttackHandler.aimv1(self, playerList.get(1), 20));
                    defaultAction = false;
                }

                if(defaultAction){
                    defaultAction(enemyDirection);
                }
            }
        }
        pathfind(retval.getHeading());
        return retval;
    }

    //Sub Actions
    public static void defaultAction(int enemyDirection){
        Tester.appendFile("Escaping enemy", "testlog.txt");
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
            while (notfoundFood && i < Math.min(10, foodList.size())){
                int closestFoodDirection;
                closestFoodDirection = Tools.getHeadingBetween(foodList.get(i), self);
                if(Tools.aroundDegrees(closestFoodDirection, (enemyDirection + 180)%360, 20)){
                    Tester.appendFile("Running while fetching food", "testlog.txt");
                    Tester.appendFile("Enemy direction is: " + enemyDirection + ", food direction is: " + closestFoodDirection, "testlog.txt");
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
            Tester.appendFile("Just running", "testlog.txt");
            retval.assign((enemyDirection + 180)%360);
        }
        retval.assign(PlayerActions.FORWARD);
    }
}
