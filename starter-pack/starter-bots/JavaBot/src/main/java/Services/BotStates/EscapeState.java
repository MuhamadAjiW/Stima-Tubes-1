package Services.BotStates;

import java.util.Comparator;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.StateTypes;
import Models.PlayerAction;
import Services.Response;
import Services.Tools;

public class EscapeState extends StateBase {
    public static Response runState(){
        PlayerAction nextAction = currentAction;
        StateTypes nextState = StateTypes.ESCAPE_STATE;
        boolean defaultAction = true;

        int enemyDirection = 0;

        var playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());
        var asteroidList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.ASTEROID_FIELD)
                    .sorted(Comparator
                            .comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        var wormholeList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.WORMHOLE)
                    .sorted(Comparator
                            .comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());
        var foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());
        
        if (!playerList.isEmpty()){
            enemyDirection = Tools.getHeadingBetween(playerList.get(1), self);
            if (!Tools.detectEnemy(playerList.get(1), self, Radarsize)){
                System.out.println("Enemy out of sight");
                nextState = StateTypes.DEFAULT_STATE;
                defaultAction = false;
            }
        }

        System.out.println("Torpedo count: " + self.TorpedoSalvoCount);
        if(self.TorpedoSalvoCount > 0){
            System.out.println("Firing torpedoes");
            nextAction.action = PlayerActions.FIRETORPEDOES;
            nextAction.heading = enemyDirection;
            defaultAction = false;
        }

        if(defaultAction){
            System.out.println("Escaping enemy");

            boolean notfoundFood = true;
            int i = 0;
            while (notfoundFood && i < 50){
                int closestFoodDirection = Tools.getHeadingBetween(foodList.get(i), self) + 360;
                if(closestFoodDirection > enemyDirection + 495 && closestFoodDirection < enemyDirection + 585){
                    System.out.println("Running while fetching food");
                    notfoundFood = false;
                    nextAction.heading = closestFoodDirection;
                    break;
                }
                else{
                    i++;
                }
            }

            if (notfoundFood){
                System.out.println("Just running");
                nextAction.heading = enemyDirection + 180;
            }

            nextAction.action = PlayerActions.FORWARD;
            nextState = StateTypes.ESCAPE_STATE;
        }

        retval.assign(nextState, nextAction);
        return retval;
    }
}
