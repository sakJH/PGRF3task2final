import lwjglutils.OGLBuffers;

import java.sql.SQLOutput;

public class Grid {
    private static OGLBuffers buffers;
    /**
     * GL_TRIANGLES
     *
     * @param m vertex count in row
     * @param n vertex count in column
     */
    public static OGLBuffers gridListTriangle(int m, int n)
    {
        float[] vertices = new float[2 * m * n];
        int[] indices = new int[3 * 2 * (m - 1) * (n - 1)];

        int index = 0;
        for (int i = 0; i < m; i += 1) {
            for (int j = 0; j < n; j += 1) {

                vertices[index++] = j / (float) (n - 1);
                vertices[index++] = i / (float) (m - 1);
            }
        }

        // Indices
        int indicesIndex = 0;
        for (int i = 0; i < m - 1; i++) {
            int offset = (i * m);
            for (int j = 0; j < n - 1; j++) {
                int a = offset + j;
                int b = offset + j + n;
                int c = offset + j + 1;
                int d = offset + j + n + 1;
                // ABC
                indices[indicesIndex++] = a;
                indices[indicesIndex++] = b;
                indices[indicesIndex++] = c;
                // BCD
                indices[indicesIndex++] = b;
                indices[indicesIndex++] = c;
                indices[indicesIndex++] = d;
            }
        }


        OGLBuffers.Attrib[] attribs = new OGLBuffers.Attrib[] {
                new OGLBuffers.Attrib("inPosition", 2),
        };
        buffers = new OGLBuffers(vertices, attribs, indices);
        return buffers;
    }

    public static OGLBuffers gridStripsTriangle(int m, int n)
    {
        float[] vertices = new float[2 * m * n];
        int[] indices = new int[3 * 2 * (m - 1) * (n - 1)];

        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                vertices[index++] = j / (float) (m - 1);
                vertices[index++] = i / (float) (n - 1);
            }
        }
        int index2 = 0;
        for (int i = 0; i < n - 1; i++) {
            int offset = i * m;

            if (i % 2 == 0) {
                for (int j = 0; j < m; j++) {
                    indices[index2++] = offset + j;
                    indices[index2++] = offset + j + m;
                    if (j == m - 1)
                    {
                        indices[index2++] = offset + j + m;
                    }
                }
            } else {
                for (int col = m - 1; col >= 0; col--) {
                    indices[index2++] = offset + col;
                    indices[index2++] = offset + col + m;
                    if (col == 0)
                    {
                        indices[index2++] = offset + col + m;
                    }
                }
            }
        }
        OGLBuffers.Attrib[] attribs = new OGLBuffers.Attrib[] {
                new OGLBuffers.Attrib("inPosition", 2),
        };

        buffers = new OGLBuffers(vertices, attribs, indices);
        return buffers;
    }

    //OLD
    public Grid(final int m, final int n) {
        float[] vertices = new float[2 * m * n];
        int[] indices = new int[3 * 2 * (m - 1) * (n - 1)];

        int index = 0;
        for (int i = 0; i < m; i += 1) {
            for (int j = 0; j < n; j += 1) {

                vertices[index++] = j / (float) (n - 1);
                vertices[index++] = i / (float) (m - 1);
            }
        }

        // Indices
        int indicesIndex = 0;
        for (int i = 0; i < m - 1; i++) {
            int offset = (i * m);
            for (int j = 0; j < n - 1; j++) {
                int a = offset + j;
                int b = offset + j + n;
                int c = offset + j + 1;
                int d = offset + j + n + 1;

                // ABC
                indices[indicesIndex++] = a;
                indices[indicesIndex++] = b;
                indices[indicesIndex++] = c;

                // BCD
                indices[indicesIndex++] = b;
                indices[indicesIndex++] = c;
                indices[indicesIndex++] = d;
            }
        }

        OGLBuffers.Attrib[] attribs = new OGLBuffers.Attrib[] {
                new OGLBuffers.Attrib("inPosition", 2),
        };
        buffers = new OGLBuffers(vertices, attribs, indices);
    }

    public OGLBuffers getBuffers() {
        return buffers;
    }
}

