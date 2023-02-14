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
import Services.Common.Effect;
import Services.Handlers.RadarHandler;
import Services.Handlers.NavigationHandler;
import Services.Handlers.AttackHandler;
import Services.Common.Tester;

public class AttackState extends StateBase {
    
    public static Response runState() {
        boolean defaultAction;
        int aim0, aim1, aim2, aim3, fixAim;
        GameObject nearestEnemy, nearestGasCloud;

        List<GameObject> playerList;
        List<GameObject> gasList;

        defaultAction = true;
        playerList = gameState.getPlayerGameObjects().stream()
                .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                .collect(Collectors.toList());

        if (!playerList.isEmpty()) {
            gasList = gameState.getGameObjects()
                        .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                        .sorted(Comparator
                        .comparing(item -> Tools.getDistanceBetween(playerList.get(0), item)))
                        .collect(Collectors.toList());
            nearestEnemy = playerList.get(1);
            nearestGasCloud = gasList.get(0);
            aim0 = AttackHandler.aimv0(self, nearestEnemy);
            Effect enemyEffect = new Effect(nearestEnemy.Effects); // TODO : declare di luar juga, if supernova avail & safe -> attackstate
            if (self.SupernovaAvailable > 0) { // TODO : + check world.radius > 1.5 supernova size
                System.out.println("finding supernova target");
                Tester.appendFile("finding supernova target", "attack.txt");

                for (int i = 1; i < playerList.size(); i++) {
                    nearestEnemy = playerList.get(i);
                    if (AttackHandler.detAttckRange(self, nearestEnemy) <= 1) {
                        fixAim = AttackHandler.aimv1(self, nearestEnemy, 20);
                        retval.assign(StateTypes.ATTACK_STATE);
                        retval.assign(PlayerActions.FIRESUPERNOVA);
                        retval.assign(fixAim);
                        
                        Tester.appendFile("found supernova target to " + Integer.toString(fixAim), "attack.txt");
                        defaultAction = false;
                        break;
                    }
                }
            }

            if (defaultAction) {
                if (AttackHandler.detAttckRange(self, nearestEnemy) <= 1) { // kalo enemy jauh / di luar attack range
                    System.out.println("enemy range level 1");
                    Tester.appendFile("enemy range level 1", "attack.txt");

                    if (self.TorpedoSalvoCount > 0 && self.size > 100) {
                        System.out.println("firing torpedoes");
                        Tester.appendFile("firing torpedoes to " + Integer.toString(AttackHandler.aimv1(self, nearestEnemy, 60)), "attack.txt");
                        fireTorpedoes(AttackHandler.aimv1(self, nearestEnemy, 60));
                        retval.assign(StateTypes.ATTACK_STATE);
                        defaultAction = false;
                    } else {
                        System.out.println("no torpedoes");
                        Tester.appendFile("no torpedoes", "attack.txt");
                        retval.assign(StateTypes.DEFAULT_STATE);
                        defaultAction = false;
                    }
                } else if (RadarHandler.isBig(playerList.get(1), self.size.doubleValue())) { // enemy lbh gede => jgn attack baik midrange atau closerange
                    System.out.println("Bigger enemy detected");
                    Tester.appendFile("bigger enemy", "attack.txt");
                    retval.assign(StateTypes.ESCAPE_STATE);
                    retval.assign(PlayerActions.STOP);
                    defaultAction = false;
                } else if (AttackHandler.detAttckRange(self, nearestEnemy) == 2) {
                    /* if (self.size() - 20 > 1.5 * enemy.getsize() && self.TeleporterCount > 0) {
                        retval.assign(AttackHandler.aimv1(self, enemy, 20)); 
                    } */
                    System.out.println("enemy range level 2");
                    Tester.appendFile("enemy range level 2", "attack.txt");
                    if (self.TorpedoSalvoCount > 0) {
                        aim1 = AttackHandler.aimv1(self,nearestEnemy,60);
                        aim2 = AttackHandler.aimv2(self,nearestEnemy);
                        aim3 = AttackHandler.aimv3(self,nearestEnemy, nearestGasCloud);
                        if (NavigationHandler.outsideBound(gameState, nearestEnemy) && aim2 != -9999) {
                            Tester.appendFile("enemy is near outer ring!! lesgow", "attack.txt");
                            retval.assign(aim2);
                            Tester.appendFile("firing torpedoes to aim2: " + Integer.toString(aim2), "attack.txt");
                        } else if (aim3 != -9999) {
                            Tester.appendFile("enemy is near gas cloud", "attack.txt");
                            retval.assign(aim3);
                            Tester.appendFile("firing torpedoes to aim3: " + Integer.toString(aim3), "attack.txt");
                        } else {
                            retval.assign(aim1);
                            Tester.appendFile("firing torpedoes to aim1: " + Integer.toString(aim1), "attack.txt");
                        }
                        retval.assign(StateTypes.ATTACK_STATE);
                        retval.assign(PlayerActions.FIRETORPEDOES);
                        defaultAction = false;
                        System.out.println("firing torpedoes");
                    } else {
                        System.out.println("no torpedoes");
                        Tester.appendFile("no torpedoes", "attack.txt");
                        retval.assign(StateTypes.DEFAULT_STATE);
                        defaultAction = false;
                    }
                } else if (AttackHandler.detAttckRange(self, nearestEnemy) == 3) {
                    System.out.println("enemy range level 3");
                    Tester.appendFile("enemy range level 3", "attack.txt");
                    /* if (self.size() - 20 > 1.5 * enemy.getsize() && self.TeleporterCount > 0) {
                        fixAim = AttackHandler.aimv1(self, enemy, 20);
                        retval.assign(StateTypes.ATTACK_STATE);
                        retval.assign(PlayerActions.FIRETELEPORTER);
                        retval.assign(fixAim);
                        defaultAction = false;
                    } */
                    if (self.TorpedoSalvoCount > 0 /* && self.size > 100 */ && !enemyEffect.isShield()) {
                        System.out.println("firing torpedoes");
                        retval.assign(StateTypes.ATTACK_STATE);
                        retval.assign(PlayerActions.FIRETORPEDOES);
                        aim1 = AttackHandler.aimv1(self, nearestEnemy, 60);
                        retval.assign(aim1);
                        defaultAction = false;
                        //fireTorpedoes(AttackHandler.aimv1(self, nearestEnemy, 60));
                        Tester.appendFile("firing torpedoes to " + Integer.toString(aim1), "attack.txt");
                        retval.assign(StateTypes.ATTACK_STATE);
                        defaultAction = false;
                    } else {
                        Tester.appendFile("default act: kejar", "attack.txt");
                        defaultAction(aim0);
                    }
                } else {
                    System.out.println("else");
                    Tester.appendFile("else", "attack.txt");
                    System.out.println("Enemy distance: " + Tools.getDistanceBetween(self, playerList.get(1)));
                    System.out.println("Torpedo count: " + self.TorpedoSalvoCount);

                    if (self.TorpedoSalvoCount > 0 && self.size > 10) {
                        System.out.println("firing torpedoes");
                        aim1 = AttackHandler.aimv1(self, nearestEnemy, 60);
                        Tester.appendFile("firing torpedoes to " + Integer.toString(aim1), "attack.txt");
                        fireTorpedoes(aim1);
                        defaultAction = false;
                    }

                    if (defaultAction) {
                        defaultAction(aim0);
                    }
                }
            }
        }

        pathfind(retval.getHeading());
        return retval;
    }

    // sub
    public static void defaultAction(int enemyDirection) {
        System.out.println("In pursuit");
        retval.assign(PlayerActions.FORWARD);
        retval.assign(enemyDirection);
    }

}