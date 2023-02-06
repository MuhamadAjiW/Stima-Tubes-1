package Services;

import Models.GameObject;

public class Tools {
    public static double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    public static int getHeadingBetween(GameObject otherObject, GameObject bot) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    public static boolean detectEnemy(GameObject otherObject, GameObject bot, Double size){
        boolean detected = false;
        if(size + bot.getSize() + otherObject.getSize() > getDistanceBetween(otherObject, bot)){
            detected = true;
        }
        return detected;
    }

    public static boolean isSmall(GameObject otherObject, Double threshold){
        boolean small = false;
        if(otherObject.size < threshold){
            small = true;
        }
        return small;
    }

    public static boolean isBig(GameObject otherObject, Double threshold){
        boolean small = false;
        if(otherObject.size > threshold){
            small = true;
        }
        return small;
    }

    public static int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }
}
