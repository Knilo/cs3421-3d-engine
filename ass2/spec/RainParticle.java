package ass2.spec;

import java.util.Random;


//Rain particle adapted from particle sytem in week 9 lecture code
public class RainParticle {
    boolean active; // always active in this program
    float life;     // how alive it is
    float r, g, b;  // color
    float x, y, z;  // position
    float originalY = 0;
    float speedX, speedY, speedZ; // speed in the direction

    private final float[][] colors = {    // rainbow of 12 colors
          { 1.0f, 0.5f, 0.5f }, { 1.0f, 0.75f, 0.5f }, { 1.0f, 1.0f, 0.5f },
          { 0.75f, 1.0f, 0.5f }, { 0.5f, 1.0f, 0.5f }, { 0.5f, 1.0f, 0.75f },
          { 0.5f, 1.0f, 1.0f }, { 0.5f, 0.75f, 1.0f }, { 0.5f, 0.5f, 1.0f },
          { 0.75f, 0.5f, 1.0f }, { 1.0f, 0.5f, 1.0f }, { 1.0f, 0.5f, 0.75f } };

    private Random rand = new Random();

    // Constructor
    public RainParticle() {
       active = true;
       burst();
    }

    public void burst() {
       // Set the initial position
       x = z = 0.0f;
       originalY = rand.nextFloat() * 10;
       y = originalY;
       
       // Generate a random speed and direction in polar coordinate, then resolve
       // them into x and y.
       float maxSpeed = 0.1f;
       float speed = 0.02f + (rand.nextFloat() - 0.5f) * maxSpeed; 
       float angle = (float)Math.toRadians(rand.nextInt(360));

       speedX = speed * (float)Math.cos(angle);
       speedY = 0;//speed * (float)Math.sin(angle) + 0.1f;
       speedZ = (rand.nextFloat() - 0.5f) * maxSpeed;

       int colorIndex = (int)(((speed - 0.02f) + maxSpeed) / (maxSpeed * 2) * colors.length) % colors.length;
       // Pick a random color
       r = colors[colorIndex][0];
       g = colors[colorIndex][1];
       b = colors[colorIndex][2];
       
       // Initially it's fully alive
       life = 1.0f;
    }
}
