#version 410 core
layout (location = 0) in vec3 a_pos;
layout (location = 1) in vec3 a_normal;
layout (location = 2) in vec2 aUV;

uniform mat4 u_m;
uniform mat4 mvp;

out vec2 uv;
out vec3 FragPos;
out vec3 normal;

void main()
{
    gl_Position = mvp * vec4(a_pos, 1.0);
    FragPos = vec3(u_m * vec4(a_pos, 1.0));
    normal = a_normal;
    uv = aUV;
}