package ass2.spec;

import com.jogamp.opengl.GL2;

public class CuboidObject {
    public static void drawCuboid (GL2 gl, double height, double width, int textureId) {
        gl.glPushMatrix();
            gl.glTranslated(-width/2, 0, width/2); //centre the cuboid
            gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId);
            gl.glBegin(GL2.GL_QUADS);
            // front   
            
            gl.glNormal3d(0,0,1);
            
            gl.glTexCoord2d(0.0, 0.0);
            gl.glVertex3d(0, 0, 0); 
            gl.glTexCoord2d(1.0, 0.0);
            gl.glVertex3d(width, 0, 0); 
            gl.glTexCoord2d(1.0, 1.0);
            gl.glVertex3d(width, height, 0);
            gl.glTexCoord2d(0.0, 1.0);
            gl.glVertex3d(0, height, 0); 
            // back 
            
            gl.glColor3f(1, 1, 0); 
            gl.glNormal3d(0,0,-1);
            
            gl.glTexCoord2d(1.0, 0.0);
            gl.glVertex3d(0, 0, -width);
            gl.glTexCoord2d(1.0, 1.0);
            gl.glVertex3d(0, height, -width); 
            gl.glTexCoord2d(0.0, 1.0);
            gl.glVertex3d(width, height, -width);    
            gl.glTexCoord2d(0.0, 0.0);
            gl.glVertex3d(width, 0, -width); 
            
            
            // top
            gl.glColor3f(1, 0, 0);
            gl.glNormal3d(0,1,0);
            
            gl.glTexCoord2d(1.0, 0.0);
            gl.glVertex3d(0, height, 0); 
            gl.glTexCoord2d(1.0, 1.0);
            gl.glVertex3d(width, height, 0); 
            gl.glTexCoord2d(0.0, 1.0);
            gl.glVertex3d(width, height, -width);  
            gl.glTexCoord2d(0.0, 0.0);
            gl.glVertex3d(0, height, -width);  
            
            // bottom  
            gl.glColor3f(0, 1, 0); 
            gl.glNormal3d(0,-1,0);
            
            gl.glTexCoord2d(0.0, 0.0);
            gl.glVertex3d(0, 0, 0);
            gl.glTexCoord2d(1.0, 0.0);
            gl.glVertex3d(0, 0, -width); 
            gl.glTexCoord2d(1.0, 1.0);
            gl.glVertex3d(width, 0, -width);    
            gl.glTexCoord2d(0.0, 1.0);
            gl.glVertex3d(width, 0, 0); 
            
            //left
            gl.glColor3f(0, 1, 1); 
            gl.glNormal3d(-1,0,0);
            
            gl.glTexCoord2d(1.0, 0.0);
            gl.glVertex3d(0, height, -width);
            gl.glTexCoord2d(1.0, 1.0);
            gl.glVertex3d(0, 0, -width);
            gl.glTexCoord2d(0.0, 1.0);
            gl.glVertex3d(0, 0, 0);
            gl.glTexCoord2d(0.0, 0.0);
            gl.glVertex3d(0, height, 0);
            
            //right
            gl.glColor3f(0, 0, 1); 
            gl.glNormal3d(1,0,0);
            
            gl.glTexCoord2d(1.0, 0.0);
            gl.glVertex3d(width, 0, -width);
            gl.glTexCoord2d(1.0, 1.0);
            gl.glVertex3d(width, height, -width);
            gl.glTexCoord2d(0.0, 1.0);
            gl.glVertex3d(width, height, 0);
            gl.glTexCoord2d(0.0, 0.0);
            gl.glVertex3d(width, 0, 0);
            
            gl.glEnd();
        gl.glPopMatrix();
    }
}
