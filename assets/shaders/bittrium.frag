uniform sampler2D u_texture;

uniform float u_time;
uniform vec2 u_offset;

varying vec4 v_color;
varying vec2 v_texCoords;

void main(){
	vec4 color = texture2D(u_texture, v_texCoords.xy);
	vec2 pos = gl_FragCoord.xy + .8 * u_offset;

	float t = clamp((sin(u_time * .01 + pos.x * .008 + pos.y * .004) + 1.) / 2., 0., 1.);
	vec3 c = vec3(mix(0., 1., t), mix(.89, .39, t), mix(1., .85, t));

    gl_FragColor = vec4(color.rgb * c.rgb, color.a);
}