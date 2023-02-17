package Services.Handlers;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;

import Enums.ObjectTypes;
import Models.GameObject;
import Services.Common.*;
import Models.GameState;
import Models.Position;

public class AttackHandler {
    // SUPERNOVA FLAGS
    public static boolean supernovaFired = false;
    public static boolean supernovaEmpty = false;

    // TELEPORT FLAGS
    public static boolean teleporterFired = false;
    public static boolean teleporterPrepped = false;
    public static boolean teleporterEmpty = false;
    public static int teleporterdelay = 0;

    // DISTANCE RANGE
    private static final double longRange = 150;
    private static final double midRange = 100;
    private static final double closeRange = 50;

    // SIZE RANGE
    private static final int bigSize = 60;
    private static final int smallSize = 10;

    // CONSIDERING SIZE
    public static double realDist(GameObject o1, GameObject o2) {
        return Tools.getDistanceBetween(o1, o2) - o1.getSize() - o2.getSize();
    }

    public static double realDist(Position p1, Position p2, int r1, int r2) {
        return Tools.getDistanceBetween(p1, p2) - r1 - r2;
    }

    public static int detAttckRange(GameObject o1, GameObject o2) {
        double realDist = realDist(o1, o2);
        if (realDist <= closeRange) {
            return 1;
        } else if (realDist <= midRange) {
            return 2;
        } else if (realDist <= longRange) {
            return 3;
        } else {
            return 4;
        }
    }

    public static int detSizeRange(GameObject o) {
        if (o.getSize() <= smallSize) {
            return 1;
        } else if (o.getSize() <= bigSize) {
            return 2;
        } else {
            return 3;
        }
    }

    // AIMING

    // Basic
    public static int aimv0(GameObject self, GameObject enemy) {
        // nembak langsung no prediction
        return Tools.getHeadingBetween(enemy, self);
    }

    // Predicted (naive)
    public static int aimv1(GameObject self, GameObject enemy, int attackSpeed) {
        // nembak dengan prediction kecepatan
        // target tembakan = titik pusat enemy
        // consider size? n jarak, krn keknya musuh cmn punya 1 strategi def -> cb
        // consider yg agak random?
        double a, b, c, k, alpha;
        int aimHeading1, aimHeading2, baseAim;

        a = enemy.getPosition().x - self.getPosition().x;
        b = enemy.getPosition().y - self.getPosition().y;
        c = enemy.getSpeed() / attackSpeed
                * (a * Math.sin(Math.toRadians(enemy.getHeading())) - b * Math.cos(Math.toRadians(enemy.getHeading())));
        k = Math.sqrt(a * a + b * b);
        alpha = Tools.valDeg(Math.atan2(a, -1 * b));
        baseAim = aimv0(self, enemy);
        aimHeading1 = (int) (Math.toDegrees(Math.acos(c / k)) + alpha);
        aimHeading2 = Tools.valDeg((int) (-1 * Math.toDegrees(Math.acos(c / k)) + alpha)); // inverse cos range [0, 180]

        if (Math.abs(baseAim - aimHeading1) < Math.abs(baseAim - aimHeading2)) {
            return aimHeading1;
        } else {
            return aimHeading2;
        }
    }

    // Apothema
    public static int aimv2(GameObject self, GameObject enemy) {
        double m1, m2, c1, c2;
        int xIn, yIn, tempHeading, SEHeading;
        int headingTH = 30;
        Position intercept = new Position();
        SEHeading = Tools.getHeadingBetween(enemy, self);
        if (Tools.kuadran(enemy.getPosition()) != -1) {
            m1 = enemy.getPosition().getY() / enemy.getPosition().getX();
            c1 = 0;
            m2 = -1 / m1;
            c2 = self.getPosition().getY() - m2 * self.getPosition().getX();
            double det = m1 - m2;
            if (det == 0) {
                tempHeading = -9999; // MARK
            } else {
                yIn = (int) ((c2 * m1 - c1 * m2) / det);
                xIn = (int) ((c2 - c1) / det);
                intercept.setX(xIn);
                intercept.setY(yIn);
            }
        } else {
            if (enemy.getPosition().getY() == 0) {
                intercept.setX(self.getPosition().getX());
                intercept.setY(0);
            } else {
                intercept.setX(0);
                intercept.setY(self.getPosition().getY());
            }
        }
        tempHeading = Tools.getHeadingBetween(intercept, self.getPosition());

        if (tempHeading != -9999 && Tools.absDegDelta(tempHeading, SEHeading) > headingTH) {
            tempHeading = -9999;
        }

        // TODO: ADD time checking (dsT * vT = dsE * vE) + tolerance

        return tempHeading;
    }

    // Aim pembelok
    public static int aimv3(GameObject self, GameObject enemy, GameObject danger) {
        double m1, m2, c1, c2;
        int xIn, yIn, delta, h0, h1, h2, hSD;
        double distTH = 30;
        boolean can = true;
        Position intercept = new Position();

        if (!Tools.isTegak(enemy.getHeading())) {
            m1 = Math.tan(enemy.getHeading());
            c1 = enemy.getPosition().getY() - m1 * enemy.getPosition().getX();
            m2 = -1 / m1;
            c2 = danger.getPosition().getY() - m2 * danger.getPosition().getX();
            double det = m1 - m2;

            if (det == 0) {
                can = false; // MARK
            } else {
                yIn = (int) ((c2 * m1 - c1 * m2) / det);
                xIn = (int) ((c2 - c1) / det);
                intercept.setX(xIn);
                intercept.setY(yIn);
            }
        } else {
            if (enemy.getHeading() == 0 || enemy.getHeading() == 180) {
                intercept.setX(danger.getPosition().getX());
                intercept.setY(enemy.getPosition().getY());
            } else {
                intercept.setX(enemy.getPosition().getX());
                intercept.setY(danger.getPosition().getY());
            }
        }
        
        can = can && realDist(intercept, danger.getPosition(), enemy.getSize(), danger.getSize()) <= distTH && Tools.absDegDelta(enemy.getHeading(), Tools.getHeadingBetween(enemy, danger)) <= 90;

        if (can) {
            h0 = Tools.getHeadingBetween(intercept, self.getPosition());
            delta = (int) Math
                    .toDegrees(Math.asin(enemy.getSize() / Tools.getDistanceBetween(intercept, self.getPosition())));
            h1 = (h0 + delta + 360) % 360;
            h2 = (h0 - delta + 360) % 360;
            hSD = Tools.getHeadingBetween(danger, self);
            if (Tools.absDegDelta(h1, hSD) > Tools.absDegDelta(h2, hSD)) {
                return h1;
            } else {
                return h2;
            }
            // TODO: ADD time checking (dsT * vT = dsE * vE) + tolerance
        } else {
            return -9999;
        }
    }

    // STRATEGIES
    // Aim to nearest food
    public static int attackFood(GameState gameState, GameObject self, GameObject enemy) {
        List<GameObject> foodList;
        // aim ke food terdekat musuh
        // syarat: range agak jauh (mid), biar musuh bisa tetep nyari makan tanpa
        // ngedetect kita mo nyerang, cuman masalahnya kalo gini ada chance gede musuh
        // udah pergi duluan
        foodList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                .sorted(Comparator
                        .comparing(item -> Tools.getDistanceBetween(enemy, item)))
                .collect(Collectors.toList());

        return aimv0(self, foodList.get(0));
    }
}