package Services.GenericClass;

import java.util.List;

import Models.GameObject;
import Models.Position;
import Models.Trajectory;
import Services.Tools;

public class DodgeHandler {
    public static Position intercept(Trajectory line1,Trajectory line2){
        Position retval = new Position();

        long m1 = line1.gradient;
        long m2 = line2.gradient;
        long c1 = line1.constant;
        long c2 = line2.constant;

        //y = m1x + c1
        //y = m2x + c2
        //y - m1x = c1
        //y - m2x = c2
        //1 - m1 (y) = c1
        //1 - m2 (x) = c2
        //1/det
        //-m2 m1
        //-1   1

        long determinant = (1*(-1*m2)) - (1*(-1*m1));
        if (determinant == 0){
            retval.x = -9999;
            retval.y = -9999;
        }
        else{
            retval.y = (int) (determinant * (c1*((-1)*m2) + c2*(m1)));
            retval.x = (int) (determinant * (c1*(-1) + c2*1));
        }
        return retval;
    }

    public static int timeToIntercept(Trajectory line1,Trajectory line2){
        int retval;
        Position target;

        retval = 0;
        target = intercept(line1, line2);
        if (target.x == -9999 && target.y == -9999){
            retval = -9999;
        }
        else{
            double targetDst = Math.min(Tools.getDistanceBetween(line1.init, target), Tools.getDistanceBetween(line2.init, target));
            double cachedDst = 0;

            if (!(targetDst < 1 && targetDst > -1)){
                do{
                    retval++;
                    cachedDst = targetDst;
                    targetDst = Tools.getDistanceBetween(line1.interpolate(retval), line2.interpolate(retval));
        
                } while (cachedDst > targetDst && (targetDst < 1 && targetDst > -1));
            }
            if (retval == 0){
                retval = -9999;
            }
        }

        return retval;
    }

    public static double closestDistance(Trajectory line1, Trajectory line2){
        double retval;
        int time;
        Position interpolatedPosition1;
        Position interpolatedPosition2;

        retval = 0;
        time = timeToIntercept(line1, line2);

        if (time == -9999){
            retval = -9999;
        }
        else{
            interpolatedPosition1 = line1.interpolate(time);
            interpolatedPosition2 = line2.interpolate(time);
            retval = Tools.getDistanceBetween(interpolatedPosition1, interpolatedPosition2);
        }

        return retval;
    }

    public static boolean inTrajectory(GameObject bot, List<GameObject> torpedoList){
        double closestDistance;
        Trajectory botTrajectory;
        Trajectory torpedoTrajectory;
        boolean hit;
        int i;

        hit = false;
        i = 0;
        botTrajectory = new Trajectory(bot);
        while(!hit && i < torpedoList.size()){
            torpedoTrajectory = new Trajectory(torpedoList.get(i));
            closestDistance = closestDistance(botTrajectory, torpedoTrajectory);
            System.out.println("Closest interpolated distance to torpedo: " + closestDistance);
            if(closestDistance != -9999){
                if(closestDistance < bot.size + torpedoList.get(i).size + 5){
                    hit = true;
                }
                else{
                    i++;
                }
            }
            else{
                i++;
            }
        }
        return hit;
    }
}
