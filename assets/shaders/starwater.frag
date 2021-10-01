#define HIGHP
#define NSCALE 2700.0
#define CAMSCALE (NSCALE*10.0)

//light tint color
#define S1 vec3(0.705, 0.56, 1.0)

uniform sampler2D u_texture;
uniform sampler2D u_stars;

uniform vec2 u_campos; //water
uniform vec2 u_resolution;
uniform float u_time;

uniform vec2 u_ccampos; //space
uniform vec2 u_resolutionSpace;

varying vec2 v_texCoords;

const float mscl = 40.0;
const float mth = 7.0;

void main(){
    vec2 c = v_texCoords;
    //water shader
    vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);
    float stime = u_time / 5.0;
    vec2 offset = vec2(sin(stime/3.0 + coords.y/0.75) * v.x, 0.0);
    //space shader
    vec2 coordsSpace = vec2(c.x * u_resolutionSpace.x, c.y * u_resolutionSpace.y);

    //water
    vec4 tex = texture2D(u_texture, c + offset);
    vec3 color = tex.rgb * vec3(0.9, 0.9, 1.0);
    float al = texture2D(u_texture, c).a * tex.a;
    vec3 stars = texture2D(u_stars, coordsSpace/NSCALE + vec2(-0.1 + stime / 1330.0, -0.1 + stime / 1000.0) + u_ccampos / CAMSCALE).rgb;

    float tester = mod((coords.x + coords.y*1.1 + sin(stime / 8.0 + coords.x/5.0 - coords.y/100.0)*2.0) +
    sin(stime / 20.0 + coords.y/3.0) * 1.0 +
    sin(stime / 10.0 - coords.y/2.0) * 2.0 +
    sin(stime / 7.0 + coords.y/1.0) * 0.5 +
    sin(coords.x / 3.0 + coords.y / 2.0) +
    sin(stime / 20.0 + coords.x/4.0) * 1.0, mscl);

    if(tester < mth){
        color += S1 * 0.15;
    }

    gl_FragColor = vec4(color.rgb + stars, al);
    // * 0.4 * (1.0 + sin(sin(stime / 20.0 + coordsSpace.y/53.0) * 0.3 + sin(stime / 7.0 - coordsSpace.y/61.0) + sin(stime / 20.0 + coordsSpace.x/111.0) * 0.7 + 2.0))
}