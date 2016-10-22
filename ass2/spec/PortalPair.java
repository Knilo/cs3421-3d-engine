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
    
    public void draw (GL2 gl, LevelTexture blueTex, LevelTexture orangeTex) {
        double portalWidth = 0.75;
        double portalHeight = 1.5;
        float segment = 60;
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_DST_ALPHA);
        gl.glPushMatrix();
            GLU glu = new GLU();
            GLUquadric sphere = glu.gluNewQuadric();
            
            gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
            glu.gluQuadricTexture(sphere, true);
            
            gl.glPushMatrix();
                gl.glTranslated(orangePortalPos[0], orangePortalPos[1], orangePortalPos[2]);
                gl.glScaled(0.2, 0.2, 0.2);
                
                
                //render portal
                gl.glTranslated(0, portalHeight, 0);
                gl.glBindTexture(GL.GL_TEXTURE_2D, orangeTex.getTextureId());
                gl.glBegin(GL.GL_TRIANGLE_FAN);
                    
                    gl.glVertex3f(0, 0, 0);
                    for (float angle = 0.0f; angle <= 2.0f * Math.PI; angle += (2.0f*Math.PI/segment)) {
                        double x = Math.cos(angle);
                        double y = Math.sin(angle);
                        gl.glTexCoord2d(0.5 + 0.5*x, 0.5 + 0.5*y);
                        gl.glVertex3d(x * portalWidth, y * portalHeight, 0);
                    }
                gl.glEnd();
                
            gl.glPopMatrix();
            
            gl.glPushMatrix();
                gl.glTranslated(bluePortalPos[0], bluePortalPos[1], bluePortalPos[2]);
                gl.glScaled(0.2, 0.2, 0.2);
                gl.glEnable(GL2.GL_BLEND);
                //render portal
                gl.glTranslated(0, portalHeight, 0);
                gl.glBindTexture(GL.GL_TEXTURE_2D, blueTex.getTextureId());
                gl.glBegin(GL.GL_TRIANGLE_FAN);
                    gl.glVertex3f(0, 0, 0);
                    for (float angle = 0.0f; angle <= 2.0f * Math.PI; angle += (2.0f*Math.PI/segment)) {
                        double x = Math.cos(angle);
                        double y = Math.sin(angle);
                        double texX = 0.5 + 0.5*x;
                        double texY = 0.5 + 0.5*y;
                        if (texX > 1 || texX < 0) {
                            System.out.println("TEX X BROKE");
                        }
                        if (texY > 1 || texY < 0) {
                            System.out.println("TEX Y BROKE");
                        }
                        gl.glTexCoord2d(texX, texY);
                        gl.glVertex3d(x * portalWidth, y * portalHeight, 0);
                    }
                gl.glEnd();
                
            gl.glPopMatrix();
        gl.glPopMatrix();
        gl.glDisable(GL2.GL_BLEND);
    }
}
