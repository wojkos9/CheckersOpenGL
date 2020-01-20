#version 410 core

in vec2 uv;

uniform sampler2D sampler;

out vec4 FragColor;


void main()
{
    FragColor = vec4(texture(sampler, uv).rgb, 1.0);
}