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
                System.out.println("Enemy within radar");
                if(RadarHandler.isBig(playerList.get(1), self.size.doubleValue() )){
                    System.out.println("Enemy is big, size: " + playerList.get(1).size);
                    retval.assign(StateTypes.ESCAPE_STATE);
                    retval.assign(PlayerActions.STOP);
                    defaultAction = false;
                }
                else{
                    if(RadarHandler.isSmall(playerList.get(1), self.size.doubleValue() )){
                        System.out.println("Enemy is small, size: " + playerList.get(1).size);
                        retval.assign(StateTypes.ATTACK_STATE);
                        retval.assign(PlayerActions.STOP);
                        defaultAction = false;
                    }
                    else{
                        System.out.println("Enemy is uncertain, size: " + playerList.get(1).size);
                        //TODO: Gimana kalo nanggung sizenya?
                        defaultAction = true;
                    }
                }
            }
            else if (self.TorpedoSalvoCount > 0 && self.size > 100){
                if(!NavigationHandler.outsideBound(gameState, self)){
                    System.out.println("Ship is big, firing missles nonetheless");
                    retval.assign(StateTypes.ATTACK_STATE);
                    retval.assign(PlayerActions.STOP);
                    defaultAction = false;
                }
            }
        }

        if (defaultAction){
            hoardingFood();
        }

        pathfindDef(retval.getHeading());
        return retval;
    }

    // Hoarding Food
    public static void hoardingFood(){
        System.out.println("Hoarding food");
        // Initialize Values
        List<GameObject> foodList;
        double foodThreshold, sizeSelf;
        int newHeading;
        double xAwal, yAwal;

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
            xAwal = (double) (foodList.get(0).getPosition().x);
            yAwal = (double) (foodList.get(0).getPosition().y);
            for (int i=1; i<foodList.size(); i++){
              if((Tools.getDistanceBetween(foodList.get(0), foodList.get(i))) <= (sizeSelf - foodThreshold)){
                xAwal = (foodList.get(i).getPosition().x + (double) xAwal) / 2;
                yAwal = (foodList.get(i).getPosition().y + (double) yAwal) / 2;
              } else {
                  break;
              }
          }

          Position tujuan = new Position((int) xAwal, (int) yAwal);
          newHeading = Tools.getHeadingBetween(tujuan, self.getPosition());

          retval.assign(newHeading);
          retval.assign(StateTypes.DEFAULT_STATE);
        } 
        else{
            retval.assign(Tools.getHeadingBetween(self.getPosition(), gameState.world.getCenterPoint()));
        }
        retval.assign(PlayerActions.FORWARD);
    }


    public static void findSupernova(){
        // can kepikiran carana kumaha tapi klo range <50 pasti kesana, dodge objects, ignore foods
        
    }

    //threshold kalo udah gede banget attack aja
    public static void pathfindDef(int currentHeading){
        List<GameObject> objectList;
        objectList = gameState.getGameObjects()
        .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GAS_CLOUD || item.getGameObjectType() == ObjectTypes.WORMHOLE)
        .sorted(Comparator
                .comparing(item -> Tools.getDistanceBetween(self, item)))
        .collect(Collectors.toList());;

        if (NavigationHandler.outsideBound(gameState, self)){
            retval.assign(NavigationHandler.dodgeEdge(self, gameState));
        }
        else{
            if (RadarHandler.detectThreat(gameState, self, Radarsize - 70)){
                retval.assign(NavigationHandler.dodgeEnemy());
            }
            retval.assign(NavigationHandler.dodgeObjects(currentHeading, gameState, self, objectList));
            //retval.assign(NavigationHandler.dodgeObjects(currentHeading, gameState, self));
            
        }
    }
}
