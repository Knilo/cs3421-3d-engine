package ass2.spec;

/**
 * Enemy class simply stores location of an enemy
 * @author Weilon
 *
 */
public class Enemy {
    private double[] myPos;
    
    //initialise enemy positions
    public Enemy(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
    }
    
    public double[] getPosition() {
        return myPos;
    }
    
}
