package ass2.spec;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener, KeyListener {
    
    private Terrain myTerrain;
    private LevelTexture[] myTextures;
    private GLUT glut;
    private GLU glu;
    private static int angleY = 0;
    private static int angleX = 0;
    private double posX = 0;
    private double posY = 0;
    private double posZ = -0;
    private double momentumX = 0;
    private double momentumZ = 0;
    private double momentum = 0;
    private final double maxMomentum = 0.35;
    private double scale = 1;
    private boolean firstPersonEnabled = false;
    private static int framerate = 60;
    private String grassTexture = "grass_top.png";
    private String grassTextureExt = "png";
    private final int grassTextureId = 0;
    private String leafTexture = "leaves.jpg";
    private String leafTextureExt = "jpg";
    private final int leafTextureId = 1;
    private String trunkTexture = "trunk.png";
    private String trunkTextureExt = "png";
    private final int trunkTextureId = 2;
    MyObject testObject;
    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
   
    }
    
    /** 
     * Run the game.
     *
     */
    public void run(Game game) {
    	  GLProfile glp = GLProfile.getDefault();
          GLCapabilities caps = new GLCapabilities(glp);
          GLJPanel panel = new GLJPanel();
          panel.addGLEventListener(this);
          
          // DEBUG: add a key listener to respond to keypresses
          // the panel needs to be focusable to get key events
          panel.addKeyListener(game);        
          panel.setFocusable(true);   
          
          
 
          // Add an animator to call 'display' at 60fps        
          FPSAnimator animator = new FPSAnimator(framerate);
          animator.add(panel);
          animator.start();

          getContentPane().add(panel);
          setSize(800, 600);        
          setVisible(true);
          setDefaultCloseOperation(EXIT_ON_CLOSE);        
    }
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length == 0) {
            System.out.println("No level file provided.");
            return;
        }
        Terrain terrain = LevelIO.load(new File(args[0]));
        
        if (args.length == 2) {
            try {
                framerate = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                framerate = 60;
            }
        }
        Game game = new Game(terrain);
        game.run(game);
    }

	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
    	GL2 gl = drawable.getGL().getGL2();
    	GLU glu = new GLU();
    	
    	//Forgetting to clear the depth buffer can cause problems 
    	//such as empty black screens.
    	gl.glClearColor(1, 1, 1, 1);
    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	
    	gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();  
        
        //Move camera
        
        //round off numbers to 1 decimal place for now to make debugging/math easier
        DecimalFormat df = new DecimalFormat("#.##");       
        posX = Double.parseDouble(df.format(posX));	
        posZ = Double.parseDouble(df.format(posZ));
        
        updateMomentum();
        updateHeight();
        
        double sinShift = sinDeg(angleY);
        double cosShift = cosDeg(angleY);
        setCamera(gl, sinShift, cosShift);
        
        float[] pos = {0, 1, 1, 0};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
        
        
        if (firstPersonEnabled) {
            gl.glTranslated(-posX, -posY, posZ);
        } else {
            gl.glTranslated(-posX + 0.5 * sinShift, -posY, posZ - 0.5 * cosShift); //shift axis to twist on
        }

        gl.glRotated(90, 0, 1, 0);
        gl.glScaled(scale,scale,scale);
        float fLargest[] = new float[1];
        gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, fLargest,0);
  
        displayTerrain(gl);
        displayTrees(gl);
    	displayRoads(gl);
    	displayEnemies(gl);
        
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL2.GL_FILL);   
	}

	private void updateMomentum() {
	    //insert momentum here
	    
	}
	
	private void updateHeight() { 
		System.out.println("############################################ x,z: " + posX +","+posZ );
		
	    try {
	        posY = myTerrain.altitude(posX, posZ);
	        System.out.println("############################################ height: " + posY);
	    } catch (ArrayIndexOutOfBoundsException e) {
	       posY = 0;
	    }
	    
	}
	
	private void setCamera(GL2 gl, double xOffset, double zOffset) {
		glu.gluLookAt(0, 0.1, 0.0, 0 + xOffset, 0.1, 0 - zOffset, 0, 1, 0);
        gl.glPushMatrix();
            gl.glTranslated(0 + 0.5 * xOffset, -0.1, 0 - 0.5 * zOffset);
            gl.glScaled(0.2, 0.2, 0.2);
            if (!firstPersonEnabled) {
                gl.glTranslated(0, 0.65, 0);
                gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[trunkTextureId].getTextureId());
                testObject = new MyObject(gl);
                testObject.draw(gl);
                //glut.glutSolidTeapot(0.1f);
            }
        gl.glPopMatrix();
	}
	
	private void displayRoads(GL2 gl) {
		
		for (Road currRoad : this.myTerrain.roads()) {
			gl.glPushMatrix();
				double width = currRoad.width();
				int size = currRoad.size();
				gl.glBegin(GL2.GL_LINE_STRIP);
					for (double t = 0; t < currRoad.size(); t+=0.05) {
						double point[] = currRoad.point(t);
						gl.glVertex3d(point[0], this.myTerrain.altitude(point[0], point[1])+0.05, point[1]);
					}
				gl.glEnd();
			gl.glPopMatrix();
		}
		
	}
	
	private void displayEnemies(GL2 gl) {
	    for (Enemy currEnemy : this.myTerrain.enemies()) {
	        double currEnemyPos[] = currEnemy.getPosition();
	        gl.glPushMatrix();
	            gl.glTranslated(currEnemyPos[0], currEnemyPos[1], currEnemyPos[2]);
	            gl.glScaled(0.1, 0.1, 0.1);
	            glut.glutSolidSphere(1, 10, 10);
	        gl.glPopMatrix();
	    }
	}

	private void displayTrees(GL2 gl) {
		double trunkHeight = 5;
		double trunkRadius = 1;
		double leavesRadius = 3;
		
		for(Tree currTree : this.myTerrain.trees()) {
			double currTreePos[] = currTree.getPosition();	
			gl.glPushMatrix();
				gl.glTranslated(currTreePos[0], currTreePos[1], currTreePos[2]);
				gl.glScaled(0.1, 0.1, 0.1);
				// draw trunk		
				drawTrunk(gl, trunkRadius, trunkHeight);			
				// draw leaves
				gl.glTranslated(0, trunkHeight + leavesRadius - 1, 0);
				drawLeaves(gl, leavesRadius);
			gl.glPopMatrix();
		}
	}

	private void drawTrunk(GL2 gl, double radius, double height) {
		gl.glPushMatrix();	
	        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[trunkTextureId].getTextureId());
	        gl.glTranslated(-0.5, 0, 0.5);
	        gl.glBegin(GL2.GL_QUADS);
	        // front   
	        
	        gl.glNormal3d(0,0,1);
	        
	        gl.glTexCoord2d(0.0, 0.0);
	        gl.glVertex3d(0, 0, 0); 
	        gl.glTexCoord2d(1.0, 0.0);
	        gl.glVertex3d(1, 0, 0); 
	        gl.glTexCoord2d(1.0, 1.0);
	        gl.glVertex3d(1, height, 0);
	        gl.glTexCoord2d(0.0, 1.0);
	        gl.glVertex3d(0, height, 0); 
	        // back 
	        
	        gl.glColor3f(1, 1, 0); 
	        gl.glNormal3d(0,0,-1);
	        
	        gl.glTexCoord2d(0.0, 0.0);
	        gl.glVertex3d(0, 0, -1);
	        gl.glTexCoord2d(1.0, 0.0);
	        gl.glVertex3d(0, height, -1); 
	        gl.glTexCoord2d(1.0, 1.0);
	        gl.glVertex3d(1, height, -1);    
	        gl.glTexCoord2d(0.0, 1.0);
	        gl.glVertex3d(1, 0, -1); 
	        
	        
	        // top
	        gl.glColor3f(1, 0, 0);
	        gl.glNormal3d(0,1,0);
	        
	        gl.glTexCoord2d(0.0, 0.0);
	        gl.glVertex3d(0, height, 0); 
	        gl.glTexCoord2d(1.0, 0.0);
	        gl.glVertex3d(1, height, 0); 
	        gl.glTexCoord2d(1.0, 1.0);
	        gl.glVertex3d(1, height, -1);  
	        gl.glTexCoord2d(0.0, 1.0);
	        gl.glVertex3d(0, height, -1);  
	        
	        // bottom  
	        gl.glColor3f(0, 1, 0); 
	        gl.glNormal3d(0,-1,0);
	        
	        gl.glTexCoord2d(0.0, 0.0);
	        gl.glVertex3d(0, 0, 0);
	        gl.glTexCoord2d(1.0, 0.0);
	        gl.glVertex3d(0, 0, -1); 
	        gl.glTexCoord2d(1.0, 1.0);
	        gl.glVertex3d(1, 0, -1);    
	        gl.glTexCoord2d(0.0, 1.0);
	        gl.glVertex3d(1, 0, 0); 
	        
	        //left
	        gl.glColor3f(0, 1, 1); 
	        gl.glNormal3d(-1,0,0);
	        
	        gl.glTexCoord2d(0.0, 0.0);
	        gl.glVertex3d(0, height, -1);
	        gl.glTexCoord2d(1.0, 0.0);
	        gl.glVertex3d(0, 0, -1);
	        gl.glTexCoord2d(1.0, 1.0);
	        gl.glVertex3d(0, 0, 0);
	        gl.glTexCoord2d(0.0, 1.0);
	        gl.glVertex3d(0, height, 0);
	        
	        //right
	        gl.glColor3f(0, 0, 1); 
	        gl.glNormal3d(1,0,0);
	        
	        gl.glTexCoord2d(0.0, 0.0);
	        gl.glVertex3d(1, 0, -1);
	        gl.glTexCoord2d(1.0, 0.0);
	        gl.glVertex3d(1, height, -1);
	        gl.glTexCoord2d(1.0, 1.0);
	        gl.glVertex3d(1, height, 0);
	        gl.glTexCoord2d(0.0, 1.0);
	        gl.glVertex3d(1, 0, 0);
	        
	        gl.glEnd();
			
        gl.glPopMatrix();
	}
	

	private void drawLeaves(GL2 gl, double radius) {
	    gl.glPushMatrix();
    	    GLUquadric sphere = glu.gluNewQuadric();
    	    gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[leafTextureId].getTextureId());
            //glut.glutSolidSphere(radius, 40, 40);
    	    glu.gluQuadricTexture(sphere, true);
            glu.gluSphere(sphere, 4, 20, 20);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[grassTextureId].getTextureId());
        gl.glPopMatrix();
	}

	private void displayTerrain(GL2 gl) {
		gl.glPushMatrix();
		Dimension d = this.myTerrain.size();
		
		double p0[] = new double[3];
		double p1[] = new double[3];
		double p2[] = new double[3];
		
		//specifiy how texture values combine with current surface color values.
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE );
		gl.glTexParameteri(GL2.GL_TEXTURE, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		//use textures here
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[grassTextureId].getTextureId());
		for (int x = 0; x < d.getWidth()-1; x++) { //
			for (int z = 0; z < d.getHeight()-1; z++) {
			    gl.glPushMatrix();
				gl.glBegin(GL2.GL_TRIANGLE_STRIP);
				p0[0] = x;
				p0[1] = myTerrain.getGridAltitude(x, z+1);
				p0[2] = z+1;
				
				p1[0] = x;	
				p1[1] = myTerrain.getGridAltitude(x, z);
				p1[2] = z;

				p2[0] = x+1;	
				p2[1] = myTerrain.getGridAltitude(x+1,z);
				p2[2] = z;
				
				double [] n0 = getNormal(p2,p1,p0);
				n0 = normalise(n0);
				printPoint(p0,"");
				printPoint(p1,"");
				printPoint(p2,": ");
				printVector(n0,"\n");
				
				gl.glNormal3d(n0[0], n0[1], n0[2]);
				
				gl.glTexCoord2d(0.0, 0.0);
				gl.glVertex3d(p0[0], p0[1], p0[2]);
				gl.glTexCoord2d(0.0, 1.0);
				gl.glVertex3d(p1[0], p1[1], p1[2]);
				gl.glTexCoord2d(1.0, 0.0);
				gl.glVertex3d(p2[0], p2[1], p2[2]);
				
				p0[0] = p1[0];
				p0[1] = p1[1];
				p0[2] = p1[2];
				
				p1[0] = p2[0];
				p1[1] = p2[1];
				p1[2] = p2[2];	
				
				p2[0] = x+1;	
				p2[1] = myTerrain.getGridAltitude(x+1, z+1);
				p2[2] = z+1;
				double [] n1 = getNormal(p2,p1,p0);
				n1 = normalise(n1);
				printPoint(p0,"");
				printPoint(p1,"");
				printPoint(p2,": ");
				printVector(n1,"\n");
				
				gl.glNormal3d(n1[0], n1[1], n1[2]);
				gl.glTexCoord2d(0.0, 0.0);
				gl.glVertex3d(p2[0], p2[1], p2[2]);	
				
				p0[0] = p1[0];
				p0[1] = p1[1];
				p0[2] = p1[2];
				
				p1[0] = p2[0];
				p1[1] = p2[1];
				p1[2] = p2[2];
				
				p2[0] = x;
				p2[1] = this.myTerrain.getGridAltitude(x, z+1);
				p2[2] = z+1;
				
				double [] n2 = getNormal(p2,p1,p0);
				n2 = normalise(n2);
				printPoint(p0,"");
				printPoint(p1,"");
				printPoint(p2,": ");
				printVector(n2,"\n");
				
				gl.glNormal3d(n2[0], n2[1], n2[2]);
				gl.glTexCoord2d(0.0, 1.0);
				gl.glVertex3d(p2[0], p2[1], p2[2]);
				
				p0[0] = p1[0];
				p0[1] = p1[1];
				p0[2] = p1[2];
				
				p1[0] = p2[0];
				p1[1] = p2[1];
				p1[2] = p2[2];
				
				p2[0] = x;
				p2[1] = this.myTerrain.getGridAltitude(x,z);
				p2[2] = z;
				
				double [] n3 = getNormal(p2,p1,p0);
				n3 = normalise(n3);
				printPoint(p0,"");
				printPoint(p1,"");
				printPoint(p2,": ");
				printVector(n3,"\n");
				
				gl.glNormal3d(n3[0], n3[1], n3[2]);
				gl.glTexCoord2d(1.0, 0.0);
				gl.glVertex3d(p2[0], p2[1], p2[2]);
				
				gl.glEnd();
				gl.glPopMatrix();
				//System.out.println();
			}		
		}
		
		//System.exit(0);
		gl.glPopMatrix();
	}
	
	double getMagnitude(double [] n){
    	double mag = n[0]*n[0] + n[1]*n[1] + n[2]*n[2];
    	mag = Math.sqrt(mag);
    	return mag;
    }
    
    double [] normalise(double [] n){
    	double  mag = getMagnitude(n);
    	double norm[] = {n[0]/mag,n[1]/mag,n[2]/mag};
    	return norm;
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

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
		glut = new GLUT();
    	glu = new GLU();
    	//If you do not add this line
    	//opengl will draw things in the order you
    	//draw them in your program
		gl.glEnable(GL2.GL_DEPTH_TEST);
  	  
    	// enable lighting
        gl.glEnable(GL2.GL_LIGHTING);
        //Turn on default light
        gl.glEnable(GL2.GL_LIGHT0);
        
        
        float globAmb[] = {0.9f, 0.9f, 0.9f, 1.0f};
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); // Global ambient light.
        
        // normalise normals (!)
        // this is necessary to make lighting work properly
        gl.glEnable(GL2.GL_NORMALIZE);
        
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);
        
        //enable textures
        gl.glEnable(GL2.GL_TEXTURE_2D);
        myTextures = new LevelTexture[3];
        myTextures[0] = new LevelTexture(gl, grassTexture, grassTextureExt, true);
        myTextures[1] = new LevelTexture(gl, leafTexture, leafTextureExt, true);
        myTextures[2] = new LevelTexture(gl, trunkTexture, trunkTextureExt, true);
        testObject = new MyObject(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
	    //GLU glu = new GLU();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        glu = new GLU();
        glu.gluPerspective(75, (float)width/(float)height, 0.01, 100.0);
        /*
        double aspect = (1.0 * width) / height;
        double size = 1.0;
        if(aspect >=1){
             gl.glOrtho(-size * aspect, size* aspect, -size, size, 1, 10);
         } else {
             gl.glOrtho(-size, size, -size/aspect, size/aspect, 1, 10);
         }
         */
       // gl.glOrtho(-2,2,-2,2,1,10);
        
        myTextures[0] = new LevelTexture(gl, grassTexture, grassTextureExt, true);
        myTextures[1] = new LevelTexture(gl, leafTexture, leafTextureExt, true);
        myTextures[2] = new LevelTexture(gl, trunkTexture, trunkTextureExt, true);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		  
            case KeyEvent.VK_UP:
                   
                //angleX = (angleX + 10) % 360;
                break;
            case KeyEvent.VK_DOWN:
            	     
                //angleX = (angleX - 10) % 360;
                break;	
            case KeyEvent.VK_LEFT:
                   
                angleY = (angleY - 10) % 360;
                break;
            case KeyEvent.VK_RIGHT:
                 
                angleY = (angleY + 10) % 360;
                break;
             
            case KeyEvent.VK_W:
                
                posZ += Math.cos(Math.toRadians(angleY)) * 0.1;
                posX += Math.sin(Math.toRadians(angleY)) * 0.1;
                //momentumZ += 0.2;
                break;
                
            case KeyEvent.VK_S:
                
                posZ -= Math.cos(Math.toRadians(angleY)) * 0.1;
                posX -= Math.sin(Math.toRadians(angleY)) * 0.1;
                //momentumZ -= 0.2;
                break;
                
            case KeyEvent.VK_A:
            	//posX -= 0.1;
            	angleY = (angleY - 10) % 360;
                //momentumX -= 0.2;
                break;
            case KeyEvent.VK_D:
            	//posX += 0.1;
            	angleY = (angleY + 10) % 360;
                //momentumX += 0.2;
                break;
                
            case KeyEvent.VK_Q:
                
                scale -= 0.1;
                break;
            case KeyEvent.VK_E:
                
                scale += 0.1;
                break;
            case KeyEvent.VK_R:
                
                posY -= 0.1;
                break;
            
            case KeyEvent.VK_F:
                
                posY += 0.1;
                break;
            case KeyEvent.VK_C:
                if (firstPersonEnabled) {
                    firstPersonEnabled = false;
                } else {
                    firstPersonEnabled = true;
                }
            default:
                break;
		 }
		 System.out.println(angleX + " " + angleY);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	void printPoint (double [] p, String end) {
		//System.out.print("["+ p[0] + "," + p[2]+"] (" +p[1] + ") " + end);
	}
	
	void printVector (double [] p, String end) {
		//System.out.print("("+ p[0] + "," + p[1] + "," + p[2]+") " + end);
	}
	
	static double sinDeg (double degree) {
	    return Math.sin(Math.toRadians(degree));
	}
	static double cosDeg (double degree) {
        return Math.cos(Math.toRadians(degree));
    }
	
}
