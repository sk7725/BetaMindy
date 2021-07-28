uniform sampler2D u_texture;

uniform float u_time;
uniform vec2 u_offset;
uniform vec2 u_resolution;

varying vec4 v_color;
varying vec2 v_texCoords;

const float PI = 3.14159;

const vec3 color1 = vec3(1.0, 0.1, 0.9);
const vec3 color2 = vec3(1.0, 1.0, 0.0);
const vec3 color3 = vec3(0.0, 1.0, 1.0);
const vec3 color4 = vec3(0.1, 1.0, 0.3);
const vec3 color5 = vec3(0.0, 0.5, 0.1);
const vec3 color6 = vec3(1.0, 0.1, 0.1);
//now even cheaper
vec3 hue(float hm){
    if(hm>2.5) return color1;
    if(hm>1.6) return color2;
    if(hm>0.9) return color3;
    if(hm>0.8) return color5;
    if(hm>0.3) return color6;
    return color4;
    //return 2.0*(vec3(sin(hm),sin(hm+1.1*PI)*1.2,sin(hm+0.6666*PI)));
}
float zigzag(float x){
   return asin(sin(x));
}

float hash(float n) { return fract(sin(n) * 1e4); }
float hash(vec2 p) { return fract(1e4 * sin(17.0 * p.x + p.y * 0.1) * (0.1 + abs(sin(p.y * 13.0 + p.x)))); }
float blend(float f){return f * f * (3.0 - 2.0 * f);}
float noise(float x) {
   float i = floor(x);
   float f = fract(x);
   float u = f;
   return mix(hash(i), hash(i + 1.0), u);
}
mat2 rotation2d(float angle) {
   float s = sin(angle);
   float c = cos(angle);

   return mat2(
      c, -s,
      s, c
   );
}


float noise(vec2 x) {
   vec2 i = floor(x);
   vec2 f = fract(x);
   float a = hash(i);
   float b = hash(i + vec2(1.0, 0.0));
   float c = hash(i + vec2(0.0, 1.0));
   float d = hash(i + vec2(1.0, 1.0));
   vec2 u = f;
   return mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;
}
float sdBox( in vec2 p, in vec2 b )
{
    vec2 d = abs(p)-b;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}
float opSubtraction( float d1, float d2 ) { return max(-d1,d2); }
float sdOpenBox( in vec2 p, in vec2 b, in vec2 b2 )
{
    return opSubtraction(sdBox(p,b2),sdBox(p,b));
}



float localisednoise(vec2 x) {
   return hash(floor(x));
}


vec3 starLayer(vec2 uv,float scale,vec2 parallax,mat2 rotate,float sizevary,float chance){
    uv*=scale;
    uv+=parallax;
    vec3 col = vec3(0.0);
    float gay = localisednoise(uv+vec2(1.0));
    if(gay>chance){return col;}
    vec2 pos = vec2(gay,localisednoise(uv+vec2(2.0)))*0.7+vec2(0.15-0.5);
    float size = sizevary*(zigzag((u_time / 60.)*scale*0.5+5.0*localisednoise(uv+vec2(9.0))));
    size+=1.0;
    if(sdOpenBox(rotate*(mod(uv,vec2(1.0)) - vec2(0.5)+pos), vec2(0.08*size), vec2(0.04*size) )<0.){
        col = hue(localisednoise(uv)*PI);
        col=mix(col,vec3(0.5),vec3(1.0-(8.0/scale)));
    }

    return col;
}

void main(){
    vec4 color = texture2D(u_texture, v_texCoords.xy);
    vec2 uv = gl_FragCoord.xy/u_resolution.xy * 3.;
    //vec2 uv = v_texCoords.xy; // test nope
    uv.x*=(u_resolution.x/u_resolution.y);
    vec2 parrallax = u_offset.xy/50.0 + u_time / 60.; // set this to the camera position in the mod

    //pixellate
    //uv -= mod(uv,0.009);

    //stars

    vec3 col = vec3(0.0);
    mat2 fourfive = rotation2d(PI*0.25);
    col+=starLayer(uv,8.,parrallax,fourfive,0.5,0.7);
    col+=starLayer(uv,16.,parrallax,fourfive,0.3,0.5);
    col+=starLayer(uv,4.,parrallax,fourfive,0.1,0.3);
    col+=starLayer(uv,12.,parrallax,fourfive,0.4,0.3);

    // Output to screen
    gl_FragColor = vec4(color.rgb + col, color.a);
}
