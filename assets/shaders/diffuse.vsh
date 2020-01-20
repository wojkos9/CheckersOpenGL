#version 410 core
layout (location = 0) in vec3 a_pos;
layout (location = 1) in vec3 a_normal;

uniform vec4 u_col;
uniform mat4 u_m;
uniform mat4 mvp;

out vec4 col;
out vec3 FragPos;
out vec3 normal;

void main()
{
    gl_Position = mvp * vec4(a_pos, 1.0);
    col = u_col;
    FragPos = vec3(u_m * vec4(a_pos, 1.0));
    normal = a_normal;
}