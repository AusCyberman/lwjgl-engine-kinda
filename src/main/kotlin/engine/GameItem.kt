package engine

import org.joml.Vector3f

class GameItem(val mesh: Mesh) {

    private var rotation = Vector3f()
    var position = Vector3f()
        get

    var scale : Float = 1.0f
        get
        set

    fun setPosition(x : Float, y : Float, z : Float) {
        position.x = x
        position.y = y
        position.z = z
    }


}