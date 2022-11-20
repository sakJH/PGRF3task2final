import lwjglutils.*;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer {
    private int shaderProgram;
    private int loc_Unknown;
    private boolean loadImg;
    OGLTexture2D texture, tempTexture;
    private double ox, oy;


    @Override
    public void display() {
        renderMain();
    }



    @Override
    public void init() {
        glEnable(GL_DEPTH_TEST);
        //Načítání shaderů
        shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        //Uniform reference
        loc_Unknown = glGetUniformLocation(shaderProgram, "u_Unknown");


    }

    private void renderMain(){




        if (loadImg) {
            tempTexture = FileLoader.loadIMG();
            if (tempTexture != null) {
                texture = tempTexture;
            }
            loadImg = false;
        }
    }

}


