package Services.States;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Enums.ObjectTypes;
import Enums.PlayerActions;
import Enums.StateTypes;
import Models.GameObject;
import Models.World;
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
                    defaultAction = false;
                }
                else{
                    if(RadarHandler.isSmall(playerList.get(1), self.size.doubleValue() )){
                        System.out.println("Enemy is small, size: " + playerList.get(1).size);
                        retval.assign(StateTypes.ATTACK_STATE);
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
                    defaultAction = false;
                }
            }
        }

        if (defaultAction){
            // hoardingFood();

            // if(detectEdge()){
            //     dodgeEdge();
            // }
        }

        pathfind(retval.getNewAction().heading);
        return retval;
    }

    // Hoarding Food
    public static void hoardingFood(boolean direction){
        System.out.println("Hoarding food");
        // Initialize Values
        List<GameObject> foodList;
        Double foodThreshold, sizeSelf, xTuj, yTuj, disFood;
        int newHeading, objFood;

        sizeSelf = self.size.doubleValue();
        foodThreshold = 0.00;
        newHeading = Tools.getHeadingBetween(foodList.get(0), self);

        foodList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                .sorted(Comparator
                        .comparing(item -> Tools.getDistanceBetween(self, item)))
                .collect(Collectors.toList());

        // Make a cluster
        for (int i=0; i<foodList.size(); i++){
            if((Tools.getDistanceBetween(foodList.get(0), foodList.get(i))) <= (sizeSelf - foodThreshold)){
                objFood = i;
                disFood = Tools.getDistanceBetween(foodList.get(0), foodList.get(i));
                xTuj = Tools.getXbyDistance(Tools.getHeadingBetween(foodList.get(objFood), foodList.get(0)), disFood / 2);
                yTuj = Tools.getYbyDistance(Tools.getHeadingBetween(foodList.get(objFood), foodList.get(0)), disFood / 2);
                newHeading = (Tools.toDegrees(Math.atan2(yTuj - self.getPosition().y,
                    xTuj - self.getPosition().x)) + 360) % 360;
            } else {
                break;
            }
        }
    
        retval.assign(PlayerActions.FORWARD);
        retval.assign(newHeading + 360);
        retval.assign(StateTypes.DEFAULT_STATE);
    }


    public static void findSupernova(){
        // can kepikiran carana kumaha tapi klo range <50 pasti kesana, dodge objects, ignore foods
        
    }

    //threshold kalo udah gede banget attack aja
    public static void pathfindDef(int currentHeading){
        if (NavigationHandler.outsideBound(gameState, self)){
            retval.assign(NavigationHandler.dodgeEdge(self, gameState));
        }
        else{
            if (RadarHandler.detectThreat(gameState, self, Radarsize - 70)){
                retval.assign(NavigationHandler.dodgeEnemy());
            }
            retval.assign(NavigationHandler.dodgeObjects(currentHeading, gameState, self));
            
        }
}
