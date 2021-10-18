package engine

import org.joml.Matrix4f
import org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack
import java.io.Closeable
import java.util.*

class ShaderProgram(vertex_shader_path : String, fragment_shader_path : String) : Closeable, Useable {
    private val programId : Int get() = field
    var vertexShader : Shader
    var fragmentShader : Shader
    private var  uniforms : HashMap<String,Int> = HashMap()
    fun create_uniform(uniformName : String ){
        val uniformLocation = glGetUniformLocation(programId,uniformName)
        if (uniformLocation < 0 ){
            throw Exception("Could not find uniform $uniformName")
        }
        uniforms[uniformName] = uniformLocation
    }
    fun set_uniform(uniformName : String, value : Matrix4f){
        MemoryStack.stackPush().use {stack ->
           var buffer = stack.mallocFloat(16)
            value.get(buffer)
            var matrixNum = uniforms[uniformName] ?: {
                create_uniform(uniformName)
                uniforms[uniformName]!!
            }()

            glUniformMatrix4fv(matrixNum,false,buffer)
        }
    }



    init {
        programId = glCreateProgram()
        if(programId == 0)
            throw Exception("Could not create shader Program")

        // make shaders
        vertexShader  = Shader(GL_VERTEX_SHADER,vertex_shader_path)
        fragmentShader = Shader(GL_FRAGMENT_SHADER,fragment_shader_path)

        glAttachShader(programId,vertexShader.shaderId)
        glAttachShader(programId,fragmentShader.shaderId)

        glLinkProgram(programId)

        if(glGetProgrami(programId, GL_LINK_STATUS) == 0)
            throw Exception("Error linking engine.Shader code: " + glGetProgramInfoLog(programId))
        if(vertexShader.shaderId != 0) {
            glDetachShader(programId,vertexShader.shaderId)
        }
        if(fragmentShader.shaderId != 0)
            glDetachShader(programId,fragmentShader.shaderId)

        glValidateProgram(programId)
        if(glGetProgrami(programId, GL_VALIDATE_STATUS) == 0)
            throw Exception("Warning validating shader code: " + glGetProgramInfoLog(programId))

    }

    override fun bind() = glUseProgram(programId)
    override fun unbind() = glUseProgram(0)

    override fun close() {
       unbind()
        if(programId != 0){
            glDeleteProgram(programId)
        }
    }

}