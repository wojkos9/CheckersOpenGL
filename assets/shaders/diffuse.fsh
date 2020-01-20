#version 410 core

in vec4 col;
in vec3 FragPos;
in vec3 normal;

out vec4 FragColor;

uniform vec3 u_lightPos;
uniform float u_ambient;


void main()
{
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(u_lightPos - FragPos);
    float diffuse = max(dot(norm, lightDir), 0.0);
    vec3 col1 = (u_ambient + diffuse) * vec3(col);
    FragColor = vec4(col1, col.a);
}