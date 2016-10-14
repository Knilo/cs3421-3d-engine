package ass2.spec;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private float[] mySunlight;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        mySunlight = new float[3];
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * TO BE COMPLETED
     * 
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
    	double altitude = 0;
        // 3 corners of the triangle it is in
        int i,j;
        
        i = (int)Math.floor(x);
        j = (int)Math.ceil(z);
        double p0[] = {i,this.getGridAltitude(i, j) ,j};
   
        i = (int)Math.ceil(x);
        j = (int)Math.floor(z);
        double p1[] = {i,this.getGridAltitude(i, j) ,j};
             
        if (z - Math.floor(z) < 0.5) {       
        	i = (int)Math.floor(x);
            j = (int)Math.floor(z);
        } else {
        	i = (int)Math.ceil(x);
            j = (int)Math.ceil(z);           
        }
        
        double p2[] = {i,this.getGridAltitude(i, j) ,j};
        
        // Find the equation of the plane that passes through the 3 points.
        // Equation of a plane: a(x-x0) + b(y-y0) + c(z-z0) = 0
        // where (x0,y0,z0) is any point on the plane
        // and <a,b,c> is a vector perpendicular to the plane
        double n[] = getNormal(p2, p1, p0);
        
        // From this equation of a plane we can figure out the height by substituting in the non-integer coordinates.
        // y = (a(x0-x) + c(z0 - z))/b + y0
        altitude = (n[0]*(p0[0] - x) + n[2]*(p0[2] - z))/n[1] + p0[1];
     
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        myRoads.add(road);        
    }
    
    double [] getNormal(double[] p0, double[] p1, double[] p2){
    	double u[] = {p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]};
    	double v[] = {p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2]};
    	
    	return cross(u,v);
    	
    }
	
	double [] cross(double u [], double v[]){
    	double crossProduct[] = new double[3];
    	crossProduct[0] = u[1]*v[2] - u[2]*v[1];
    	crossProduct[1] = u[2]*v[0] - u[0]*v[2];
    	crossProduct[2] = u[0]*v[1] - u[1]*v[0];
    	//System.out.println("CP " + crossProduct[0] + " " +  crossProduct[1] + " " +  crossProduct[2]);
    	return crossProduct;
    }


}
