package Services.BotStates;

import java.util.Comparator;
import java.util.stream.Collectors;

import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Models.GameState;
import Models.PlayerAction;
import Services.Response;
import Services.Tools;

public class AttackState extends StateBase {
    public static Response runState(GameState gameState, PlayerAction currentAction, GameObject self){
        PlayerAction nextAction = currentAction;
        StateTypes nextState = StateTypes.DEFAULT_STATE;
        boolean defaultAction = true;

        var playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        if(!playerList.isEmpty()){
            if (!Tools.detectEnemy(playerList.get(1), self, Radarsize)){
                System.out.println("Enemy out of sight");
                nextState = StateTypes.DEFAULT_STATE;
                defaultAction = false;
            }
    
            if (!Tools.detectEnemy(playerList.get(1), self, Radarsize)){
                System.out.println("Bigger enemy detected");
                nextState = StateTypes.ESCAPE_STATE;
                defaultAction = false;
            }
        }


        if(defaultAction){
            System.out.println("In pursuit");
            nextAction.action = PlayerActions.FORWARD;
            nextAction.heading = Tools.getHeadingBetween(playerList.get(1), self);
            nextState = StateTypes.ATTACK_STATE;
        }

        retval.assign(nextState, nextAction);
        return retval;
    }
}
