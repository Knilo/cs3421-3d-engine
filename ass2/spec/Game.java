package ass2.spec;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
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
    private double posY = -0.2;
    private double posZ = -4;
    private double momentumZ = 0;
    private double scale = 0.25;
    private static int framerate = 60;
    private String grassTexture = "grass.bmp";
    private String grassTextureExt = "bmp";
    private String leafTexture = "leaves.jpg";
    private String leafTextureExt = "jpg";
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
        if (momentumZ > 0.5) {
            momentumZ = 0.5;
        } else if (momentumZ < -0.5) {
            momentumZ = -0.5;
        }
        posZ += momentumZ * momentumZ * momentumZ;
        if (momentumZ < 0.02 && momentumZ > -0.02) {
            momentumZ = 0;
        } else if (momentumZ < 0) {
            momentumZ += 0.01;
        } else if (momentumZ > 0) {
            momentumZ -= 0.01;
        }
        double sinShift = sinDeg(angleY);
        double cosShift = cosDeg(angleY);
        setCamera(gl, sinShift, cosShift);
        
        gl.glTranslated(posX + 0.5 * sinShift, posY, posZ - 0.5 * cosShift);
        //gl.glRotated(-angleY + 180, 0, 1, 0);
        //gl.glRotated(-angleX, 1, 0, 0);
        gl.glScaled(scale,scale,scale);
        //glu.gluLookAt(0, 0.1, -2, posX + Math.sin(Math.toRadians(angleY)), posY, posZ + Math.sin(Math.toRadians(angleY)), 0, 1, 0);
        //gl.glPushMatrix();
        
        //gl.glPopMatrix();
        //gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL2.GL_LINE);
        
        gl.glPushMatrix();
            //trying to change the angle of the terrain so that we can see it from a bird's eye view
            //however, camera will still move into the terrain.
            //gl.glTranslated (0, (posZ * Math.sin(Math.toRadians(20))), 0);
            //gl.glRotated(20, 1, 0, 0);         
            displayTerrain(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
            displayTrees(gl);
        gl.glPopMatrix();
        
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL2.GL_FILL);   
	}
	
	private void setCamera(GL2 gl, double xOffset, double zOffset) {
	    
        glu.gluLookAt(0, 0.1, 0, 0 + xOffset, 0.1, 0 - zOffset, 0, 1, 0);
        gl.glPushMatrix();
            gl.glTranslated(0 + 0.5 * xOffset, 0, 0 - 0.5 * zOffset);
            gl.glScaled(0.2, 0.2, 0.2);
            glut.glutSolidTeapot(0.1f);
        gl.glPopMatrix();
	    
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
			gl.glRotated(-90, 1, 0, 0);
			glut.glutSolidCylinder(radius, height, 40, 40);
        gl.glPopMatrix();
	}

	private void drawLeaves(GL2 gl, double radius) {
	    GLUquadric sphere = glu.gluNewQuadric();
	    gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[1].getTextureId());
        //glut.glutSolidSphere(radius, 40, 40);
	    glu.gluQuadricTexture(sphere, true);
        glu.gluSphere(sphere, 4, 20, 20);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());
	}

	private void displayTerrain(GL2 gl) {
		Dimension d = this.myTerrain.size();
		
		double p0[] = new double[3];
		double p1[] = new double[3];
		double p2[] = new double[3];
		
		//specifiy how texture values combine with current surface color values.
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE );
		gl.glTexParameteri(GL2.GL_TEXTURE, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
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
				
				//use textures here
				gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[0].getTextureId());
				
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
				System.out.println();
			}		
		}
		
		//System.exit(0);
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
        float[] pos = {0, 1, 1, 1};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
        
        float globAmb[] = {0.9f, 0.9f, 0.9f, 1.0f};
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); // Global ambient light.
        
        // normalise normals (!)
        // this is necessary to make lighting work properly
        gl.glEnable(GL2.GL_NORMALIZE);
        
        gl.glEnable(GL2.GL_CULL_FACE);
        gl.glCullFace(GL2.GL_BACK);
        
        //enable textures
        gl.glEnable(GL2.GL_TEXTURE_2D);
        myTextures = new LevelTexture[2];
        myTextures[0] = new LevelTexture(gl, grassTexture, grassTextureExt, true);
        myTextures[1] = new LevelTexture(gl, leafTexture, leafTextureExt, true);
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
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		  
            case KeyEvent.VK_UP:
                   
                angleX = (angleX + 10) % 360;
                break;
            case KeyEvent.VK_DOWN:
            	     
                angleX = (angleX - 10) % 360;
                break;	
            case KeyEvent.VK_LEFT:
                   
                angleY = (angleY + 10) % 360;
                break;
            case KeyEvent.VK_RIGHT:
                 
                angleY = (angleY - 10) % 360;
                break;
             
            case KeyEvent.VK_W:
                
                //posZ += 0.1;
                momentumZ += 0.3;
                break;
                
            case KeyEvent.VK_S:
                
                //posZ -= 0.1;
                momentumZ -= 0.3;
                break;
                
            case KeyEvent.VK_A:
                
                posX += 0.1;
                break;
            case KeyEvent.VK_D:
                posX -= 0.1;
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
		System.out.print("["+ p[0] + "," + p[2]+"] (" +p[1] + ") " + end);
		//System.out.print("["+ p[0] + "," + p[2]+"] (" +p[1] + ") " + end);
	}
	
	void printVector (double [] p, String end) {
		System.out.print("("+ p[0] + "," + p[1] + "," + p[2]+") " + end);
	}
	
	static double sinDeg (double degree) {
	    return Math.sin(Math.toRadians(degree));
	}
	static double cosDeg (double degree) {
        return Math.cos(Math.toRadians(degree));
    }
	
}
