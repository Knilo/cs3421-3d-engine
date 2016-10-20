package ass2.spec;

import com.jogamp.opengl.GL2;

public class Enemy {
    private double[] myPos;
    
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
