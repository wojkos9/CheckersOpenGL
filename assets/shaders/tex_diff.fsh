#version 410 core

in vec2 uv;
in vec3 FragPos;
in vec3 normal;

out vec4 FragColor;

uniform vec3 u_lightPos;
uniform float u_ambient;
uniform sampler2D sampler;

void main()
{
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(u_lightPos - FragPos);
    float diffuse = max(dot(norm, lightDir), 0.0);
    vec3 col1 = (u_ambient + diffuse) * texture(sampler, uv).rgb;
    FragColor = vec4(col1, 1.0);
}