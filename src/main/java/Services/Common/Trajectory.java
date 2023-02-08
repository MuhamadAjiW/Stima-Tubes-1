package Services.Common;

import Models.GameObject;
import Models.Position;

public class Trajectory {
    public long gradient;
    public long constant;
    public long vel;
    public double vy;
    public double vx;
    public int tetha;
    public Position init = new Position();

    public Trajectory(GameObject object){
        tetha = object.currentHeading;
        vel = object.getSpeed();
        vx = Math.cos(Math.toRadians(tetha))*vel;
        vy = Math.sin(Math.toRadians(tetha))*vel;
        init.x = object.getPosition().x;
        init.y = object.getPosition().y;
        gradient = Math.round(Math.sin(Math.toRadians(tetha)) * vel);
        constant = init.y - (gradient*init.x);
    }

    public Trajectory(long gradient, long constant){
        this.gradient = gradient;
        this.constant = constant;
        init.x = 0;
        init.y = 0;
        vel = 0;
        vx = 0;
        vy = 0;
    }

    public Position interpolate(int time){
        Position retval = new Position();
        retval.x = (int) Math.round(vx*time + init.x);
        retval.y = (int) Math.round(vy*time + init.y);
        return retval;
    }
}
