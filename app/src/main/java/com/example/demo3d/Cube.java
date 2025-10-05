package com.example.demo3d;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Cube {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private ByteBuffer indexBuffer;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec4 vColor;" +
                    "varying vec4 color;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  color = vColor;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec4 color;" +
                    "void main() {" +
                    "  gl_FragColor = color;" +
                    "}";

    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // Cube vertices
    private float vertices[] = {
            -1.0f,  1.0f,  1.0f,  // 0: front top left
            1.0f,  1.0f,  1.0f,  // 1: front top right
            -1.0f, -1.0f,  1.0f,  // 2: front bottom left
            1.0f, -1.0f,  1.0f,  // 3: front bottom right
            -1.0f,  1.0f, -1.0f,  // 4: back top left
            1.0f,  1.0f, -1.0f,  // 5: back top right
            -1.0f, -1.0f, -1.0f,  // 6: back bottom left
            1.0f, -1.0f, -1.0f   // 7: back bottom right
    };

    // Colors for each vertex
    private float colors[] = {
            1.0f, 0.0f, 0.0f, 1.0f,  // red
            0.0f, 1.0f, 0.0f, 1.0f,  // green
            0.0f, 0.0f, 1.0f, 1.0f,  // blue
            1.0f, 1.0f, 0.0f, 1.0f,  // yellow
            1.0f, 0.0f, 1.0f, 1.0f,  // magenta
            0.0f, 1.0f, 1.0f, 1.0f,  // cyan
            1.0f, 1.0f, 1.0f, 1.0f,  // white
            0.5f, 0.5f, 0.5f, 1.0f   // gray
    };

    // Indices for the cube faces
    private byte indices[] = {
            0, 1, 2, 1, 3, 2,  // front
            4, 5, 6, 5, 7, 6,  // back
            0, 4, 2, 4, 6, 2,  // left
            1, 5, 3, 5, 7, 3,  // right
            0, 1, 4, 1, 5, 4,  // top
            2, 3, 6, 3, 7, 6   // bottom
    };

    public Cube() {
        // Initialize vertex byte buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // Initialize color byte buffer
        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);

        // Initialize index byte buffer
        indexBuffer = ByteBuffer.allocateDirect(indices.length);
        indexBuffer.put(indices);
        indexBuffer.position(0);

        // Prepare shaders and program
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_BYTE, indexBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }
}