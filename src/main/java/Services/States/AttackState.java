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
        Tester.appendFile("TeleporerFired: " + AttackHandler.teleporterFired, "testlog.txt");
        Tester.appendFile("TeleporerPrepped: " + AttackHandler.teleporterPrepped, "testlog.txt");
        Tester.appendFile("TeleporerEmpty: " + AttackHandler.teleporterEmpty, "testlog.txt");

        boolean defaultAction;
        int aim0, aim1, aim2, aim3, fixAim;
        GameObject nearestEnemy, nearestGasCloud;

        List<GameObject> playerList;
        List<GameObject> gasList;
        List<GameObject> teleporterList;
        List<GameObject> supernovaList;

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

            if(!gasList.isEmpty()){
                nearestGasCloud = gasList.get(0);
            }
            else{
                nearestGasCloud = nearestEnemy;
            }

            if (AttackHandler.teleporterEmpty){
                teleporterList = gameState.getGameObjects()
                                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
                                .collect(Collectors.toList());

                if (teleporterList.isEmpty()){
                    AttackHandler.teleporterdelay = 0;
                    AttackHandler.teleporterFired = false;
                    AttackHandler.teleporterEmpty = false;
                }
            }
            if (AttackHandler.supernovaEmpty){
                supernovaList = gameState.getGameObjects()
                                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVABOMB)
                                .collect(Collectors.toList());

                if(supernovaList.isEmpty()){
                    AttackHandler.teleporterdelay = 0;
                    AttackHandler.supernovaFired = false;
                    AttackHandler.supernovaEmpty = false;
                }
            }
            if (AttackHandler.teleporterPrepped){
                Tester.appendFile("Firing teleporter", "testlog.txt");
                retval.assign(StateTypes.ATTACK_STATE);
                retval.assign(PlayerActions.FIRETELEPORT);
                AttackHandler.teleporterdelay = 0;
                AttackHandler.teleporterFired = true;
                AttackHandler.teleporterPrepped = false;
                defaultAction = false;
            }

            aim0 = AttackHandler.aimv0(self, nearestEnemy);
            if (Tools.getDistanceBetween(self, nearestEnemy) > 300){
                retval.assign(StateTypes.DEFAULT_STATE);
                retval.assign(PlayerActions.FORWARD);
            }
            else{
                Effect enemyEffect = new Effect(nearestEnemy.Effects); // TODO : declare di luar juga, if supernova avail & safe -> attackstate
                if (self.SupernovaAvailable > 0) { // TODO : + check world.radius > 1.5 supernova size
                    Tester.appendFile("finding supernova target", "testlog.txt");

                    for (int i = 1; i < playerList.size(); i++) {
                        nearestEnemy = playerList.get(i);
                        if (AttackHandler.detAttckRange(self, nearestEnemy) <= 2) {
                            fixAim = AttackHandler.aimv0(self, nearestEnemy);
                            retval.assign(StateTypes.ATTACK_STATE);
                            retval.assign(PlayerActions.FIRESUPERNOVA);
                            retval.assign(fixAim);
                            
                            Tester.appendFile("found supernova target to " + Integer.toString(fixAim), "testlog.txt");
                            defaultAction = false;
                            AttackHandler.supernovaFired = true;
                            break;
                        }
                    }
                }

                if (defaultAction) {
                    if (AttackHandler.detAttckRange(self, nearestEnemy) <= 1) { // kalo enemy jauh / di luar attack range
                        Tester.appendFile("enemy range level 1", "testlog.txt");
                        if (self.size - 30 > nearestEnemy.size && self.TeleporterCount > 0 && !AttackHandler.teleporterFired) {
                            Tester.appendFile("Prepping teleporter", "testlog.txt");
                            fixAim = AttackHandler.aimv1(self, nearestEnemy, 20);
                            retval.assign(StateTypes.ATTACK_STATE);
                            retval.assign(PlayerActions.FORWARD);
                            retval.assign(fixAim);
                            AttackHandler.teleporterdelay = 0;
                            AttackHandler.teleporterPrepped = true;
                            defaultAction = false;
                        }
                        else if (self.TorpedoSalvoCount > 0 && self.size > 50) {
                            Tester.appendFile("firing torpedoes to " + Integer.toString(AttackHandler.aimv1(self, nearestEnemy, 20)), "testlog.txt");
                            fireTorpedoes(AttackHandler.aimv1(self, nearestEnemy, 20));
                            retval.assign(StateTypes.ATTACK_STATE);
                            defaultAction = false;
                            AttackHandler.teleporterPrepped = false;
                        } else {
                            Tester.appendFile("no torpedoes", "testlog.txt");
                            defaultAction = true;
                            AttackHandler.teleporterPrepped = false;
                        }
                    } else if (RadarHandler.isBig(playerList.get(1), self.size.doubleValue() + 10)) { // enemy lbh gede => jgn attack baik midrange atau closerange
                        Tester.appendFile("bigger enemy", "testlog.txt");
                        retval.assign(StateTypes.ESCAPE_STATE);
                        retval.assign(PlayerActions.FORWARD);
                        defaultAction = false;
                        AttackHandler.teleporterPrepped = false;
                    } else if (AttackHandler.detAttckRange(self, nearestEnemy) == 2) {
                        Tester.appendFile("enemy range level 2", "testlog.txt");
                        if (self.size - 30 > nearestEnemy.size && self.TeleporterCount > 0 && !AttackHandler.teleporterFired) {
                            Tester.appendFile("Prepping teleporter", "testlog.txt");
                            fixAim = AttackHandler.aimv1(self, nearestEnemy, 20);
                            retval.assign(StateTypes.ATTACK_STATE);
                            retval.assign(PlayerActions.FORWARD);
                            retval.assign(fixAim);
                            AttackHandler.teleporterdelay = 0;
                            AttackHandler.teleporterPrepped = true;
                            defaultAction = false;
                        }
                        else if (self.TorpedoSalvoCount > 0 && self.size > 50) {
                            aim1 = AttackHandler.aimv1(self,nearestEnemy,20);
                            aim2 = AttackHandler.aimv2(self,nearestEnemy);
                            aim3 = AttackHandler.aimv3(self,nearestEnemy, nearestGasCloud);
                            if (NavigationHandler.outsideBound(gameState, nearestEnemy) && aim2 != -9999) {
                                Tester.appendFile("enemy is near outer ring!! lesgow", "testlog.txt");
                                retval.assign(aim2);
                                Tester.appendFile("firing torpedoes to aim2: " + Integer.toString(aim2), "testlog.txt");
                            } else if (aim3 != -9999) {
                                Tester.appendFile("enemy is near gas cloud", "testlog.txt");
                                retval.assign(aim3);
                                Tester.appendFile("firing torpedoes to aim3: " + Integer.toString(aim3), "testlog.txt");
                            } else {
                                retval.assign(aim1);
                                Tester.appendFile("firing torpedoes to aim1: " + Integer.toString(aim1), "testlog.txt");
                            }
                            retval.assign(StateTypes.ATTACK_STATE);
                            retval.assign(PlayerActions.FIRETORPEDOES);
                            defaultAction = false;
                            AttackHandler.teleporterPrepped = false;
                        } else {
                            Tester.appendFile("no torpedoes", "testlog.txt");
                            defaultAction = true;
                            AttackHandler.teleporterPrepped = false;
                        }
                    } else if (AttackHandler.detAttckRange(self, nearestEnemy) == 3) {
                        Tester.appendFile("enemy range level 3", "testlog.txt");
                        if (self.size - 30 > nearestEnemy.size && self.TeleporterCount > 0 && !AttackHandler.teleporterFired) {
                            Tester.appendFile("Prepping teleporter", "testlog.txt");
                            fixAim = AttackHandler.aimv1(self, nearestEnemy, 20);
                            retval.assign(StateTypes.ATTACK_STATE);
                            retval.assign(PlayerActions.FORWARD);
                            retval.assign(fixAim);
                            AttackHandler.teleporterdelay = 0;
                            AttackHandler.teleporterPrepped = true;
                            defaultAction = false;
                        }
                        else if (self.TorpedoSalvoCount > 0  && self.size > 50  && !enemyEffect.isShield()) {
                            retval.assign(StateTypes.ATTACK_STATE);
                            retval.assign(PlayerActions.FIRETORPEDOES);
                            aim1 = AttackHandler.aimv1(self, nearestEnemy, 20);
                            retval.assign(aim1);
                            defaultAction = false;
                            //fireTorpedoes(AttackHandler.aimv1(self, nearestEnemy, 20));
                            Tester.appendFile("firing torpedoes to " + Integer.toString(aim1), "testlog.txt");
                            retval.assign(StateTypes.ATTACK_STATE);
                            defaultAction = false;
                            AttackHandler.teleporterPrepped = false;
                        } else {
                            Tester.appendFile("default act: kejar", "testlog.txt");
                            defaultAction(aim0);
                        }
                    } else {
                        Tester.appendFile("else", "testlog.txt");

                        if (self.TorpedoSalvoCount > 0 && self.size > 50) {
                            aim1 = AttackHandler.aimv1(self, nearestEnemy, 20);
                            Tester.appendFile("firing torpedoes to " + Integer.toString(aim1), "testlog.txt");
                            fireTorpedoes(aim1);
                            AttackHandler.teleporterPrepped = false;
                            defaultAction = false;
                        }

                        
                    }
                }
            }

            
            

            if (defaultAction) {
                defaultAction(aim0);
            }
        }

        pathfind(retval.getHeading());
        return retval;
    }

    // sub
    public static void defaultAction(int enemyDirection) {
        AttackHandler.teleporterPrepped = false;
        Tester.appendFile("In pursuit", "testlog.txt");
        retval.assign(PlayerActions.FORWARD);
        retval.assign(enemyDirection);
    }

    public static void detectSupernova(){
        Tester.appendFile("Detecting supernova", "testlog.txt");

        List<GameObject> playerList;
        List<GameObject> supernova;
        
        playerList = gameState.getPlayerGameObjects().stream()
                .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                .collect(Collectors.toList());
        supernova = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVABOMB)
                .collect(Collectors.toList());

        if(!supernova.isEmpty()){
            for(int i = 1; i < playerList.size(); i++){
                if(Tools.getDistanceBetween(supernova.get(0), playerList.get(i)) < 300 && Tools.getDistanceBetween(supernova.get(0), self) + self.size > 300){
                    Tester.appendFile("Detonating supernova", "testlog.txt");
                    retval.assign(PlayerActions.DETONATESUPERNOVA);
                    AttackHandler.supernovaFired = false;
                    break;
                }
            }
        }
        else if(supernova.isEmpty()){
            AttackHandler.supernovaEmpty = true;
        }
    }


    public static void detectTeleporter(){
        Tester.appendFile("Detecting teleporter", "testlog.txt");

        List<GameObject> playerList;
        List<GameObject> teleporterList;
        
        playerList = gameState.getPlayerGameObjects().stream()
                .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                .collect(Collectors.toList());
        teleporterList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TELEPORTER)
                .collect(Collectors.toList());

        if(!teleporterList.isEmpty()){
            for(int i = 1; i < playerList.size(); i++){
                Tester.appendFile("Distance to player: " + Tools.getDistanceBetween(teleporterList.get(0), playerList.get(i)), "testlog.txt");
                if(Tools.getDistanceBetween(teleporterList.get(0), playerList.get(i)) < self.size + playerList.get(i).size && playerList.get(i).size < self.size){
                    Tester.appendFile("Teleporting", "testlog.txt");
                    retval.assign(PlayerActions.TELEPORT);
                    AttackHandler.teleporterFired = false;
                    break;
                }
            }
        }
        else if(teleporterList.isEmpty() && AttackHandler.teleporterdelay < 5){
            AttackHandler.teleporterEmpty = true;
        }
        else{
            AttackHandler.teleporterdelay++;
        }
    }
}