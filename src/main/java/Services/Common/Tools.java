package Services.Common;

import Models.GameObject;
import Models.Position;

public class Tools {
    public static double getDistanceBetween(GameObject object1, GameObject object2) {
        double triangleX;
        double triangleY;
        triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    public static double getDistanceBetween(Position pos1, Position pos2){
        double triangleX;
        double triangleY;
        triangleX = Math.abs(pos1.x -pos2.x);
        triangleY = Math.abs(pos1.y - pos2.y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    public static int getHeadingBetween(GameObject otherObject, GameObject bot) {
        int direction;
        direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    public static int getHeadingBetween(Position pos1, Position pos2) {
        int direction;
        direction = toDegrees(Math.atan2(pos1.y - pos2.y,
                pos1.x - pos2.x));
        return (direction + 360) % 360;
    }

    public static int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    public static boolean aroundDegrees(int currentDegree, int targetDegree, int range){
        if (targetDegree + range > 360){
            return (currentDegree < targetDegree + range - 360 && currentDegree > targetDegree - range);
        }
        else if (targetDegree - range < 0){
            return (currentDegree < targetDegree + range && currentDegree > targetDegree - range + 360);
        }
        else{
            return (currentDegree < targetDegree + range && currentDegree > targetDegree - range);
        }
    }  

    public static Double getXbyDistance(int heading, Double distance, GameObject obj) {
        return ((Math.cos(heading) * distance) + obj.getPosition().x);
    }

    public static Double getYbyDistance(int heading, Double distance, GameObject obj) {
        return ((Math.sin(heading) * distance) + obj.getPosition().y);
    }
    
    public static int valDeg(double rad) {
        while (rad < 0) {
            rad += 2 * Math.PI;
        }
        while (rad > 2 * Math.PI) {
            rad -= 2 * Math.PI;
        }
        return toDegrees(rad);
    }

    public static int valDeg(int deg) {
        while (deg < 0) {
            deg += 360;
        }
        return deg % 360;
    }

    public static int kuadran(Position p) {
        if (p.getX() > 0 && p.getY() > 0) {
            return 1;
        } else if (p.getX() < 0 && p.getY() > 0) {
            return 2;
        } else if (p.getX() < 0 && p.getY() < 0) {
            return 3;
        } else if (p.getX() > 0 && p.getY() < 0) {
            return 4;
        } else {
            return -1; // di pusat/sumbu
        }
    }

    public static int absDegDelta(int deg1, int deg2) {
        int delta = Math.abs(deg1 - deg2);
        if (delta > 180) {
            if (deg1 > deg2) {
                deg2 += 360;
            } else {
                deg1 += 360;
            }
            delta = Math.abs(deg1 - deg2);
        }
        return delta;
    }

    public static boolean isTegak(int deg) {
        return deg == 0 || deg == 90 || deg == 180 || deg == 360;
    }

    public static int tandaTorsi(GameObject self, GameObject enemy) {
        // -1 = cw, 0 = ke pusat, 1: ccw
        int hbReversed = getHeadingBetween(self, enemy);
        int hplus = valDeg(hbReversed + 90);
        int hmin = valDeg(hbReversed - 90);
        if (absDegDelta(hplus, hbReversed) < absDegDelta(hmin, hbReversed)) { // cw
            return -1;
        } else if (absDegDelta(hplus, hbReversed) == absDegDelta(hmin, hbReversed)) { // ke pusat
            return 0;
        } else {
            return 1;
        }
    }
}
