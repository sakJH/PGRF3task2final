#version 330
in vec2 inPosition;
in vec2 inTextureCoord;
out vec2 texCoords;

uniform sampler2D textureBase;

void main() {
    vec2 position = inPosition;
    gl_Position = vec4(position.x, position.y, 0,1);
    texCoords = inTextureCoord;
}
