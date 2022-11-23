#version 330
in vec2 inPosition;

out vec2 texCoords;

uniform sampler2D textureBase;

void main() {
    texCoords = inPosition;

    vec2 position = inPosition * 2 - 1;
    gl_Position = vec4(position.x, -position.y, 0., 1.);  //TODO vyřešit -y -> pro obrácení obrázku
}
