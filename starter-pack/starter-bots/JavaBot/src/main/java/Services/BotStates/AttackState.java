package Services.BotStates;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Services.Response;
import Services.Tools;

public class AttackState extends StateBase {
    public static Response runState(){
        boolean defaultAction;
        int enemyDirection;
        List<GameObject> playerList;
        
        defaultAction = true;
        playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        if(!playerList.isEmpty()){
            enemyDirection = Tools.getHeadingBetween(playerList.get(1), self);

            if (!Tools.detectEnemy(playerList.get(1), self, Radarsize)){
                if (self.TorpedoSalvoCount > 0 && self.size > 100){
                    fireTorpedoes(enemyDirection);
                    retval.assign(StateTypes.ATTACK_STATE);
                    defaultAction = false;
                }
                else{
                    System.out.println("Enemy out of sight");
                    retval.assign(StateTypes.DEFAULT_STATE);
                    defaultAction = false;
                }
            }
    
            else if(Tools.isBig(playerList.get(1), self.size.doubleValue() )){
                System.out.println("Bigger enemy detected");
                retval.assign(StateTypes.ESCAPE_STATE);
                defaultAction = false;
            }

            else {
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

    public static void defaultAction(int enemyDirection){
        System.out.println("In pursuit");
        retval.assign(PlayerActions.FORWARD);
        retval.assign(enemyDirection);
        retval.assign(StateTypes.ATTACK_STATE);
        
        pathfind(retval.getNewAction().heading);
    }
    
}
