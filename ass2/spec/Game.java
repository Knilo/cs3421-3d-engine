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
    private static int angle = 0;

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
          FPSAnimator animator = new FPSAnimator(60);
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
        
      //Move camera
        gl.glRotated(angle, 1, 0, 0);
        gl.glTranslated(-1,0,-2); //so it does not get clipped.
        gl.glScaled(0.25, 0.25, 0.25);
    
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL2.GL_LINE);
    	displayTerrain(gl);
    	gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL2.GL_FILL);
	}

	private void displayTerrain(GL2 gl) {
		// TODO Auto-generated method stub
		Dimension d = this.myTerrain.size();
		gl.glBegin(GL2.GL_TRIANGLE_STRIP);
		for (int i = 0; i < d.getWidth()-1; i++) {
			if (i % 2 == 0) {
				for (int j = 0; j < d.getHeight (); j++) {
					
					gl.glVertex3d(i, myTerrain.getGridAltitude(i, j), j);
					gl.glVertex3d(i+1, myTerrain.getGridAltitude(i+1, j), j);
				}
			} else {
				for (int j = (int) (d.getHeight()-1); j > 0; j--) {					
					gl.glVertex3d(i, myTerrain.getGridAltitude(i, j), j);
					gl.glVertex3d(i+1, myTerrain.getGridAltitude(i+1, j), j);
				}
			}			
		}	
		
		
		gl.glEnd();
	
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
        
        // normalise normals (!)
        // this is necessary to make lighting work properly
        gl.glEnable(GL2.GL_NORMALIZE);
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
		       
				  angle = (angle + 10) % 360;
				  break;
		 case KeyEvent.VK_DOWN:
			     
				  angle = (angle - 10) % 360;
				  break;		
		 default:
			 break;
		 }
		 System.out.println(angle);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
