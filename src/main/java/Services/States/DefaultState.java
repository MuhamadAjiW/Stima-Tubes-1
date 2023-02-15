package Services.States;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Models.Position;
import Services.Common.Response;
import Services.Common.Tester;
import Services.Common.Tools;
import Services.Handlers.AttackHandler;
import Services.Handlers.NavigationHandler;
import Services.Handlers.RadarHandler;

public class DefaultState extends StateBase{
    public static Response runState(){
        boolean defaultAction;
        GameObject nearestEnemy;
        List<GameObject> playerList;
        
        defaultAction = true;
        playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        if (!playerList.isEmpty()){
            nearestEnemy = playerList.get(1);
            if (RadarHandler.detectEnemy(nearestEnemy, self, Radarsize)){
                Tester.appendFile("Enemy within radar", "testlog.txt");
                if(RadarHandler.isBig(nearestEnemy, self.size.doubleValue() )){
                    Tester.appendFile("Enemy is big, size: " + nearestEnemy.size, "testlog.txt");
                    retval.assign(StateTypes.ESCAPE_STATE);
                    retval.assign(PlayerActions.FORWARD);
                    defaultAction = false;
                    AttackHandler.teleporterPrepped = false;
                }
                else{
                    if(RadarHandler.isSmall(nearestEnemy, self.size.doubleValue() )){
                        Tester.appendFile("Enemy is small, size: " + nearestEnemy.size, "testlog.txt");
                        retval.assign(StateTypes.ATTACK_STATE);
                        retval.assign(PlayerActions.FORWARD);
                        defaultAction = false;
                        AttackHandler.teleporterPrepped = false;
                    }
                    else{
                        Tester.appendFile("Enemy is uncertain, size: " + nearestEnemy.size, "testlog.txt");
                        //TODO: Gimana kalo nanggung sizenya?
                        defaultAction = true;
                        AttackHandler.teleporterPrepped = false;
                    }
                }
            }

            if (self.size > 40){
                if(!NavigationHandler.outsideBound(gameState, self)){
                    Tester.appendFile("Ship is big, attacking nonetheless", "testlog.txt");
                    if(self.size - 30 > nearestEnemy.size && self.TeleporterCount > 0 && !AttackHandler.teleporterFired){
                        Tester.appendFile("Prepping teleporter", "testlog.txt");
                        retval.assign(PlayerActions.FORWARD);
                        retval.assign(StateTypes.ATTACK_STATE);
                        retval.assign(AttackHandler.aimv1(self, nearestEnemy, 20));
                        AttackHandler.teleporterPrepped = true;
                        defaultAction = false;
                    }
                    else if (self.TorpedoSalvoCount > 0){
                        retval.assign(StateTypes.ATTACK_STATE);
                        fireTorpedoes(AttackHandler.aimv1(self, nearestEnemy, 20));
                        defaultAction = false;
                        AttackHandler.teleporterPrepped = false;
                    }
                }
            }
            else if(self.SupernovaAvailable > 0){
                retval.assign(StateTypes.ATTACK_STATE);
                retval.assign(PlayerActions.FIRESUPERNOVA);
                retval.assign(AttackHandler.aimv1(self, nearestEnemy, 20));
                AttackHandler.supernovaFired = true;
                Tester.appendFile("firing supernova randomly", "testlog.txt");
                defaultAction = false;
            }
             
        }

        if (defaultAction){
            AttackHandler.teleporterPrepped = false;
            List<GameObject> supernovas = gameState.getGameObjects()
                                            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVAPICKUP)
                                            .sorted(Comparator
                                                    .comparing(item -> Tools.getDistanceBetween(self, item)))
                                            .collect(Collectors.toList());

            Tester.appendFile("Supernova count: " + supernovas.size(), "testlog.txt");
            if(!supernovas.isEmpty()){
                if((Tools.getDistanceBetween(supernovas.get(0), self) < 300 && self.size > 50) || (Tools.getDistanceBetween(supernovas.get(0), self) < 50)){
                    retval.assign(Tools.getHeadingBetween(supernovas.get(0), self));
                }

                else{
                    hoardingFood();
                }
            }

            else{
                hoardingFood();
            }
        }

        pathfind(retval.getHeading());
        return retval;
    }

    // Hoarding Food
    public static void hoardingFood(){
        Tester.appendFile("Hoarding food", "testlog.txt");
        // Initialize Values
        List<GameObject> foodList;
        double foodThreshold, sizeSelf;
        int newHeading;
        int xAwal, yAwal;

        sizeSelf = self.size.doubleValue();
        foodThreshold = 0.00;

        foodList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                .sorted(Comparator
                        .comparing(item -> Tools.getDistanceBetween(self, item)))
                .collect(Collectors.toList());

        // Make a cluster
        if(!foodList.isEmpty()){
            newHeading = Tools.getHeadingBetween(foodList.get(0), self);
            xAwal = (foodList.get(0).getPosition().x);
            yAwal = (foodList.get(0).getPosition().y);
            for (int i=1; i<foodList.size(); i++){
              if((Tools.getDistanceBetween(foodList.get(0), foodList.get(i))) <= (sizeSelf - foodThreshold)){
                xAwal = (foodList.get(i).getPosition().x + xAwal) / 2;
                yAwal = (foodList.get(i).getPosition().y + yAwal) / 2;
              } else {
                  break;
              }
          }

          Position tujuan = new Position(xAwal, yAwal);
          newHeading = Tools.getHeadingBetween(tujuan, self.getPosition());

          retval.assign(newHeading);
          retval.assign(StateTypes.DEFAULT_STATE);
        } 
        else{
            retval.assign(Tools.getHeadingBetween(gameState.world.getCenterPoint(), self.getPosition()));
        }
        retval.assign(PlayerActions.FORWARD);
    }
}
