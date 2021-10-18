#version 330

out vec4 fragColor;
in vec3 exColour;
void main()
{
    fragColor = vec4(exColour, 1.0);
}