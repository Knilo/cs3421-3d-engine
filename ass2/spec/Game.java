package ass2.spec;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import com.jogamp.opengl.util.FPSAnimator;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class Game extends JFrame implements GLEventListener, KeyListener {

    private Terrain myTerrain;
    private static int angleY = 0;
    private static int angleX = 0;
    private static int framerate = 60;
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

    	//Forgetting to clear the depth buffer can cause problems 
    	//such as empty black screens.
    	gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	
    	gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();  
      
        //Move camera
        
        gl.glTranslated(-1, -1, -7);
        gl.glRotated(-angleY, 0, 1, 0);
        gl.glRotated(-angleX, 1, 0, 0);
        
        //gl.glScaled(0.25,0.25,0.25);
        gl.glScaled(0.25,0.25,0.25);
        
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL2.GL_FILL);       
        displayTerrain(gl);
    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL2.GL_FILL);     
	}

	private void displayTerrain(GL2 gl) {
		// TODO Auto-generated method stub
		Dimension d = this.myTerrain.size();
		
		double p0[] = new double[3];
		double p1[] = new double[3];
		double p2[] = new double[3];
		
		for (int i = 0; i < d.getWidth()-1; i++) {
			gl.glBegin(GL2.GL_TRIANGLE_STRIP);
			for (int j = 0; j < d.getHeight (); j++) {			
				if (j > 0) {
					p2[0] = i;	
					p2[1] = myTerrain.getGridAltitude(i, j);
					p2[2] = j;
					System.out.print("1 ");
					printPoint(p2);
				
					double [] n1 = getNormal(p0,p1,p2);				
					n1 = normalise(n1);
					System.out.println("n1:" + n1[0]+ " "+ n1[1]+" " +n1[2]);
					if (n1[1] < 0) {
					    System.out.println("LESS THAN 0!");
					}
					gl.glNormal3d(n1[0], n1[1], n1[2]);
					gl.glVertex3d(p2[0], p2[1], p2[2]);
					
					p0[0] = p1[0];
					p0[1] = p1[1];
					p0[2] = p1[2];
					
					p1[0] = p2[0];
					p1[1] = p2[1];
					p1[2] = p2[2];	
					
					p2[0] = i+1;	
					p2[1] = myTerrain.getGridAltitude(i+1, j);
					p2[2] = j;
					System.out.print("2 ");
					printPoint(p2);
					double [] n2 = getNormal(p2,p1,p0);
					n1 = normalise(n2);
					System.out.println("n2:" + n2[0]+ " "+ n2[1]+" " +n2[2]);
					gl.glNormal3d(n2[0], n2[1], n2[2]);
					gl.glVertex3d(p2[0], p2[1], p2[2]);
					
					p0[0] = p1[0];
					p0[1] = p1[1];
					p0[2] = p1[2];
					
					p1[0] = p2[0];
					p1[1] = p2[1];
					p1[2] = p2[2];	
			
				} else {
					p0[0] = i;
					p0[1] = myTerrain.getGridAltitude(i, j);
					p0[2] = j;
					
					p1[0] = i+1;
					p1[1] = myTerrain.getGridAltitude(i+1, j);
					p1[2] = j;
					System.out.print("A ");
					printPoint(p0);
					System.out.print("B ");
					printPoint(p1);
					gl.glVertex3d(p0[0], p0[1], p0[2]);
					gl.glVertex3d(p1[0], p1[1], p1[2]);
//					System.out.println(p0[0] + " " + p0[2]);
//					System.out.println(p1[0] + " " + p1[2]);
				}								
			}
			gl.glEnd();
		}	
		
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
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// TODO Auto-generated method stub
		GL2 gl = drawable.getGL().getGL2();
	    
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
       
        gl.glOrtho(-2,2,-2,2,1,10);
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
	void printPoint (double [] p) {
			System.out.println("XZ("+p[0] + "," + p[2]+")" + ": " + p[1] );
	}
	
}

