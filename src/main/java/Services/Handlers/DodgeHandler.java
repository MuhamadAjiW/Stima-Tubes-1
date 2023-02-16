package Services.Handlers;

import java.util.List;

import Models.GameObject;
import Models.Position;
import Services.Common.Tester;
import Services.Common.Tools;
import Services.Common.Trajectory;

public class DodgeHandler {
    public static boolean dodging = false;
    public static boolean critical = false;
    public static boolean hit = false;

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
        //1 - m1 | (y) = c1
        //1 - m2 | (x) = c2
        //1/det
        //-m2 m1
        //-1   1

        long determinant = (1*(-1*m2)) - (1*(-1*m1));
        if (determinant == 0){
            retval.x = -9999;
            retval.y = -9999;
        }
        else{
            retval.y = (int) ((c1*((-1)*m2) + c2*(m1)) / determinant);
            retval.x = (int) ((c1*(-1) + c2*1) / determinant);
        }
        return retval;
    }

    public static int timeToIntercept(Trajectory line1,Trajectory line2){
        int retval;

        retval = 0;
        double targetDst = (Tools.getDistanceBetween(line1.init, line2.init));
        double cachedDst = 0;
        do{
            retval++;
            cachedDst = targetDst;
            targetDst = Tools.getDistanceBetween(line1.interpolate(retval), line2.interpolate(retval));
    
        } while (cachedDst > targetDst);
        if (retval < 2){
            retval = -9999;
        }

        return retval - 1;
    }

    public static double closestDistance(Trajectory line1, Trajectory line2){
        int time;
        time = timeToIntercept(line1, line2);

        return distanceAfterTime(line1, line2, time);

    }

    public static double distanceAfterTime(Trajectory line1, Trajectory line2, int time){
        double retval;
        Position interpolatedPosition1;
        Position interpolatedPosition2;

        retval = 0;

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
        int time;
        int cluster;
        int i;

        hit = false;
        critical = false;
        i = 0;
        botTrajectory = new Trajectory(bot);
        while(!hit && i < torpedoList.size()){
            torpedoTrajectory = new Trajectory(torpedoList.get(i));

            time = timeToIntercept(botTrajectory, torpedoTrajectory);
            closestDistance = distanceAfterTime(botTrajectory, torpedoTrajectory, time);
            
            Tester.appendFile("Closest interpolated distance to torpedo: " + closestDistance, "testlog.txt");
            if(closestDistance != -9999){
                if(closestDistance < bot.size + torpedoList.get(i).size - 5){
                    
                    if (time < ((bot.size+torpedoList.get(i).size)/torpedoTrajectory.vel) + 3  && time > 1){
                        cluster = 0;
                        for(int j = 0; j < torpedoList.size(); j++){
                            if (Tools.getDistanceBetween(torpedoList.get(i), torpedoList.get(j)) < 60){
                                Tester.appendFile("Distance with other torpedo: " + Tools.getDistanceBetween(torpedoList.get(i), torpedoList.get(j)), "testlog.txt");
                                cluster++;
                            }
                        }

                        if(cluster > 2){
                            Tester.appendFile("Critical!", "testlog.txt");
                            critical = true;
                        }
                        
                    }

                    Tester.appendFile("Hit!", "testlog.txt");
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
