import lwjglutils.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import transforms.Mat4;
import transforms.Mat4PerspRH;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer {
    private int shaderHdr;
    private int loc_HdrMode, loc_PosX, loc_PosY, loc_Width, loc_Height;
    private boolean loadImg;
    OGLTexture2D texture, tempTexture, secondTexture;
    private double posX, posY;
    int hdrMode = 0;
    Mat4 projection;
    private OGLBuffers buffers;

    int width_ = 800, height_ = 600;

    @Override
    public void display() {
        renderMain();
    }



    @Override
    public void init() {
        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();
        glClearColor(0.7f, 0.7f, 0.7f, 1.0f);

        textRenderer = new OGLTextRenderer(width_, height_);

        //projection = new Mat4PerspRH(Math.PI / 3, Main.getHeight() / (float) Main.getWidth(), 0.1f, 50.f);

        projection = new Mat4PerspRH(Math.PI / 4, 1, 0.1, 100.0);

        try { texture = new OGLTexture2D("img/city.jpg"); } catch (IOException e) { e.printStackTrace(); }

        try { secondTexture = new OGLTexture2D("img/city.jpg"); } catch (IOException e) { e.printStackTrace(); }

        //Načítání shaderů
        shaderHdr = ShaderUtils.loadProgram("/shaders/Hdr");
        //Uniform reference
        loc_HdrMode = glGetUniformLocation(shaderHdr, "u_HdrMode");
        loc_PosX = glGetUniformLocation(shaderHdr, "u_PosX");
        loc_PosY = glGetUniformLocation(shaderHdr, "u_PosY");
        loc_Width = glGetUniformLocation(shaderHdr, "u_Width");
        loc_Height = glGetUniformLocation(shaderHdr, "u_Height");

    }

    private void renderMain(){
        glUseProgram(shaderHdr);
        glEnable(GL_DEPTH_TEST);

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width_, height_);
        glClearColor(0.3f, 0.3f, 0.3f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        texture.bind(shaderHdr, "texture", 0);


        glUniform1f(loc_HdrMode,hdrMode);
        glUniform1f(loc_PosX, (float) posX);
        glUniform1f(loc_PosY, (float) posY);
        glUniform1f(loc_Width, (float) width_);
        glUniform1f(loc_Height, (float) height_);

        buffers.draw(GL_TRIANGLES, shaderHdr);


        if (loadImg) {
            tempTexture = FileLoader.loadIMG();
            if (tempTexture != null) {
                texture = tempTexture;
            }
            loadImg = false;
        }
    }

    private final GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0 &&
                    (w != width_ || h != height_)) {
                width_ = w;
                height_ = h;
                projection = new Mat4PerspRH(Math.PI / 4, height_ / (double) width_, 0.1, 100.0);
                if (textRenderer != null)
                    textRenderer.resize(width_, height_);
            }
        }
    };

    private final GLFWCursorPosCallback cpCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            posX = x;
            posY = y;
        }
    };



    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_1 -> { }

                    case GLFW_KEY_Q -> loadImg = !loadImg;

                }
            }
        }
    };

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWWindowSizeCallback getWsCallback() {
        return wsCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallback;
    }

}



