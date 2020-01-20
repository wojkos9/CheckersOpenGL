#version 410 core
layout (location = 0) in vec3 a_pos; // the position variable has attribute position 0

uniform vec4 u_col;
uniform mat4 mvp;

out vec4 col;

void main()
{
    gl_Position = mvp * vec4(a_pos, 1.0); // see how we directly give a vec3 to vec4's constructor
    col = u_col;
}