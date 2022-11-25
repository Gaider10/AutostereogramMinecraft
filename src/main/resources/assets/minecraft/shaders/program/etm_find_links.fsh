#version 150

uniform sampler2D DepthSampler;

uniform vec2 InSize;

uniform float ETM_MaxDist;
uniform float ETM_EyeSep;
uniform float ETM_EyeDist;
uniform float ETM_MinDepth;
uniform float ETM_MaxDepth;
uniform float ETM_MinSep;
uniform float ETM_MaxSep;

out vec4 oColor;

float sample_depth(int x, int y) {
    float n = 0.05;
    float f = 8.0 * 16.0 * 4.0;
    float a = (f + n) / (n - f);
    float b = (2.0 * f * n) / (n - f);

    float z_window = texelFetch(DepthSampler, ivec2(x, y), 0).x;
    float z_ndc = z_window * 2.0 - 1.0;
    float z_view = -b / (z_ndc + a);

    float max_d = ETM_MaxDist;

    return clamp(-z_view / max_d, 0.0, 1.0);
}

int sample_sep_i(int x, int y) {
    float tex_depth = sample_depth(x, y);
    float depth = tex_depth * (ETM_MaxDepth - ETM_MinDepth) + ETM_MinDepth;
    float sep = ETM_EyeSep * depth / (depth + ETM_EyeDist);
    int sep_i = int(sep);
    return sep_i;
}

int calc_sep_i_l(int sep_i) {
    return sep_i / 2;
}

int calc_sep_i_r(int sep_i) {
    return (sep_i + 1) / 2;
}

float unpackU8(int v) {
    return float(v) / 255.0;
}

vec2 unpackU16(int v) {
    return vec2(unpackU8(v & 255), unpackU8(v >> 8));
}

void main() {
    int min_sep_i_l = calc_sep_i_l(int(ETM_MinSep));
    int max_sep_i_l = calc_sep_i_l(int(ETM_MaxSep));
    int min_sep_i_r = calc_sep_i_r(int(ETM_MinSep));
    int max_sep_i_r = calc_sep_i_r(int(ETM_MaxSep));

    int x = int(gl_FragCoord.x);
    int y = int(gl_FragCoord.y);

    int lx = x;
    int rx = x;

    // Find the closest left link
    for (int sep_i_r = min_sep_i_r; sep_i_r <= max_sep_i_r; sep_i_r++) {
        // No more left links - randomize
        if (x - sep_i_r * 2 < 0) {
            break;
        }

        int sep_i = sample_sep_i(x - sep_i_r, y);

        // Found closest left link
        if (sep_i_r == calc_sep_i_r(sep_i)) {
            lx = x - sep_i;
            break;
        }
    }

    // Find the closest right link
    for (int sep_i_l = min_sep_i_l; sep_i_l <= max_sep_i_l; sep_i_l++) {
        // No more right links - randomize
        if (x + sep_i_l * 2 >= int(InSize.x)) {
            break;
        }

        int sep_i = sample_sep_i(x + sep_i_l, y);

        // Found closest right link
        if (sep_i_l == calc_sep_i_l(sep_i)) {
            rx = x + sep_i;
            break;
        }
    }

    oColor = vec4(unpackU16(lx), unpackU16(rx));
}