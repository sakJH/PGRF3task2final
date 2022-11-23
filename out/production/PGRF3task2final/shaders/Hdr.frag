#version 330

out vec4 outColor;

uniform sampler2D texture;
uniform sampler2D secondTexture;

uniform int u_HdrMode;
uniform float u_PosX, u_PosY;
uniform float u_Width, u_Height;

in vec2 texCoords;

void main() {
    vec3 textureColor = texture2D(texture, texCoords).rgb;

    outColor = vec4(textureColor, 1.0);

}