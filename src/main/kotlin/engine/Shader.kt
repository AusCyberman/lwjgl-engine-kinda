package engine

import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30.*
import java.io.File

class Shader(val shaderType: Int, val resource_name: String) {
    internal val shaderId: Int = glCreateShader(shaderType)
        get

    class ShaderCompilationError(val shaderId: Int, resource_name: String) :
        Exception("Error during compilation of \"${resource_name}\": ${GL20.glGetShaderInfoLog(shaderId)}")

    init {
        val f = javaClass.classLoader.getResource("$resource_name")
            ?: throw Exception("Could not find resource $resource_name")
        var file = File(f.toURI())
        glShaderSource(shaderId, file.readText())
        glCompileShader(shaderId)
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw ShaderCompilationError(shaderId, resource_name)
        }
    }
}
