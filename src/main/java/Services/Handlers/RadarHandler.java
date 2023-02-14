package Services.Handlers;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Models.GameObject;
import Models.GameState;
import Models.Position;
import Services.Common.Tools;

public class RadarHandler {
    public static boolean detectEnemy(GameObject otherObject, GameObject bot, Double size){
        boolean detected;
        detected = false;
        if(size + bot.getSize() + otherObject.getSize() > Tools.getDistanceBetween(otherObject, bot)){
            detected = true;
        }
        return detected;
    }

    public static boolean detectThreat(GameState gameState, GameObject bot, Double size){
        boolean detected;
        List<GameObject> playerList;

        playerList = gameState.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

        detected = false;
        if(size + bot.getSize() + playerList.get(1).getSize() > Tools.getDistanceBetween(playerList.get(1), bot)){
            if(isBig(playerList.get(1), bot.size.doubleValue() )){
                detected = true;
            }
        }
        return detected;
    }
    
    public static Position getPositionAfter(GameObject o, int tick) {
        int x = (int) (o.getPosition().x + tick * o.getSpeed() * Math.cos(Math.toRadians(o.getHeading())));
        int y = (int) (o.getPosition().y + tick * o.getSpeed() * Math.sin(Math.toRadians(o.getHeading())));
        return new Position(x,y);
    }

    public static boolean isOTWCollide(GameObject enemy, GameObject self, Position surpPos) {
        Position toBe = getPositionAfter(enemy, 1);
        double jP = Tools.getDistanceBetween(toBe, surpPos);
        double bA = enemy.getSize() + self.getSize();
        return jP < bA;
    }

    public static GameObject findNearEnemy(GameState gs, Position p, GameObject self) {
        List<GameObject> playerList;
        playerList = gs.getPlayerGameObjects().stream()
                    .sorted(Comparator.comparing(item -> Tools.getDistanceBetween(p, item.getPosition())))
                    .collect(Collectors.toList());
        if (playerList.size() > 1) {
            if (playerList.get(0).getId() == self.getId()) {
                return playerList.get(1);
            } else {
                return playerList.get(0);
            }
        } else {
            return self;
        }
    }

    public static boolean isSmall(GameObject otherObject, Double threshold){
        boolean small;
        small = false;
        if(otherObject.size < threshold){
            small = true;
        }
        return small;
    }

    public static boolean isBig(GameObject otherObject, Double threshold){
        boolean big;
        big = false;
        if(otherObject.size > threshold){
            big = true;
        }
        return big;
    }

    public static boolean detectEdge(GameObject bot, Double threshold, GameState gameState){
        boolean edgeDetected;
        int worldRad;
        double sizeSelf, xBoundary, yBoundary;

        edgeDetected = false;
        worldRad = gameState.world.getRadius();
        sizeSelf = bot.size.doubleValue();
        xBoundary = Tools.getXbyDistance(bot.currentHeading, sizeSelf + threshold, bot);
        yBoundary = Tools.getYbyDistance(bot.currentHeading, sizeSelf + threshold, bot);

        if ((xBoundary * xBoundary) + ((yBoundary * yBoundary)) <= worldRad){
            edgeDetected = true;
        }
        
        return edgeDetected;
    }
}
