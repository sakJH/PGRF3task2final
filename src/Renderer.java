import lwjglutils.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import transforms.Mat4;
import transforms.Mat4PerspRH;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer {
    private int shaderHdr;
    private int loc_HdrMode, loc_PosX, loc_PosY, loc_Width, loc_Height, loc_Exposure, loc_Gamma, loc_Brightness, loc_InvertColor, loc_GreyFilter, loc_Solarise, loc_SolariseGrey;
    private boolean loadImg;
    OGLTexture2D texture, tempTexture, secondTexture, texture0, texture1, texture2, texture3, texture4, texture5;
    private double posX, posY;
    int hdrMode = 0, brightness = 0, invertColor = 0, greyFilter = 0, solarise = 0, solariseGrey = 0, imgMode = 0;
    Mat4 projection;
    private OGLBuffers buffers;

    int width_ = 800, height_ = 600;
    private float exposure = 1.f, gamma = 0.6f;

    @Override
    public void display() {
        renderMain();
        renderText();
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

        buffers = Quad.getQuad();

        try { texture0 = new OGLTexture2D("img/0.png"); } catch (IOException e) { e.printStackTrace(); }
        try { texture1 = new OGLTexture2D("img/1.jpg"); } catch (IOException e) { e.printStackTrace(); }
        try { texture2 = new OGLTexture2D("img/2.jpg"); } catch (IOException e) { e.printStackTrace(); }
        try { texture3 = new OGLTexture2D("img/3.jpg"); } catch (IOException e) { e.printStackTrace(); }
        try { texture4 = new OGLTexture2D("img/4.jpg"); } catch (IOException e) { e.printStackTrace(); }
        try { texture5 = new OGLTexture2D("img/5.jpg"); } catch (IOException e) { e.printStackTrace(); }

        texture = texture0;

        //Načítání shaderů
        shaderHdr = ShaderUtils.loadProgram("/shaders/Hdr");
        //Uniform reference
        loc_HdrMode = glGetUniformLocation(shaderHdr, "u_HdrMode");
        loc_PosX = glGetUniformLocation(shaderHdr, "u_PosX");
        loc_PosY = glGetUniformLocation(shaderHdr, "u_PosY");
        loc_Width = glGetUniformLocation(shaderHdr, "u_Width");
        loc_Height = glGetUniformLocation(shaderHdr, "u_Height");
        loc_Exposure = glGetUniformLocation(shaderHdr, "u_Exposure");
        loc_Gamma = glGetUniformLocation(shaderHdr, "u_Gamma");
        loc_Brightness = glGetUniformLocation(shaderHdr, "u_Brightness");
        loc_InvertColor = glGetUniformLocation(shaderHdr, "u_InvertColor");
        loc_GreyFilter = glGetUniformLocation(shaderHdr, "u_GreyFilter");
        loc_Solarise = glGetUniformLocation(shaderHdr, "u_Solarise");
        loc_SolariseGrey = glGetUniformLocation(shaderHdr, "u_SolariseGrey");

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

        glUniform1i(loc_HdrMode,hdrMode);
        glUniform1f(loc_PosX, (float) posX);
        glUniform1f(loc_PosY, (float) posY);
        glUniform1f(loc_Width, (float) width_);
        glUniform1f(loc_Height, (float) height_);
        glUniform1f(loc_Exposure, exposure);
        glUniform1f(loc_Gamma, gamma);
        glUniform1i(loc_Brightness, brightness);
        glUniform1i(loc_InvertColor, invertColor);
        glUniform1i(loc_GreyFilter, greyFilter);
        glUniform1i(loc_Solarise, solarise);
        glUniform1i(loc_SolariseGrey, solariseGrey);

        buffers.draw(GL_TRIANGLES, shaderHdr); //TODO naplnit buffer

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
                    case GLFW_KEY_1 -> { hdrMode++; if (hdrMode > 9) hdrMode = 0; System.out.println("HDR Mode " + hdrMode);} // Zobrazení HDR

                    case GLFW_KEY_Q -> { loadImg = !loadImg; } // Načtení jiného obrázku

                    case GLFW_KEY_2 -> { exposure += 0.5f; if (exposure > 3.f) exposure = 1.f; System.out.println("Exposure " + exposure);} //Změna expozice

                    case GLFW_KEY_3 -> { gamma += 0.3f; if (gamma > 3.f) gamma = 0.3f; System.out.println("Gamma " + gamma);} //Změna Gamma

                    case GLFW_KEY_4 -> { brightness++; if (brightness > 3) brightness = 0; System.out.println("Brightness Mode " + brightness);} // Zobrazení Jasu

                    case GLFW_KEY_W -> { invertColor++; if (invertColor > 1) invertColor = 0;} //Invertování (obrácení barev)

                    case GLFW_KEY_E -> { greyFilter++; if (greyFilter > 1) greyFilter = 0;} // Černobílý filter

                    case GLFW_KEY_R -> { solarise++; if (solarise > 1) solarise = 0;} // Solarizace (obrácení tónu od určité hranice (THRESHOLD))

                    case GLFW_KEY_T -> { solariseGrey++; if (solariseGrey > 1) solariseGrey = 0;} // Solarizace černobíleho filtru

                    case GLFW_KEY_A -> {
                        imgMode++;

                        if (imgMode > 5) imgMode = 0;
                        System.out.println(imgMode);

                        /*
                        texture = switch (imgMode) {
                            case 0: texture = texture0;
                            case 1: texture = texture1;
                            case 2: texture = texture2;
                            case 3: texture = texture3;
                            case 4: texture = texture4;
                            case 5: texture = texture5;
                            default: texture = texture0;
                        };
*/
                    }
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

    public void renderText(){
        String modeHRD = switch (hdrMode) {
            case 0 -> "No HDR";
            case 1 -> "Reinhard";
            case 2 -> "Reinhard Extended";
            case 3 -> "Reinhard Extended by Luminance";
            case 4 -> "Reinhard-Jodie";
            case 5 -> "Uncharted 2";
            case 6 -> "Linear Tone-Mapping";
            case 7 -> "White Preserving Luma Based Reinhard Tone-Mapping";
            case 8 -> "RomBinDa House ToneMapping";
            case 9 -> "Filmic Tone-Mapping";
            default -> "default";
        };

        String modeBrightness = switch (brightness) {
            case 0 -> "Origin";
            case 1 -> "+ 0.1";
            case 2 -> "+ 0.3";
            case 3 -> "+ 0.5";
            default -> "default";
        };

        String modeInvertColor = switch (invertColor) {
            case 0 -> "No";
            case 1 -> "Yes";
            default -> "default";
        };

        String modeGreyFilter = switch (greyFilter) {
            case 0 -> "No";
            case 1 -> "Yes";
            default -> "default";
        };

        String modeSolarise = switch (solarise) {
            case 0 -> "No";
            case 1 -> "Yes";
            default -> "default";
        };

        String modeSolariseGrey = switch (solariseGrey) {
            case 0 -> "No";
            case 1 -> "Yes";
            default -> "default";
        };

        DecimalFormat ft = new DecimalFormat("#.##");

        String textHdr = "[1] HDR mode: " + modeHRD;
        String textExposure = "[2] Exposure value: " + exposure;
        String textGama = "[3] Gamma correction value: " + ft.format(gamma);
        String textBrightness = "[4] Brightness value: " + modeBrightness;
        String textNewImage = "[Q] New Image";
        String textInvertColor = "[W] InvertColor: " + modeInvertColor;
        String textGreyFilter = "[E] Grey Filter: " + modeGreyFilter;
        String textSolarise = "[R] Solarise: " + modeSolarise;
        String textSolariseGrey = "[T] Solarise Grey: " + modeSolariseGrey;
        String textSwitchImage = "[A] Switch Image";

        textRenderer.setBackgroundColor(new Color(255,255,255));
        textRenderer.setColor(new Color(0, 0, 0));

        textRenderer.addStr2D(3, 20, textHdr);
        textRenderer.addStr2D(3, 35, textExposure);
        textRenderer.addStr2D(3, 50, textGama);
        textRenderer.addStr2D(3, 65, textBrightness);
        textRenderer.addStr2D(3, 80, textNewImage);
        textRenderer.addStr2D(3, 95, textInvertColor);
        textRenderer.addStr2D(3, 110, textGreyFilter);
        textRenderer.addStr2D(3, 125, textSolarise);
        textRenderer.addStr2D(3, 140, textSolariseGrey);
        textRenderer.addStr2D(3, 155, textSwitchImage);

    }
    
    private void textureMode(int mode){

        if (mode == 1) { texture = texture1;}
        if (mode == 2) { texture = texture2;}
        if (mode == 3) { texture = texture3;}
        if (mode == 4) { texture = texture4;}
        if (mode == 5) { texture = texture5;}
        if (mode == 0) { texture = texture0;}



    }

}



