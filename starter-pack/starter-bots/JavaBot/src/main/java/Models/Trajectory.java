package Models;

public class Trajectory {
    public long gradient;
    public long constant;
    public long vel;
    public int tetha;
    public Position init = new Position();

    public Trajectory(GameObject object){
        tetha = object.currentHeading;
        vel = object.getSpeed();
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
    }

    public Position interpolate(int time){
        Position retval = new Position();
        retval.x = (int) (Math.round(Math.cos(Math.toRadians(tetha))*vel)*time + init.x);
        retval.y = (int) (Math.round(Math.sin(Math.toRadians(tetha))*vel)*time + init.y);
        return retval;
    }
}
