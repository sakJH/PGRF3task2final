#version 330

out vec4 outColor;

uniform sampler2D texture;
uniform sampler2D secondTexture;

uniform int u_HdrMode;
uniform float u_PosX, u_PosY;
uniform float u_Width, u_Height;
uniform float u_Exposure;
uniform float u_Gamma;
uniform int u_Brightness;
uniform int u_InvertColor;
uniform int u_GreyFilter;
uniform int u_Solarise;

in vec2 texCoords;

// Reinhard
vec3 reinhard(vec3 v){ return v / (1.0f + v); }

// Reinhard Extended
vec3 reinhard_extended(vec3 v, float max_white)
{
    vec3 numerator = v * (1.0f + (v / vec3(max_white * max_white)));
    return (numerator / (1.0f + v));
}

// Luminance
float luminance(vec3 v){
    return dot(v, vec3(0.2126f, 0.7152f, 0.0722f)); }
vec3 change_luminance(vec3 c_in, float l_out)
{
    float l_in = luminance(c_in);
    return c_in * (l_out / l_in);
}

// Reinhard_extended_luminance
vec3 reinhard_extended_luminance(vec3 v, float max_white_l)
{
    float l_old = luminance(v);
    float numerator = l_old * (1.0f + (l_old / (max_white_l * max_white_l)));
    float l_new = numerator / (1.0f + l_old);
    return change_luminance(v, l_new);
}
/*
// Reinhard-Jodie
vec3 reinhard_jodie(vec3 v)
{
    float l = luminance(v);
    vec3 tv = v / (1.0f + v);
    return lerp(v / (1.0f + l), tv, tv);
}*/  //lepr dělá problémy OpenGL

//Uncharted 2
vec3 uncharted2_tonemap_partial(vec3 x)
{
    float A = 0.15f;
    float B = 0.50f;
    float C = 0.10f;
    float D = 0.20f;
    float E = 0.02f;
    float F = 0.30f;
    return ((x*(A*x+C*B)+D*E)/(x*(A*x+B)+D*F))-E/F;
}
vec3 uncharted2_filmic(vec3 v)
{
    float exposure_bias = 2.0f;
    vec3 curr = uncharted2_tonemap_partial(v * exposure_bias);

    vec3 W = vec3(11.2f);
    vec3 white_scale = vec3(1.0f) / uncharted2_tonemap_partial(W);
    return curr * white_scale;
}

// Filtry
float overflowControl(float value)
{
    if (value < 0) {value = 0;}
    if (value > 255) {value = 255;}
    return value;
}

vec3 brightness(vec3 vec){
    float add = 50.;
    float newR = overflowControl(vec.r + add);
    float newG = overflowControl(vec.g + add);
    float newB = overflowControl(vec.b + add);
    return vec3(newR, newG, newB);
}

void main() {
    vec3 textureColor = texture2D(texture, texCoords).rgb;
    vec4 appliedHdr;

    if(u_HdrMode == 0)
    {
        vec3 result = pow(textureColor, vec3(1.0 / u_Gamma));
        appliedHdr = vec4(result, 1.0);
    }
    if(u_HdrMode == 1) // Reinhard
    {
        appliedHdr = vec4(reinhard(textureColor), 1.);
    }
    if (u_HdrMode == 2) // Reinhard Extended
    {
        appliedHdr = vec4(reinhard_extended(textureColor, 5), 1.);
    }
    if (u_HdrMode == 3) // reinhard_extended_luminance
    {
        vec3 resolut = reinhard_extended_luminance(textureColor,5000);
        appliedHdr = vec4(resolut, 1.);
    }
    if (u_HdrMode == 4) // Reinhard-Jodie
    {
        /*vec3 resolut = reinhard_jodie(textureColor);
        appliedHdr = vec4(resolut, 1.);*/
    }
    if (u_HdrMode == 5) // Uncharted 2
    {
        vec3 resolut = uncharted2_filmic(textureColor);
        appliedHdr = vec4(resolut, 1.);
    }


    //filtry

    float gammaCorrection = 1 / u_Gamma;

    float appliedHdrR = pow(255 * (appliedHdr.r / 255), gammaCorrection);
    float appliedHdrG = pow(255 * (appliedHdr.g / 255), gammaCorrection);
    float appliedHdrB = pow(255 * (appliedHdr.b / 255), gammaCorrection);


    if(u_Brightness == 0)
    {
        outColor = appliedHdr;
    }
    if(u_Brightness == 1)
    {
        float add = 0.1;
        float newR = overflowControl(appliedHdr.r + add);
        float newG = overflowControl(appliedHdr.g + add);
        float newB = overflowControl(appliedHdr.b + add);

        outColor = vec4(newR, newG, newB,1.);
    }
    if(u_Brightness == 2)
    {
        float add = 0.3;
        float newR = overflowControl(appliedHdr.r + add);
        float newG = overflowControl(appliedHdr.g + add);
        float newB = overflowControl(appliedHdr.b + add);

        outColor = vec4(newR, newG, newB,1.);
    }
    if(u_Brightness == 3)
    {
        float add = 0.5;
        float newR = overflowControl(appliedHdr.r + add);
        float newG = overflowControl(appliedHdr.g + add);
        float newB = overflowControl(appliedHdr.b + add);

        outColor = vec4(newR, newG, newB,1.);
    }


    if(u_InvertColor == 1)
    {

        float newR = 255 - appliedHdr.r;
        float newG = 255 - appliedHdr.g;
        float newB = 255 - appliedHdr.b;

        outColor = vec4(newR, newG, newB,1.);
    }

    if(u_GreyFilter == 1)
    {
        float i = (appliedHdr.r + appliedHdr.g + appliedHdr.b) / 3;
        outColor = vec4(i, i, i,1.);
    }

    if(u_Solarise == 1){
        int threshold = 128;
        float solariseRed, solariseGreen, solariseBlue;


        if(appliedHdr.r < threshold)
        {
            solariseRed = 255 - appliedHdr.r;
        }
        else {solariseRed = appliedHdr.r;}

        if(appliedHdr.g < threshold)
        {
            solariseGreen = 255 - appliedHdr.g;
        }
        else {solariseGreen = appliedHdr.r;}

        if(appliedHdr.b < threshold)
        {
            solariseBlue = 255 - appliedHdr.b;
        }
        else {solariseBlue = appliedHdr.b;}


        outColor = vec4(solariseRed, solariseGreen, solariseBlue,1.);
    }


}