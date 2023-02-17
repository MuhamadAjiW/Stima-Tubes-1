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
    public static final int tpdSizeTH = 30;

    public static Response runState() {
        boolean defaultAction;
        int fixAim, nEnemyTorsi;
        GameObject nearestEnemy, nearestGasCloud;
        List<GameObject> playerList, gasList, teleporterList, supernovaList;

        Tester.appendFile("TeleporerFired: " + AttackHandler.teleporterFired, "testlog.txt");
        Tester.appendFile("TeleporerPrepped: " + AttackHandler.teleporterPrepped, "testlog.txt");
        Tester.appendFile("TeleporerEmpty: " + AttackHandler.teleporterEmpty, "testlog.txt");

        defaultAction = true;
        playerList = gameState.getPlayerGameObjects().stream()
                .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                .collect(Collectors.toList());

        nearestEnemy = playerList.get(1);
        Effect enemyEffect = new Effect(nearestEnemy.Effects);
        nEnemyTorsi = Tools.tandaTorsi(self, nearestEnemy);

        if (!playerList.isEmpty()) {
            gasList = gameState.getGameObjects()
                        .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD)
                        .sorted(Comparator
                        .comparing(item -> Tools.getDistanceBetween(playerList.get(0), item)))
                        .collect(Collectors.toList());
            
            if (!gasList.isEmpty()){
                nearestGasCloud = gasList.get(0);
            } else{
                nearestGasCloud = nearestEnemy;
            }

            // TELEPORTER & SUPERNOVA FLAG SETTINGS
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

            // ATTACKING
            // FIRING TELEPORTER
            if (AttackHandler.teleporterPrepped) {
                Tester.appendFile("Firing teleporter", "testlog.txt");
                retval.assign(StateTypes.ATTACK_STATE);
                retval.assign(PlayerActions.FIRETELEPORT);
                AttackHandler.teleporterdelay = 0;
                AttackHandler.teleporterFired = true;
                AttackHandler.teleporterPrepped = false;
                defaultAction = false;
            }
            // BIG SELF SIZE
            if (AttackHandler.detSizeRange(self) == 3) {
                Tester.appendFile("Big self size", "testlog.txt");
                // PREP TELEPORT
                if (self.size - 30 > nearestEnemy.size && self.TeleporterCount > 0 && !AttackHandler.teleporterFired) {
                    Tester.appendFile("Prepping teleporter", "testlog.txt");
                    fixAim = AttackHandler.aimv1(self, nearestEnemy, 20);
                    retval.assign(StateTypes.ATTACK_STATE);
                    retval.assign(PlayerActions.FORWARD);
                    retval.assign(fixAim);
                    AttackHandler.teleporterdelay = 0;
                    AttackHandler.teleporterPrepped = true;
                    defaultAction = false;
                // FIRE TPD
                } else if (self.TorpedoSalvoCount > 0 && self.size > tpdSizeTH) {
                    fixAim = AttackHandler.aimv1(self, nearestEnemy, 20) + nEnemyTorsi * 4;
                    Tester.appendFile("firing torpedoes to " +  Integer.toString(fixAim), "testlog.txt");
                    fireTorpedoes(fixAim);
                    retval.assign(StateTypes.ATTACK_STATE);
                    defaultAction = false;
                    AttackHandler.teleporterPrepped = false;
                // GOTO DEFAULT
                } else {
                    retval.assign(StateTypes.DEFAULT_STATE);
                    retval.assign(PlayerActions.FORWARD);
                }
            } else {
                // ENEMY JAUH
                if (AttackHandler.detAttckRange(self, nearestEnemy) == 4) {
                    retval.assign(StateTypes.DEFAULT_STATE);
                    retval.assign(PlayerActions.FORWARD);
                // ADA SUPERNOVA
                } else if (self.SupernovaAvailable > 0) { // TODO : + check world.radius > 1.5 supernova size
                    Tester.appendFile("finding supernova target", "testlog.txt");

                    for (int i = 1; i < playerList.size(); i++) {
                        nearestEnemy = playerList.get(i);
                        if (AttackHandler.detAttckRange(self, nearestEnemy) >= 2) {
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
                // ENEMY TERLALU KECIL
                } else if (AttackHandler.detSizeRange(nearestEnemy) == 1) {
                    Tester.appendFile("Very small enemy", "testlog.txt");
                    // PREP TELEPORT
                    if (self.size - 30 > nearestEnemy.size && self.TeleporterCount > 0 && !AttackHandler.teleporterFired) {
                        Tester.appendFile("Prepping teleporter", "testlog.txt");
                        fixAim = AttackHandler.aimv1(self, nearestEnemy, 20);
                        retval.assign(StateTypes.ATTACK_STATE);
                        retval.assign(PlayerActions.FORWARD);
                        retval.assign(fixAim);
                        AttackHandler.teleporterdelay = 0;
                        AttackHandler.teleporterPrepped = true;
                        defaultAction = false;
                    // FIRE TPD // TODO: check apakah aimnya efektif utk yg kecil
                    } else if (self.TorpedoSalvoCount > 0 && self.size > tpdSizeTH) {
                        fixAim = AttackHandler.aimv1(self, nearestEnemy, 20) + nEnemyTorsi * 4;
                        Tester.appendFile("firing torpedoes to " +  Integer.toString(fixAim), "testlog.txt");
                        fireTorpedoes(fixAim);
                        retval.assign(StateTypes.ATTACK_STATE);
                        defaultAction = false;
                        AttackHandler.teleporterPrepped = false;
                    // GOTO DEFAULT
                    } else {
                        retval.assign(StateTypes.DEFAULT_STATE);
                        retval.assign(PlayerActions.FORWARD);
                    }
                // ENEMY IS BIGGER
                } else if (RadarHandler.isBig(nearestEnemy, self.size.doubleValue() + 10)) {
                    // FIRE TPD
                    if (AttackHandler.detAttckRange(self, nearestEnemy) >= 3) {
                        if (self.TorpedoSalvoCount > 0 && self.size > tpdSizeTH) {
                            fixAim = AttackHandler.aimv1(self, nearestEnemy, 20) + nEnemyTorsi * 4;
                            Tester.appendFile("firing torpedoes to " +  Integer.toString(fixAim), "testlog.txt");
                            fireTorpedoes(fixAim);
                            retval.assign(StateTypes.ATTACK_STATE);
                            defaultAction = false;
                            AttackHandler.teleporterPrepped = false;
                        } else {
                            retval.assign(StateTypes.DEFAULT_STATE);
                            retval.assign(PlayerActions.FORWARD);
                        }
                    // ESCAPE
                    } else {
                        Tester.appendFile("bigger enemy", "testlog.txt");
                        retval.assign(StateTypes.ESCAPE_STATE);
                        retval.assign(PlayerActions.FORWARD);
                        defaultAction = false;
                        AttackHandler.teleporterPrepped = false;
                    }
                // ENEMY SMALLER
                } else  if (RadarHandler.isSmall(nearestEnemy, self.size.doubleValue())) {
                    if (self.size - 30 > nearestEnemy.size && self.TeleporterCount > 0 && !AttackHandler.teleporterFired) {
                        Tester.appendFile("Prepping teleporter", "testlog.txt");
                        fixAim = AttackHandler.aimv1(self, nearestEnemy, 20) ;
                        retval.assign(StateTypes.ATTACK_STATE);
                        retval.assign(PlayerActions.FORWARD);
                        retval.assign(fixAim);
                        AttackHandler.teleporterdelay = 0;
                        AttackHandler.teleporterPrepped = true;
                        defaultAction = false;
                    } else {
                        switch (AttackHandler.detAttckRange(self, nearestEnemy)){
                            case 1:
                                Tester.appendFile("enemy range level 3", "testlog.txt");
                                if (self.TorpedoSalvoCount > 0  && self.size > tpdSizeTH  && !enemyEffect.isShield()) {
                                    retval.assign(StateTypes.ATTACK_STATE);
                                    retval.assign(PlayerActions.FIRETORPEDOES);
                                    fixAim = AttackHandler.aimv1(self, nearestEnemy, 20) + nEnemyTorsi * 4;
                                    retval.assign(fixAim);
                                    defaultAction = false;
                                    Tester.appendFile("firing torpedoes to " + Integer.toString(fixAim), "testlog.txt");
                                    retval.assign(StateTypes.ATTACK_STATE);
                                    defaultAction = false;
                                    AttackHandler.teleporterPrepped = false;
                                } else {
                                    Tester.appendFile("default act: kejar", "testlog.txt");
                                    fixAim = AttackHandler.aimv0(self, nearestEnemy);
                                    defaultAction(fixAim);
                                }
                                break;
                            case 2:
                                if (self.TorpedoSalvoCount > 0 && self.size > tpdSizeTH) {
                                    if (NavigationHandler.outsideBound(gameState, nearestEnemy) && AttackHandler.aimv2(self,nearestEnemy) != -9999) {
                                        Tester.appendFile("enemy is near outer ring!! lesgow", "testlog.txt");
                                        fixAim = AttackHandler.aimv2(self,nearestEnemy);
                                    } else if (AttackHandler.aimv3(self,nearestEnemy, nearestGasCloud) != -9999) {
                                        Tester.appendFile("enemy is near gas cloud", "testlog.txt");
                                        fixAim = AttackHandler.aimv3(self,nearestEnemy, nearestGasCloud);
                                    } else {
                                        fixAim = AttackHandler.aimv1(self,nearestEnemy,20) + nEnemyTorsi * 4;
                                        
                                    }
                                    Tester.appendFile("firing torpedoes to aim1: " + Integer.toString(fixAim), "testlog.txt");
                                    retval.assign(fixAim);
                                    retval.assign(StateTypes.ATTACK_STATE);
                                    retval.assign(PlayerActions.FIRETORPEDOES);
                                    defaultAction = false;
                                    AttackHandler.teleporterPrepped = false;
                                } else {
                                    retval.assign(StateTypes.DEFAULT_STATE);
                                    retval.assign(PlayerActions.FORWARD);
                                    }
                                break;
                            case 3:
                                if (self.TorpedoSalvoCount > 0 && self.size > tpdSizeTH) {
                                    fixAim = AttackHandler.aimv1(self,nearestEnemy,20) + nEnemyTorsi * 4;
                                    retval.assign(fixAim);
                                    Tester.appendFile("firing torpedoes to aim1: " + Integer.toString(fixAim), "testlog.txt");
                                    retval.assign(StateTypes.ATTACK_STATE);
                                    retval.assign(PlayerActions.FIRETORPEDOES);
                                    defaultAction = false;
                                    AttackHandler.teleporterPrepped = false;
                                } else {
                                    retval.assign(StateTypes.DEFAULT_STATE);
                                    retval.assign(PlayerActions.FORWARD);
                                    }
                                break;
                            default:
                                retval.assign(StateTypes.DEFAULT_STATE);
                                retval.assign(PlayerActions.FORWARD);
                                
                        }
                    }
                } else { // size nanggung
                    if (AttackHandler.detAttckRange(self, nearestEnemy) <= 2 && self.TorpedoSalvoCount > 0 && self.size > tpdSizeTH && !enemyEffect.isShield()) {
                        fixAim = AttackHandler.aimv1(self, nearestEnemy, 20) + nEnemyTorsi * 4;
                        Tester.appendFile("firing torpedoes to " + Integer.toString(fixAim), "testlog.txt");
                        fireTorpedoes(fixAim);
                        retval.assign(StateTypes.ATTACK_STATE);
                        defaultAction = false;
                        AttackHandler.teleporterPrepped = false;
                    } else {
                        retval.assign(StateTypes.DEFAULT_STATE);
                        retval.assign(PlayerActions.FORWARD);
                    }
                }
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