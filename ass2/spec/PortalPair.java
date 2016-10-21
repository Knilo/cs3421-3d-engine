package ass2.spec;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

public class PortalPair {
    double[] orangePortalPos;
    double[] bluePortalPos;
    
    public PortalPair (double[] orangePortalPos, double[] bluePortalPos) {
        if (orangePortalPos.length != 3 || bluePortalPos.length != 3) {
            throw new ArrayIndexOutOfBoundsException("Parameters must be arrays of length 3");
        }
        
        this.orangePortalPos = orangePortalPos;
        this.bluePortalPos = bluePortalPos;
    }
    
    public void draw (GL2 gl) {
        gl.glPushMatrix();
            GLU glu = new GLU();
            GLUquadric sphere = glu.gluNewQuadric();
            
            gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
            glu.gluQuadricTexture(sphere, true);
            
            gl.glPushMatrix();
                gl.glColor3f(255, 128, 0);
                gl.glTranslated(orangePortalPos[0], orangePortalPos[1], orangePortalPos[2]);
                gl.glScaled(0.2, 0.2, 0.2);
                
                gl.glBegin(GL.GL_TRIANGLE_FAN);
                    float segment = 45f;
                    gl.glVertex3f(0, 0, 0);
                    for (float angle = 0.0f; angle < 2.0f * Math.PI; angle += (2.0f*Math.PI/segment)) {
                        double x = Math.cos(angle) * 1;
                        double y = Math.sin(angle) * 2;
                        gl.glVertex3d(x, y, 0);
                    }
                gl.glEnd();
                
            gl.glPopMatrix();
            
            gl.glPushMatrix();
                gl.glColor3f(50, 50, 255);
                gl.glTranslated(bluePortalPos[0], bluePortalPos[1], bluePortalPos[2]);
                gl.glScaled(0.2, 0.2, 0.2);
                glu.gluSphere(sphere, 4, 10, 10);
                
            gl.glPopMatrix();
        gl.glPopMatrix();
        
    }
}
