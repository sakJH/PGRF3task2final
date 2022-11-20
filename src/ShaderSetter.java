import lwjglutils.OGLBuffers;
import lwjglutils.OGLRenderTarget;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import transforms.Camera;
import transforms.Mat4;
import transforms.Vec3D;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class ShaderSetter {

    protected void loadShaderProgram(int shaderProgram, String path){
        shaderProgram = ShaderUtils.loadProgram(path);
    }

    protected int getUniform(int appReference, int shaderProgram, String shaderReference){
        return appReference = glGetUniformLocation(shaderProgram, shaderReference);
    }



}
