package Services.BotStates;

import java.util.Comparator;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.Response;
import Services.Tools;

public class DefaultState extends StateBase{
    public static Response runState(GameState gameState, PlayerAction currentAction, GameObject self){
        PlayerAction nextAction = currentAction;
        StateTypes nextState = StateTypes.DEFAULT_STATE;
        boolean defaultAction = true;

        var playerList = gameState.getPlayerGameObjects().stream()
                            .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                            .collect(Collectors.toList());

        if (!playerList.isEmpty()){
            System.out.println(playerList.size() + " " + Tools.getDistanceBetween(self, playerList.get(1)));
            if (Tools.detectEnemy(playerList.get(1), self, Radarsize)){
                System.out.println("Enemy within radar");
                if(Tools.isBig(playerList.get(1), self.size.doubleValue() )){
                    System.out.println("Enemy is big, size: " + playerList.get(1).size);
                    nextState = StateTypes.ESCAPE_STATE;
                    defaultAction = false;
                }
                else{
                    if(Tools.isSmall(playerList.get(1), self.size.doubleValue() )){
                        System.out.println("Enemy is small, size: " + playerList.get(1).size);
                        nextState = StateTypes.ATTACK_STATE;
                        defaultAction = false;
                    }
                    else{
                        System.out.println("Enemy is uncertain, size: " + playerList.get(1).size);
                        //TODO: Gimana kalo nanggung sizenya?
                        defaultAction = true;
                    }
                }
            }
        }

        if (defaultAction){
            System.out.println("Hoarding food");
            var foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

            // 

            nextAction.action = PlayerActions.FORWARD;
            nextAction.heading = Tools.getHeadingBetween(foodList.get(0), self);
            nextState = StateTypes.DEFAULT_STATE;
        }

        retval.assign(nextState, nextAction);
        return retval;
    }
}
