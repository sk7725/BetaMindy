#define HIGHP
#define NSCALE 150.0
#define CSCALE 4.0
#define S1 vec3(129.0, 68.0, 39.0) / 255.0
#define S2 vec3(60.0, 30.0, 20.0) / 255.0

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

const float speed = 0.005;

void main(){
    vec2 c = v_texCoords.xy;
    vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
    vec2 p = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y) * 0.01;
    p = p * 2.0 + 1.0;
    p.x += u_time/2000.;
    float noise = texture2D(u_noise, p / NSCALE).r;
    noise = noise * 2.0 + 1.0;
    p = p * 0.1 + vec2(noise * 0.9);
    p.x *= u_resolution.x/u_resolution.y;

    float l = smoothstep(0.0,1.,length(p));
    for(int i=1; i<6; i++){
        p.x+=0.3/float(i)*sin(float(i)*CSCALE*p.y+u_time*speed*1.);
        p.y+=0.3/float(i)*cos(float(i)*CSCALE*p.x+u_time*speed*1.);
    }

    float r = cos(p.x+p.y+1.0)*.5+.5;
    float ro = smoothstep(0.75,0.0,r);
    vec3 color = r * S1 + ro;

    float ship = texture2D(u_texture, c).a;
    gl_FragColor = vec4(color * ship + S2 * (1.0 - ship), ship);
}
