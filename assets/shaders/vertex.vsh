#version 410 core
layout (location = 0) in vec3 aPos; // the position variable has attribute position 0
layout (location = 1) in vec2 aUV;

uniform mat4 mvp;

out vec2 uv; // specify a color output to the fragment shader

void main()
{
    gl_Position = mvp * vec4(aPos, 1.0); // see how we directly give a vec3 to vec4's constructor
    uv = aUV; // set the output variable to a dark-red color
}