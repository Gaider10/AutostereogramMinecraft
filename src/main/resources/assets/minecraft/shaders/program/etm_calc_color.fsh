#version 150

uniform sampler2D DiffuseSampler;

uniform float Time;

out vec4 oColor;

float rand(float seed) {
    return fract(sin(seed) * 43758.5453123);
}

vec4 rand_color(int x, int y) {
    float c = rand(rand(rand(Time) + float(x)) + float(y));
    return vec4(vec3(c), 1.0);
}

int packU8(float v) {
    return int(round(v * 255.0));
}

int packU16(vec2 v) {
    return packU8(v.x) + (packU8(v.y) << 8);
}

void main() {
    int x = int(gl_FragCoord.x);
    int y = int(gl_FragCoord.y);

    // Recusively find the color of the left link
    while (true) {
        // Left link
        int lx = packU16(texelFetch(DiffuseSampler, ivec2(x, y), 0).xy);

        // No left link - randomize
        if (lx == x) {
            oColor = rand_color(x, y);
            return;
        }

        // Left link's right link
        int lrx = packU16(texelFetch(DiffuseSampler, ivec2(lx, y), 0).zw);

        // Links don't match - randomize
        if (lrx != x) {
            oColor = rand_color(x, y);
            return;
        }

        // Link match - use left link's color
        x = lx;
    }

    // Unreachable but just in case
    oColor = rand_color(x, y);
}