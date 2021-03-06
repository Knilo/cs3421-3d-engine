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
    
    //jimmys vars
    private float[] sunlightPos;
    private double sunlightAngle;
    private float[] sunlight;
    
    //weilons vars
    //private float[] sunlightDir = {0, -10000, 0, 0};
    //private float[] sunlight = {0.5f, 0.5f, 0.5f, 0};
    //private double sunlightDelta = 45;
    
    private int cameraAngle = 0;
    private double momentum = 0;
    private final double maxMomentum = 0.35;
    private double scale = 1;
    private boolean firstPersonEnabled = false;
    private boolean freeLookEnabled = false;
    private boolean enabledRain = false;
    private boolean enabledBurst = false;
    private final int maxRainParticles = 1000;
    private RainParticle[] rainParticles = new RainParticle[maxRainParticles];
    private static int framerate = 60;
    
    //textures
    private String grassTexture = "grass_top.png"; //texture taken from video game "Minecraft"
    private String grassTextureExt = "png";
    private final int grassTextureId = 0;
    
    private String leafTexture = "leaves.png"; //texture taken from video game "Minecraft"
    private String leafTextureExt = "png";
    private final int leafTextureId = 1;
    
    private String trunkTexture = "trunk.png"; //texture taken from video game "Minecraft"
    private String trunkTextureExt = "png";
    private final int trunkTextureId = 2;
    
    private String rainTexture = "rain.png";
    private String rainTextureExt = "png";
    private final int rainTextureId = 3;
    
    private String roadTexture = "road.png";
    private String roadTextureExt = "png";
    private final int roadTextureId = 4;
    
    private String bluePortalTexture = "blueportal.png"; //texture taken from video game "Portal"
    private String bluePortalTextureExt = "png";
    private final int bluePortalTextureId = 5;
    
    private String orangePortalTexture = "orangeportal.png"; //texture taken from video game "Portal"
    private String orangePortalTextureExt = "png";
    private final int orangePortalTextureId = 6;
    
    private String creeperTexture = "creeper-body.png"; //texture taken from video game "Minecraft"
    private String creeperTextureExt = "png";
    
    private String playerTexture = "steve.png"; //texture taken from video game "Minecraft"
    private String playerTextureExt = "png";

    EnemyObject creeperHead;
    PlayerObject playerHead;
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
          
          // Add a key listener to respond to keypresses
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
    
    
    /**
     * Main display method
     */
	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
    	GL2 gl = drawable.getGL().getGL2();
    	GLU glu = new GLU();
    	
    	//Forgetting to clear the depth buffer can cause problems 
    	//such as empty black screens.
    	gl.glClearColor(1, 1, 1, 1);
    	//gl.glClearColor(sunlight[0]*0.8f + 0.2f, sunlight[1]*0.8f + 0.2f, sunlight[2] + 0.9f, 1);
    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    	
    	//load the matrix and model view
    	gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();  

        
        //round off numbers to 4 decimal places for now to make debugging/math easier
        DecimalFormat df = new DecimalFormat("#.####");       
        posX = Double.parseDouble(df.format(posX));	
        posZ = Double.parseDouble(df.format(posZ));
        
        updateMomentum(); //update player's momentum/velocity
        updateHeight(); //update the player's height depending on their x/z location
        //updateLight(); //update the daylight cycle
        
        
        checkPortals(gl); //check player's location for any portals nearby
        
        double sinShift = sinDeg(angleY); //where the camera will look towards on the X axis
        double cosShift = cosDeg(angleY); //where the camera will look towards on the Z axis
        setCamera(gl, sinShift, cosShift); //set camera to the player's location and desired angle
        
        if (firstPersonEnabled) {
            gl.glTranslated(-posX, -posY, posZ); //camera will twist on itself to rotate
        } else {
            gl.glTranslated(-posX + 0.5 * sinShift, -posY, posZ - 0.5 * cosShift); 
            //shift axis to twist on to the avatar object instead of camera itself
        }
        
        gl.glPushMatrix();
            gl.glRotated(90, 0, 1, 0);
            gl.glScaled(scale,scale,scale);
            float fLargest[] = new float[1];
            gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, fLargest,0);
    
            //display things
            setSunlight(gl);
            displayTerrain(gl); //render terrain
            displayTrees(gl); //render trees
        	displayRoads(gl); //render roads
        	displayEnemies(gl); //render enemies
        	displayPortals(gl); //render portals
        	displayRain(gl); //render train if enabled
        	
        	// set lighting
        	//sunlightDir[2] -= 0.5;
    	gl.glPopMatrix();
    	
    	//gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, sunlightDir, 0);
    	//gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, sunlight, 0);
        //gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, sunlight, 0);
        
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK,GL2.GL_FILL);   
	}
	
	/**
	 * Set source of main lighting
	 * @param gl GL object
	 */
	private void setSunlight(GL2 gl) {
		gl.glPushMatrix();			
			gl.glTranslated(myTerrain.size().getWidth()/2, 0, myTerrain.size().getHeight()/2);
			double rotateVector[] = new double [3];
			
			if (sunlightPos[0] != 0 && sunlightPos[2] != 0) {
				if ((sunlightPos[0] > 0 && sunlightPos[2] > 0) || (sunlightPos[0] < 0 && sunlightPos[2] < 0)) {
					rotateVector[0] = 1;
					rotateVector[1] = 0;
					rotateVector[2] = -1;
				} else {
					rotateVector[0] = -1;
					rotateVector[1] = 0;
					rotateVector[2] = -1;
				}				
			} else if (sunlightPos[0] != 0) {
				rotateVector[0] = 0;
				rotateVector[1] = 0;
				rotateVector[2] = 1;
			} else if (sunlightPos[2] != 0) {
				rotateVector[0] = 1;
				rotateVector[1] = 0;
				rotateVector[2] = 0;
			} else {
				rotateVector[0] = 1;
				rotateVector[1] = 0;
				rotateVector[2] = 0;
			}
			
			
			System.out.print("Roate Vector: ");
			printVector(rotateVector,"\n");
			gl.glRotated(-sunlightAngle ,rotateVector[0] ,rotateVector[1], rotateVector[2]);		
	        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, sunlightPos, 0); 
	        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, sunlight, 0);
	        sunlightAngle += 1;
	        
	        //give a visual of the location of the 'sun'
//	        gl.glPushMatrix();      	        	
//	        	gl.glTranslated(5*sunlightPos[0], 5*sunlightPos[1], 5*sunlightPos[2]);	        	
//	        	glut.glutSolidSphere(1, 40, 40); 
//        	gl.glPopMatrix();
	        
	        
	        //sunlight[0] +=0.01;
	        //sunlight[0] %= 10;
	        
        gl.glPopMatrix();
	}
	
	/**
	 * Updates momentum of avatar
	 */
	private void updateMomentum() {
	    //set player's position depending on current angle of rotation
	    posX += momentum * Math.sin(Math.toRadians(angleY));
	    posZ += momentum * Math.cos(Math.toRadians(angleY));
	    
	    //momentum will decay by 20% at every iteration
	    momentum = momentum * 0.80;
	    if (Math.abs(momentum) < 0.005) { //stop the player if momentum is less than 0.005
	        momentum = 0;
	    }
	    
	}
	
//	private void updateLight() {
//	    //TO DO: CLAMP SUNLIGHT TO 0
//        sunlightDelta += 1;
//        
//        for (int i = 0; i < sunlight.length; i++) {
//            sunlight[i] = (float) Math.cos(Math.toRadians(sunlightDelta));
//            if (sunlight[i] < 0) {
//                sunlight[i] = 0;
//            }
//        }
//	    sunlightDelta %= 360;
//
//	}
	
	/**
	 * Get altitude height of player's position
	 */
	private void updateHeight() { 
		//System.out.println("############################################ x,z: " + posX +","+posZ );
		
	    try {
	        posY = myTerrain.altitude(posZ, posX);
	       //System.out.println("############################################ height: " + posY);
	    } catch (ArrayIndexOutOfBoundsException e) {
	        posY = 0;
	    }
	    
	}
	
	/**
	 * Set the camera's location and render the avatar if 3rd person mode enabled
	 * @param gl GL object
	 * @param xOffset x coordinate camera offset from player object if 3rd person mode enabled
	 * @param zOffset z coordinate camera offset from player object if 3rd person mode enabled
	 */
	private void setCamera(GL2 gl, double xOffset, double zOffset) {
	    double yOffset = (Math.tan(Math.toRadians(cameraAngle))); //set player's viewing top-down viewing angle
	    //glu.gluLook at ([where the camera will be located at] x, y, z, [where the camera is looking towards] x, y, z, [camera transformation])
	    //set camera to look at specific location depending on player's viewing ange
		glu.gluLookAt(0, 0.35 - yOffset, 0.0, 0 + xOffset, 0.1 + yOffset, 0 - zOffset, 0, 1, 0);
        gl.glPushMatrix();
            gl.glTranslated(0 + 0.5 * xOffset, -0.1, 0 - 0.5 * zOffset); //move avatar forward to see the avatar's head if 3rd person enabled
            gl.glScaled(0.2, 0.2, 0.2);
            if (!firstPersonEnabled) {
                gl.glTranslated(0, 0.65, 0); //move camera slightly upwards for better vision
                if (!freeLookEnabled) {
                    gl.glRotated(-angleY, 0, 1, 0); //follow the head if freelook is not enabled
                }
                playerHead.draw(gl); //draw the avatar
                
            }
        gl.glPopMatrix();
	}
	
	/**
	 * Check if player has entered a portal and teleports player if they are
	 * @param gl GL Object
	 */
	private void checkPortals(GL2 gl) {
	    double[] curPos = {posX, posY, posZ};
	    
	    //loop through every portal pair
	    for (PortalPair pp : this.myTerrain.portalPairs()) {
	        //check if player is near a blue portal
	        if (inRange(curPos, pp.bluePortalPos, 0.2)) {
	            curPos = pp.orangePortalPos;
	            //move player to corresponding orange portal
	            posX = curPos[0] + 0.25;
	            posY = curPos[1];
	            posZ = curPos[2];
	            break;
	        }
	        //check if player is near an orange portal
	        if (inRange(curPos, pp.orangePortalPos, 0.2)) {
                curPos = pp.bluePortalPos;
                //move player to corresponding blue portal
                posX = curPos[0] + 0.25;
                posY = curPos[1];
                posZ = curPos[2];
                break;
            }
	    }
	}
	
	/**
	 * Calculate whether two positions are within a specific range
	 * @param pos1 position 1
	 * @param pos2 position 2
	 * @param maxRange the maximum range between the 2 positions 
	 * @return true if position 1 is within maxRange of position 2, false otherwise
	 */
	private boolean inRange (double[] pos1, double[] pos2, double maxRange) {
	    if (pos1.length != pos2.length) {
	        throw new IndexOutOfBoundsException("pos1 array length not equal to pos2 array length");
	    }
	    double sumsquare = 0;
	    for (int i = 0; i < pos1.length; i++) {
	        double indexDiff = pos1[i] - pos2[i];
	        sumsquare += (indexDiff * indexDiff);
	    }
	    
	    double distance = Math.sqrt(sumsquare);
	    
	    return (distance < maxRange);
	    
	}
	
	/**
	 * Display rain if enabled
	 * @param gl GL object
	 */
	private void displayRain(GL2 gl) {
	      //Rain system adapted from particle system example in week 9 lecture code
	      if (enabledRain) {
    	      // Render the rainParticles
              // Enable Blending 
    	      gl.glEnable(GL2.GL_BLEND);      
    	      //Creates an additive blend
    	      gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
    	      
    	      gl.glPushMatrix();
        	      gl.glLoadIdentity(); //render the rain regardless of position (i.e. "directly to the screen")
        	      gl.glTranslated(Math.sin(Math.toRadians(angleY)), 6, Math.cos(Math.toRadians(angleY)));
        	      float y = 0;
        	      float z = -1;
        	      // Render the rainParticles
        	      for (int i = 0; i < maxRainParticles; i++) {
        	         if (rainParticles[i].active) {
        	            // Draw the particle using our RGB values
        	            
        	            gl.glColor4f(rainParticles[i].r, rainParticles[i].g, rainParticles[i].b, rainParticles[i].life);
        
        	            gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[rainTextureId].getTextureId()); 
        	            
        	            gl.glBegin(GL2.GL_QUADS);
        
            	            float px = rainParticles[i].x;
            	            float py = rainParticles[i].y + y;
            	            float pz = rainParticles[i].z + z;
            
            	            gl.glTexCoord2d(1, 1);
            	            gl.glVertex3f(px + 0.5f, py + 0.5f, pz); // Top Right
            	            gl.glTexCoord2d(0, 1);
            	            gl.glVertex3f(px - 0.5f, py + 0.5f, pz); // Top Left
            	            gl.glTexCoord2d(0, 0);
            	            gl.glVertex3f(px - 0.5f, py - 0.5f, pz); // Bottom Left
            	            gl.glTexCoord2d(1, 0);
            	            gl.glVertex3f(px + 0.5f, py - 0.5f, pz); // Bottom Right
        	            gl.glEnd();
        
        	            // Move the particle
        	            rainParticles[i].x += rainParticles[i].speedX;
        	            rainParticles[i].y += rainParticles[i].speedY;
        	            rainParticles[i].z += rainParticles[i].speedZ;
        	            if (rainParticles[i].y < -10) { //reset position
        	                rainParticles[i].y = rainParticles[i].originalY;
        	                rainParticles[i].speedY = -0.1f;
        	                rainParticles[i].speedX = 0;
        	                rainParticles[i].speedZ = 0;
        	            }
        	            
        	            if (rainParticles[i].z > 0.0001) {
        	                rainParticles[i].z -= (float) Math.random() * 0.7;
        	            }
        	            if (rainParticles[i].z < -10) {
        	                rainParticles[i].z += (float) Math.random() * 1.2;
        	            }
        	            
        	            
        	            // Apply the gravity force on y-axis
        	            rainParticles[i].speedY += -0.0008f;
        	            
        	            
        	            if (enabledBurst) {
        	               rainParticles[i].burst(); //reset rain
        	            }
        	         }
        	      }
        	      if (enabledBurst) enabledBurst = false;
    	      gl.glPopMatrix();
    	      
    	      gl.glDisable(GL2.GL_BLEND);
	      }
	}
	
	/**
	 * Display roads
	 * @param gl GL Object
	 */
	private void displayRoads(GL2 gl) {
		
		for (Road currRoad : this.myTerrain.roads()) {
			double width = currRoad.width();
			gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[roadTextureId].getTextureId());
	
			for (double t = 0.02; t < currRoad.size()-0.01; t+=0.01) {
				gl.glPushMatrix();
					
					double p0[] = currRoad.point(t);
					double p1[] = currRoad.point(t+0.01);					
					double a = Math.toDegrees(Math.atan(Math.abs((p1[1]-p0[1])/(p1[0]-p0[0]))));
					
					gl.glTranslated(p0[0], this.myTerrain.altitude(p0[0], p0[1])+0.02, p0[1]);					
					gl.glRotated(a, 0, 1, 0);
					
					gl.glBegin(GL2.GL_QUADS);

					gl.glNormal3d(0, 1, 0);
					    gl.glTexCoord2d(0, 0);
						gl.glVertex3d(0, 0, -width/2);
						gl.glTexCoord2d(0, width/2);
						gl.glVertex3d(0, 0, width/2);
						
						gl.glTexCoord2d(width/2, width/2);
						gl.glVertex3d(-0.15, 0, width/2);
						gl.glTexCoord2d(width/2, 0);
						gl.glVertex3d(-0.15, 0, -width/2);
						
					gl.glEnd();
//							
//							//System.out.println("point: "+p0[0] + " " + p0[1]);													
//							//gl.glVertex3d(0, 0, 0);

//							
//							
//							double m[][] = {{Math.cos(a),0,Math.sin(a)},
//											{-Math.sin(a),1,Math.cos(a)},
//											{0,           0, 1}};
//							
//							double p2[] = multiply(m,p0);
//							double p3[] = multiply(m,p1);
//							
//							gl.glVertex3d(p2[0], this.myTerrain.altitude(p0[0], p0[1])+0.05 , p0[1]);
//							gl.glVertex3d(p3[0], this.myTerrain.altitude(p0[0], p0[1])+0.05 , p0[1]);
//							gl.glVertex3d(p0[0], this.myTerrain.altitude(p0[0], p0[1])+0.05 , p0[1]);
//							gl.glVertex3d(p0[0], this.myTerrain.altitude(p0[0], p0[1])+0.05 , p0[1]);
					
					
				gl.glPopMatrix();
			}
					
		}
		
	}
	
	/**
	 * Render enemy if they exist
	 * @param gl GL Object
	 */
	private void displayEnemies(GL2 gl) {
	    for (Enemy currEnemy : this.myTerrain.enemies()) {
	        double currEnemyPos[] = currEnemy.getPosition();
	        gl.glPushMatrix();
	            gl.glTranslated(currEnemyPos[0], currEnemyPos[1], currEnemyPos[2]);
	            gl.glScaled(0.5, 0.5, 0.5);
	            creeperHead.draw(gl);
	        gl.glPopMatrix();
	    }
	}
	
	/**
	 * Render trees if they exist
	 * @param gl GL Object
	 */
	private void displayTrees(GL2 gl) {
		double trunkHeight = 5;
		double leavesRadius = 2;
		
		for(Tree currTree : this.myTerrain.trees()) {
			double currTreePos[] = currTree.getPosition();	
			gl.glPushMatrix();
				gl.glTranslated(currTreePos[0], currTreePos[1], currTreePos[2]);
				gl.glScaled(0.1, 0.1, 0.1);
				// draw trunk		
				drawTrunk(gl, trunkHeight);			
				// draw leaves
				gl.glTranslated(0, trunkHeight, 0);
				drawLeaves(gl, leavesRadius);
			gl.glPopMatrix();
		}
	}
	
	/**
	 * Render the trunk part of a tree
	 * @param gl GL object
	 * @param height Height of the tree trunk
	 */
	private void drawTrunk(GL2 gl, double height) {
		gl.glPushMatrix();	
	        CuboidObject.drawCuboid(gl, height, 2, myTextures[trunkTextureId].getTextureId());
        gl.glPopMatrix();
	}
	
	
	/**
     * Render the canopy part of a tree
     * @param gl GL object
     * @param width How wide each leaf block is
     */
	private void drawLeaves(GL2 gl, double width) {
	    gl.glPushMatrix();
	        
	        //We're drawing the canopy in the style of the Minecraft standard oak tree
	        for (int x = -2; x <= 2; x++) {
	            for (int z = -2; z <= 2; z++) {
    	            gl.glPushMatrix();
    	                gl.glTranslated(x, 0, z);
    	                CuboidObject.drawCuboid(gl, width, width, myTextures[leafTextureId].getTextureId());
    	            gl.glPopMatrix();
	            }
	        }
	        
	        
	        gl.glTranslated(0, width, 0);
	        CuboidObject.drawCuboid(gl, width, width, myTextures[leafTextureId].getTextureId());
	        gl.glPushMatrix();
	            gl.glTranslated(width, 0, 0);
	            CuboidObject.drawCuboid(gl, width, width, myTextures[leafTextureId].getTextureId());
	        gl.glPopMatrix();
	        gl.glPushMatrix();
                gl.glTranslated(-width, 0, 0);
                CuboidObject.drawCuboid(gl, width, width, myTextures[leafTextureId].getTextureId());
            gl.glPopMatrix();
            gl.glPushMatrix();
                gl.glTranslated(0, 0, width);
                CuboidObject.drawCuboid(gl, width, width, myTextures[leafTextureId].getTextureId());
            gl.glPopMatrix();
            gl.glPushMatrix();
                gl.glTranslated(0, 0, -width);
                CuboidObject.drawCuboid(gl, width, width, myTextures[leafTextureId].getTextureId());
            gl.glPopMatrix();
            gl.glTranslated(0, width, 0);
            CuboidObject.drawCuboid(gl, width, width, myTextures[leafTextureId].getTextureId());

        gl.glPopMatrix();
	}
	
	/**
	 * Render the terrain
	 * @param gl GL Object
	 */
	private void displayTerrain(GL2 gl) {
		gl.glPushMatrix();
		Dimension d = this.myTerrain.size();
		
		double p0[] = new double[3];
		double p1[] = new double[3];
		double p2[] = new double[3];
		
		//use grass texture
		gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[grassTextureId].getTextureId());
		//specifiy how texture values combine with current surface color values.
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE );
		gl.glTexParameteri(GL2.GL_TEXTURE, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		//use textures here
        gl.glBindTexture(GL2.GL_TEXTURE_2D, myTextures[grassTextureId].getTextureId());
		for (int x = 0; x < d.getWidth()-1; x++) { //
			for (int z = 0; z < d.getHeight()-1; z++) {
			    gl.glPushMatrix();
				gl.glBegin(GL2.GL_TRIANGLES);
				p0[0] = x;
				p0[1] = myTerrain.getGridAltitude(x, z);
				p0[2] = z;
				
				p1[0] = x+1;	
				p1[1] = myTerrain.getGridAltitude(x+1, z+1);
				p1[2] = z+1;

				p2[0] = x+1;	
				p2[1] = myTerrain.getGridAltitude(x+1,z);
				p2[2] = z;
				
				double [] n0 = getNormal(p0,p1,p2);
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
				
				p0[0] = x;
				p0[1] = myTerrain.getGridAltitude(x, z);
				p0[2] = z;
				
				p1[0] = x;	
				p1[1] = myTerrain.getGridAltitude(x, z+1);
				p1[2] = z+1;	
				
				p2[0] = x+1;	
				p2[1] = myTerrain.getGridAltitude(x+1, z+1);
				p2[2] = z+1;
				double [] n1 = getNormal(p0,p1,p2);
				n1 = normalise(n1);
				printPoint(p0,"");
				printPoint(p1,"");
				printPoint(p2,": ");
				printVector(n1,"\n");
				
				gl.glNormal3d(n1[0], n1[1], n1[2]);
				gl.glTexCoord2d(0.0, 0.0);
				gl.glVertex3d(p0[0], p0[1], p0[2]);
				gl.glTexCoord2d(1.0, 0.0);
				gl.glVertex3d(p1[0], p1[1], p1[2]);
				gl.glTexCoord2d(0.5, 1.0);
				gl.glVertex3d(p2[0], p2[1], p2[2]);

				gl.glEnd();
				gl.glPopMatrix();
				//System.out.println();
			}		
		}
		
		//System.exit(0);
		gl.glPopMatrix();
	}
	
	/**
	 * Render portals
	 * @param gl GL Object
	 */
	private void displayPortals(GL2 gl) {
	    gl.glPushMatrix();
	    for (PortalPair pp : this.myTerrain.portalPairs()) {
	        pp.draw(gl, myTextures[bluePortalTextureId], myTextures[orangePortalTextureId]);
	    }
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
        //Turn on ambient light
        gl.glEnable(GL2.GL_LIGHT1);
        
        
//        float sunlight[] = {0.6f, 0.6f, 0.6f, 1.0f};
        sunlight = new float[4];
        sunlight[0] = 1.5f;
        sunlight[1] = 0.5f;
        sunlight[2] = 0.5f;
        sunlight[3] = 0.5f;
//	
//        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, sunlight, 0);  
        float globamb[] = {0.8f, 0.8f, 0.8f, 1.0f,};
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globamb,0); // Global ambient light.
        

        	
        
        sunlightPos = myTerrain.getSunlight();       
        float temp = sunlightPos[0];
        sunlightPos[0] = sunlightPos[2];
		sunlightPos[2] = temp;
        sunlightAngle = 0;
        
        // normalise normals (!)
        // this is necessary to make lighting work properly
        gl.glEnable(GL2.GL_NORMALIZE);
        
        //gl.glEnable(GL2.GL_CULL_FACE);
        //gl.glCullFace(GL2.GL_BACK);
        
        //enable textures
        gl.glEnable(GL2.GL_TEXTURE_2D);
        
        //initialise textures
        myTextures = new LevelTexture[7];
        myTextures[0] = new LevelTexture(gl, grassTexture, grassTextureExt, true);
        myTextures[1] = new LevelTexture(gl, leafTexture, leafTextureExt, true);
        myTextures[2] = new LevelTexture(gl, trunkTexture, trunkTextureExt, true);
        myTextures[3] = new LevelTexture(gl, rainTexture, rainTextureExt, false);
        myTextures[4] = new LevelTexture(gl, roadTexture, roadTextureExt, true);
        myTextures[5] = new LevelTexture(gl, bluePortalTexture, bluePortalTextureExt, true);
        myTextures[6] = new LevelTexture(gl, orangePortalTexture, orangePortalTextureExt, true);
        
        //initialise VBO (player avatar and enemies)
        creeperHead = new EnemyObject(gl, creeperTexture, creeperTextureExt);
        playerHead = new PlayerObject(gl, playerTexture, playerTextureExt);
        //init rain particles
        for (int i = 0; i < rainParticles.length; i++) {
            rainParticles[i] = new RainParticle();
        }
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
        
        myTextures[0] = new LevelTexture(gl, grassTexture, grassTextureExt, true);
        myTextures[1] = new LevelTexture(gl, leafTexture, leafTextureExt, true);
        myTextures[2] = new LevelTexture(gl, trunkTexture, trunkTextureExt, true);
        myTextures[4] = new LevelTexture(gl, roadTexture, roadTextureExt, true);
        myTextures[5] = new LevelTexture(gl, bluePortalTexture, bluePortalTextureExt, true);
        myTextures[6] = new LevelTexture(gl, orangePortalTexture, orangePortalTextureExt, true);
        
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch (e.getKeyCode()) {
		  
            case KeyEvent.VK_UP:
                
                if(momentum < 0.08) {
                    momentum += 0.03;
                } else {
                    momentum += 0.01;
                }
                break;
            case KeyEvent.VK_DOWN:
            	
                if (momentum > 0) {
                    momentum = 0;
                } else {
                    momentum -= 0.01;
                }
                break;	
            case KeyEvent.VK_LEFT:
                   
                angleY = (angleY - 2) % 360;
                break;
            case KeyEvent.VK_RIGHT:
                 
                angleY = (angleY + 2) % 360;
                break;
             
            case KeyEvent.VK_W:
                
                if(momentum < 0.08) {
                	momentum += 0.03;
                } else {
                	momentum += 0.01;
                }
                break;
                
            case KeyEvent.VK_S:
                
                if (momentum > 0) {
            		momentum = 0;
                } else {
                	momentum -= 0.01;
                }
                break;
                
            case KeyEvent.VK_A:
            	//posX -= 0.1;
            	angleY = (angleY - 4) % 360;
                //momentumX -= 0.2;
                break;
            case KeyEvent.VK_D:
            	//posX += 0.1;
            	angleY = (angleY + 4) % 360;
                //momentumX += 0.2;
                break;
                
            case KeyEvent.VK_Q:
                cameraAngle += 2;
                //scale -= 0.1;
                break;
            case KeyEvent.VK_E:
                cameraAngle -= 2;
                //scale += 0.1;
                break;
            
            case KeyEvent.VK_F:
                if (freeLookEnabled) {
                    freeLookEnabled = false;
                } else {
                    freeLookEnabled = true;
                }
                break;
            case KeyEvent.VK_C:
                if (firstPersonEnabled) {
                    firstPersonEnabled = false;
                } else {
                    firstPersonEnabled = true;
                }
                break;
            case KeyEvent.VK_T:
                if (enabledRain) {
                    enabledRain = false;
                } else {
                    enabledRain = true;
                    enabledBurst = true;
                }
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
	
	/**
     * Multiply a vector by a matrix
     * 
     * @param m A 3x3 matrix
     * @param v A 3x1 vector
     * @return
     */
    public static double[] multiply(double[][] m, double[] v) {

        double[] u = new double[3];

        for (int i = 0; i < 3; i++) {
            u[i] = 0;
            for (int j = 0; j < 3; j++) {
                u[i] += m[i][j] * v[j];
            }
        }

        return u;
    }
	
	
}

/* display terrain
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
*/
