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
}
