package engine

import org.joml.Matrix4f
import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL
import java.io.Closeable
import java.nio.IntBuffer
import kotlin.properties.Delegates


class Renderer : Closeable, Runnable {

    // The window handle
    private var windowHandle by Delegates.notNull<Long>();
    var gameItems: List<GameItem> = listOf()

    companion object {
        private val FOV: Float = Math.toRadians(60.0).toFloat()
        private const val Z_NEAR: Float = 0.01f
        private const val Z_FAR: Float = 1000.0f
    }

    lateinit var projectionMatrix: Matrix4f
    override fun run() {
        println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        close()
    }

    override fun close() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null)?.free();
    }

    private fun init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        windowHandle = glfwCreateWindow(500, 500, "Hello World!", NULL, NULL);

        if (windowHandle == NULL)
            throw RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowHandle) { window, key, scancode, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        }
        glfwSetFramebufferSizeCallback(windowHandle) { window, width, height ->
        }


        // Get the thread stack and push a new frame
        stackPush().use { stack ->
            val pWidth: IntBuffer = stack.mallocInt(1); // int*
            val pHeight: IntBuffer = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(windowHandle, pWidth, pHeight);
            val width = pWidth.get(0)
            val height = pHeight.get(0)
            // Get the resolution of the primary monitor
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor()) ?: throw Exception("Could not get video mode")
            val aspectRatio = (width.toFloat() / height)
            projectionMatrix = Matrix4f().perspective(FOV, aspectRatio,
                Z_NEAR, Z_FAR)


            // Center the window
            glfwSetWindowPos(
                windowHandle,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
            )
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowHandle);
    }

    private fun loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        val positions = floatArrayOf(
            -0.5f, 0.5f, -1.05f,
            -0.5f, -0.5f, -1.05f,
            0.5f, -0.5f, -1.05f,
            0.5f, 0.5f, -1.05f)
        val indices = intArrayOf(
            0, 1, 3, 3, 1, 2)
        val colours = floatArrayOf(
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f)
        val square = Mesh(positions, indices, colours)
        val shaderProgram = ShaderProgram("vertex.glsl", "fragment.glsl")
        shaderProgram.utilise {
            shaderProgram.set_uniform("projectionMatrix", projectionMatrix)
        }
        try {

            // glPolygonMode(GL_FRONT_AND_BACK,GL_LINE)

            while (!glfwWindowShouldClose(windowHandle)) {

                glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT); // clear the framebuffer
                glClearColor(0.0f, 1.0f, 1.0f, 0.0f)

                shaderProgram.utilise {
                    square.utilise {
                        glDrawElements(GL_TRIANGLES, square.vertexCount, GL_UNSIGNED_INT, 0)
                    }
                }
                glfwSwapBuffers(windowHandle); // swap the color buffers

                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close_multiple(square, shaderProgram, this)
        }
    }
}