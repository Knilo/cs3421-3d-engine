package ass2.spec;

import java.awt.Dimension;
import java.text.DecimalFormat;
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
    private List<Enemy> myEnemies;
    private List<PortalPair> myPortalPairs;
    
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
        myEnemies = new ArrayList<Enemy>();
        myPortalPairs = new ArrayList<PortalPair>();
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
    
    public List<Enemy> enemies() {
        return myEnemies;
    }
    
    public List<PortalPair> portalPairs() {
        return myPortalPairs;
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
    	double altitude;
    	if (x == Math.floor(x) && z == Math.floor(z)) {
    		// x and z is an integer
    		altitude = this.getGridAltitude((int)x, (int)z);
    		

    	} else {
	        // 3 corners of the triangle it is in
	        int i,j;
	        
	        i = (int)Math.floor(x);
	        j = (int)Math.floor(z);
	        double p0[] = {i,this.getGridAltitude(i, j) ,j};
	        
	       
	        
	        i = (x == Math.floor(x)) ? (int)Math.ceil(x+1) : (int)Math.ceil(x);
	        j = (z == Math.floor(z)) ? (int)Math.ceil(z+1) : (int)Math.ceil(z);        
	        double p1[] = {i,this.getGridAltitude(i, j) ,j};
	        
	        
	        //Need to come out with a smarter function to determine the third point of the triangle in which the point is in
	        double s = x-Math.floor(x);
	        double t = z-Math.floor(z);
	        
	        if (t > s) {        
	            i = (int) Math.floor(x);
	        	j = (int) ((z == Math.floor(z)) ? z+1 : Math.ceil(z));
	        } else {
	        	i = (int) ((x == Math.floor(x)) ? x+1 : Math.ceil(x));
	            j = (int) Math.floor(z); 
	        }
     
	        double p2[] = {i,this.getGridAltitude(i, j) ,j};
	        //System.out.println("In triangle: (" +p0[0]+"," +p0[1]+"," +p0[2]+") ("+p1[0]+","+p1[1]+"," +p1[2]+") ("+p2[0]+","+p2[1]+"," +p2[2]+")");
	        
	        // Find the equation of the plane that passes through the 3 points.
	        // Equation of a plane: a(x-x0) + b(y-y0) + c(z-z0) = 0
	        // where (x0,y0,z0) is any point on the plane
	        // and <a,b,c> is a vector perpendicular to the plane
	        double n[] = getNormal(p0, p1, p2);
	        
	        //System.out.println("normal: " + n[0] + "," + n[1] + ","+ n[2]);
	        //System.out.println("point: (" +p0[0]+"," +p0[1]+"," +p0[2]+")");
	        //System.out.println("n[0]*(x - p0[0]): "+ n[0]*(x - p0[0]));
	        //System.out.println("n[2]*(z - p0[2]): " + n[2]*(z - p0[2]));
	        //System.out.println("n[1]: " + n[1] + " p0[1]: " + p0[1]);
	        // From this equation of a plane we can figure out the height by substituting in the non-integer coordinates.
	        // y = -(a(x - x0) + c(z - z0))/b + y0
	        
	        
	        altitude = -(n[0]*(x - p0[0]) + n[2]*(z - p0[2]))/n[1] + p0[1];
    	}
        return altitude;
    }
    
    public void addPortalPair (double orangeX, double orangeZ, double blueX, double blueZ) {
        double orangeY = altitude (orangeX, orangeZ);
        double blueY   = altitude (blueX  , blueZ);
        
        double[] orangePortalPos = {orangeX, orangeY, orangeZ};
        double[] bluePortalPos   = {blueX  , blueY  , blueZ};
        
        //to do: create portalpair object
        PortalPair pp = new PortalPair(orangePortalPos, bluePortalPos);
        myPortalPairs.add(pp);
        
    }
    
    public void addEnemy (double x, double z) {
        double y = altitude(x, z);
        Enemy enemy = new Enemy(x, y, z);
        myEnemies.add(enemy);
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
