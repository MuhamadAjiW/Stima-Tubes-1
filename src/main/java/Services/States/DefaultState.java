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
import Services.Handlers.NavigationHandler;
import Services.Handlers.RadarHandler;

public class DefaultState extends StateBase{
    public static Response runState(){
        boolean defaultAction;
        List<GameObject> playerList;
        
        defaultAction = true;
        playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(self, item)))
                    .collect(Collectors.toList());

        if (!playerList.isEmpty()){
            if (RadarHandler.detectEnemy(playerList.get(1), self, Radarsize)){
                Tester.appendFile("Enemy within radar", "testlog.txt");
                if(RadarHandler.isBig(playerList.get(1), self.size.doubleValue() )){
                    Tester.appendFile("Enemy is big, size: " + playerList.get(1).size, "testlog.txt");
                    retval.assign(StateTypes.ESCAPE_STATE);
                    retval.assign(PlayerActions.FORWARD);
                    defaultAction = false;
                }
                else{
                    if(RadarHandler.isSmall(playerList.get(1), self.size.doubleValue() )){
                        Tester.appendFile("Enemy is small, size: " + playerList.get(1).size, "testlog.txt");
                        retval.assign(StateTypes.ATTACK_STATE);
                        retval.assign(PlayerActions.FORWARD);
                        defaultAction = false;
                    }
                    else{
                        Tester.appendFile("Enemy is uncertain, size: " + playerList.get(1).size, "testlog.txt");
                        //TODO: Gimana kalo nanggung sizenya?
                        defaultAction = true;
                    }
                }
            }

            if (self.TorpedoSalvoCount > 0 && self.size > 40){
                if(!NavigationHandler.outsideBound(gameState, self)){
                    Tester.appendFile("Ship is big, attacking nonetheless", "testlog.txt");
                    retval.assign(StateTypes.ATTACK_STATE);
                    fireTorpedoes(Tools.getHeadingBetween(playerList.get(1), self));
                    defaultAction = false;
                }
            }
             
        }

        if (defaultAction){
            List<GameObject> supernovas = gameState.getGameObjects()
                                            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVAPICKUP)
                                            .sorted(Comparator
                                                    .comparing(item -> Tools.getDistanceBetween(self, item)))
                                            .collect(Collectors.toList());

            Tester.appendFile("Supernova count: " + supernovas.size(), "testlog.txt");
            if(!supernovas.isEmpty()){
                if(Tools.getDistanceBetween(supernovas.get(0), self) < 300 && self.size > 80){
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


    public static void findSupernova(){
        // can kepikiran carana kumaha tapi klo range <50 pasti kesana, dodge objects, ignore foods
        
    }
}
