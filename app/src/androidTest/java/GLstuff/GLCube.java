package GLstuff;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by monte on 16/08/2015.
 */
public class GLCube {
    private float vertices[] = {
            0f, 1f,
            1, -1f,
            -1f, -1f
    };

    private FloatBuffer vertBuff;

    public GLCube (){
        ByteBuffer bBuff = ByteBuffer.allocate(vertices.length * 4);
        bBuff.order(ByteOrder.nativeOrder());
        vertBuff = bBuff.asFloatBuffer();
        vertBuff.put(vertices);
        vertBuff.position(0);
    }
}
