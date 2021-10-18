package engine

import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import java.io.Closeable
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(positions: FloatArray, indices: IntArray, colours: FloatArray) : Closeable, Useable {
    val vaoID: Int = glGenVertexArrays()
        get
    private val vboID: Int
    private val idxVBOID: Int
    private val colourVBOID: Int
    val vertexCount: Int

    init {


        glBindVertexArray(vaoID)

        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        val verticesBuffer: FloatBuffer = MemoryUtil.memAllocFloat(positions.size).put(positions).flip()
        vertexCount = indices.size
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW)
        MemoryUtil.memFree(verticesBuffer)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)

        idxVBOID = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVBOID)

        val indicesBuffer: IntBuffer = MemoryUtil.memAllocInt(indices.size).put(indices).flip()
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW)
        MemoryUtil.memFree(indicesBuffer)

        colourVBOID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, colourVBOID)
        val colorBuffer = MemoryUtil.memAllocFloat(colours.size).put(colours).flip()
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW)
        MemoryUtil.memFree(colorBuffer)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)


        glBindVertexArray(0)
    }


    override fun close() {
        glDisableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDeleteBuffers(intArrayOf(vboID, idxVBOID, colourVBOID))
        glBindVertexArray(0)
        glDeleteVertexArrays(vaoID)
    }

    override fun bind() {
        glBindVertexArray(vaoID)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
    }

    override fun unbind() {
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)
    }
}