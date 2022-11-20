import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer {
    private int shaderProgram, shaderPost;
    private Camera camera;
    private OGLTexture2D textureBase, textureNormal , texHeight; private OGLRenderTarget renderTarget;
    private double ox, oy, camSpeed = 0.25;
    float timeChange = 0, secondObjPosX, secondObjPosY, spotCutOff;
    int loc_uProj, loc_uView, loc_uSelectedModel, loc_lightMode, loc_uModel, loc_secondObj, loc_time, loc_EyePosition, loc_SpotCutOff, loc_ConstantAttenuation, loc_LinearAttenuation, loc_QuadraticAttenuation, loc_mappingMode;
    private OGLBuffers buffers, buffersPost;
    private boolean gridModeList = true, leftMouse, rightMouse, middleMouse, mousePressed = false;
    private int gridM = 20; private int gridN = 20; private int  gridMpost = 2; private int  gridNpost = 2, lightModeValue = 0, selectedModel = 0, secondObjModel = 0, polygonModeNumber = 0, mappingModeNumber = 0;
    Mat4 model, projection, rotation, translation, scale, secondObjMove;
    private Vec3D secondObjPos, eyePos;

    @Override
    public void init() {

        poygonMode(polygonModeNumber);

        glEnable(GL_DEPTH_TEST);

        buffers = Grid.gridListTriangle(gridM, gridN);

        buffersPost = Grid.gridListTriangle(gridM, gridN);

        camera = new Camera()
                .withPosition(new Vec3D(0.f, 0f, 0f))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125)
                .withFirstPerson(false)
                .withRadius(3);
        projection = new Mat4PerspRH(Math.PI / 3, Main.getHeight() / (float) Main.getWidth(), 0.1f, 50.f);


        shaderProgram = ShaderUtils.loadProgram("/shaders/Basic");
        shaderPost = ShaderUtils.loadProgram("/shaders/Post");


        // Proj
        loc_uProj = glGetUniformLocation(shaderProgram, "u_Proj");
        loc_uModel = glGetUniformLocation(shaderProgram, "u_Model");
        loc_uView = glGetUniformLocation(shaderProgram, "u_View");
        loc_lightMode = glGetUniformLocation(shaderProgram, "u_LightMode");  //colorType -> u_LightMode
        loc_uSelectedModel = glGetUniformLocation(shaderProgram, "u_SelectedModel");  // type -> selectedModel
        loc_EyePosition = glGetUniformLocation(shaderProgram, "u_EyePos");

        //Utlum
        loc_ConstantAttenuation = glGetUniformLocation(shaderProgram, "constantAttenuation");
        loc_LinearAttenuation = glGetUniformLocation(shaderProgram, "linearAttenuation");
        loc_QuadraticAttenuation = glGetUniformLocation(shaderProgram, "quadraticAttenuation");

        //Second Obj
        loc_SpotCutOff = glGetUniformLocation(shaderProgram, "u_spotCutOff");
        loc_secondObj = glGetUniformLocation(shaderProgram, "u_secondObj");
        loc_time = glGetUniformLocation(shaderProgram, "u_time");

        //Mapping
        loc_mappingMode = glGetUniformLocation(shaderProgram, "u_mappingMode");

        spotCutOff = 0.90f;
        eyePos = camera.getEye();

        //renderTarget = new OGLRenderTarget(800, 600);

        try {
            textureBase = new OGLTexture2D("./textures/hypnotic.jpg");
            textureNormal = new OGLTexture2D("./textures/bricksn.png");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        model = new Mat4Identity();
        rotation = new Mat4Identity();
        translation = new Mat4Identity();
        scale = new Mat4Identity();

        secondObjPos = new Vec3D(5,5,5);
        secondObjMove = new Mat4Transl(secondObjPos);
    }

    @Override
    public void display() {
        renderMain();

        //renderPost();
    }

    private void renderMain(){

        poygonMode(polygonModeNumber);

        glUseProgram(shaderProgram);

        //renderTarget.bind();

        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());
        eyePos = camera.getEye();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());

        glUniformMatrix3fv(loc_secondObj, false, ToFloatArray.convert(secondObjMove));

        glUniform3fv(loc_EyePosition, ToFloatArray.convert(eyePos));

        glUniform1f(loc_SpotCutOff, spotCutOff);

        //Utlum
        glUniform1f(loc_ConstantAttenuation, 0);
        glUniform1f(loc_LinearAttenuation, 0);
        glUniform1f(loc_QuadraticAttenuation, 0.02f);

        //Model
        glUniformMatrix4fv(loc_uModel, false, ToFloatArray.convert(model));


        glUniform1i(loc_uSelectedModel, selectedModel);
        glUniform1i(loc_lightMode, lightModeValue);

        textureBase.bind(shaderProgram, "textureBase", 0);
        textureNormal.bind(shaderProgram, "textureNormal", 1);
        textureNormal.bind(shaderProgram, "texHeight", 2);

        //Mapping
        glUniform1f(loc_mappingMode, mappingModeNumber);

        buffersMode(buffers, shaderProgram);

        secondObjPosX = (float) (2 * Math.sin(-timeChange));
        secondObjPosY = (float) (2 * Math.cos(-timeChange));

        secondObjPos = new Vec3D(secondObjPosX, secondObjPosY, 1);
        secondObjMove = new Mat4Transl(secondObjPos);

        //Second obj
        timeChange += 0.01;
        glUniform1f(loc_time, timeChange);
        glUniform1i(loc_uSelectedModel, 7);
        glUniform1i(loc_lightMode, 10);
        glUniformMatrix4fv(loc_uModel, false, ToFloatArray.convert(secondObjMove));

        buffersMode(buffers, shaderProgram);
    }

    private void renderPost(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glUseProgram(shaderPost);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, Main.getWidth(), Main.getHeight());
        //renderTarget.getColorTexture().bind(shaderProgramPost, "textureBase", 0);

        buffersMode(buffersPost, shaderPost);
    }

    private final GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            double dx = (ox - x);
            double dy = (oy - y);
            if (leftMouse) {
                //Pohyb s kamerou
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / Main.getWidth())
                        .addZenith((double) Math.PI * (oy - y) / Main.getWidth());
            }
            else if (rightMouse) {
                //Rotace
                double rotX = model.get(3, 0);
                double rotY = model.get(3, 1);
                double rotZ = model.get(3, 2);

                if (rotX == 0 && rotY == 0 && rotZ == 0) {
                    model = model.mul(new Mat4RotXYZ(0, Math.PI * (dy) / Main.getHeight(), -(Math.PI * (dx) / Main.getWidth())));
                }
                else
                {
                    model = model.mul(new Mat4Transl(-rotX, -rotY, -rotZ));
                    model = model.mul(new Mat4RotXYZ(0, Math.PI * (dy) / Main.getHeight(), -(Math.PI * (dx) / Main.getWidth())));
                    model = model.mul(new Mat4Transl(rotX, rotY, rotZ));
                }
            }
            else if (middleMouse) {
                //Translace
                double trX = (ox - x) / 50;
                double trY = (oy - y) / 50;

                model = model.mul(new Mat4Transl(0, trX, trY));
            }
            ox = x;
            oy = y;
        }
    };

    private final GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (button == GLFW_MOUSE_BUTTON_LEFT || button == GLFW_MOUSE_BUTTON_RIGHT) {
                double[] xBuffer = new double[1];
                double[] yBuffer = new double[1];
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer[0];
                oy = yBuffer[0];
            }
            if (button == GLFW_MOUSE_BUTTON_LEFT) leftMouse = (action == GLFW_PRESS);
            else if (button == GLFW_MOUSE_BUTTON_RIGHT) rightMouse = (action == GLFW_PRESS);
            else if (button == GLFW_MOUSE_BUTTON_MIDDLE) middleMouse = (action == GLFW_PRESS);
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            if (dy < 0)
                camera = camera.mulRadius(1.1f);
            else
                camera = camera.mulRadius(0.9f);
        }
    };

    protected GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_W -> { camera = camera.forward(camSpeed); }
                    case GLFW_KEY_S -> { camera = camera.backward(camSpeed); }
                    case GLFW_KEY_A -> { camera = camera.left(camSpeed); }
                    case GLFW_KEY_D -> { camera = camera.right(camSpeed); }
                    //Up, Down
                    case GLFW_KEY_LEFT_SHIFT -> { camera = camera.up(camSpeed); }
                    case GLFW_KEY_LEFT_CONTROL -> { camera = camera.down(camSpeed); }
                    //Reset
                    case GLFW_KEY_R -> {
                        model = new Mat4Identity();
                        rotation = new Mat4Identity();
                        translation = new Mat4Identity();

                        camera = new Camera()
                                .withPosition(new Vec3D(0.f, 0f, 0f))
                                .withAzimuth(Math.PI * 1.25)
                                .withZenith(Math.PI * -0.125)
                                .withFirstPerson(false)
                                .withRadius(3);
                        projection = new Mat4PerspRH(Math.PI / 3, Main.getHeight() / (float) Main.getWidth(), 0.1f, 50.f);
                    }
                    // Perspektivní a ortogonální projekce
                    case GLFW_KEY_P -> { projection = new Mat4PerspRH(Math.PI / 3, Main.getHeight() / (float) Main.getWidth(), 0.1f, 50.f); }
                    case GLFW_KEY_O -> { projection = new Mat4OrthoRH(2.3, 2.3, 0.1, 20); }
                    //Scale
                    case GLFW_KEY_Z -> { projection = projection.mul(new Mat4Scale(0.9,0.9,0.9)); }
                    case GLFW_KEY_X -> { projection = projection.mul(new Mat4Scale(1.1,1.1,1.1)); }
                    //List / Strip
                    case GLFW_KEY_I -> {
                        buffers = Grid.gridStripsTriangle(gridM, gridN);
                        gridModeList = false;
                        System.out.println("List grid mode");
                    }
                    case GLFW_KEY_U -> {
                        buffers = Grid.gridListTriangle(gridM, gridN);
                        gridModeList = true;
                        System.out.println("Strip grid mode");
                    }
                    //Osvětlovací model
                    case GLFW_KEY_L -> {
                        if (lightModeValue == 13 ) { lightModeValue = 0; System.out.println("L " + lightModeValue); }
                        else { lightModeValue++; System.out.println("L " + lightModeValue); }
                    }
                    //Objekty
                    case GLFW_KEY_M -> {
                        if (selectedModel >= 7) {selectedModel = 0 ;}
                        selectedModel++;
                        System.out.println("Object " + selectedModel);
                    }
                    //Osvětlení reflektorem
                    case GLFW_KEY_B -> { if (spotCutOff < 1.0) { spotCutOff += 0.02; } }
                    case GLFW_KEY_V -> { if (spotCutOff > 0.9) { spotCutOff -= 0.02; } }
                    //Polygon mode - Fill, Line, Point
                    case GLFW_KEY_C -> { polygonModeNumber++; if (polygonModeNumber == 3) { polygonModeNumber = 0;} }
                    //Mapping
                    case GLFW_KEY_K -> { mappingModeNumber++; System.out.println(mappingModeNumber); if (mappingModeNumber == 2) { mappingModeNumber = 0;} ;}
                }
            }
        }
    };

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    private void buffersMode(OGLBuffers buffers, int shader) {
        if (gridModeList) {
            buffers.draw(GL_TRIANGLES, shader);
        } else {
            buffers.draw(GL_TRIANGLE_STRIP, shader);
        }
    }

    private void poygonMode(int mode){
        if (mode == 0) { glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); }

        if (mode == 1) { glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); }

        if (mode == 2) { glPolygonMode(GL_FRONT_AND_BACK, GL_POINT); }

    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cursorPosCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mouseButtonCallback;
    }
}


