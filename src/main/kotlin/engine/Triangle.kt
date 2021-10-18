package engine

import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.io.Closeable
import java.lang.Exception


class Triangle : Closeable {
    private val VAO = glGenVertexArrays();
    private val VBO: Int

    //  private val idxVBO : Int
    private val shaderProgram: ShaderProgram
    private val vertices = floatArrayOf(
        -0.5f,  0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f,  0.5f, 0.0f,
        0.5f,  0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f,
    )

    init {
        glBindVertexArray(VAO)
        VBO = glGenBuffers()
        val fl = MemoryUtil.memAllocFloat(vertices.size).put(vertices).flip() ?: throw Exception("Could not allocate vertices array")
        glBindBuffer(GL_ARRAY_BUFFER, VBO)
        glBufferData(GL_ARRAY_BUFFER, fl, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
        MemoryUtil.memFree(fl)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
        shaderProgram = ShaderProgram("vertex.glsl", "fragment.glsl")
    }

    fun render() {
        shaderProgram.bind()
        glBindVertexArray(VAO)
        glEnableVertexAttribArray(0)
        glDrawArrays(GL_TRIANGLES, 0, vertices.size/3)

        glDisableVertexAttribArray(0)
        glBindVertexArray(0)

        shaderProgram.unbind()
    }

    override fun close() {
        glDisableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(VBO)
        glBindVertexArray(0)
        glDeleteVertexArrays(0)
        shaderProgram.close()
    }
}

