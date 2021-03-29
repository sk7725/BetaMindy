package betamindy.entities;

/**
 * An interface that holds a drawing extension. Example of use derived from Project Unity:
 * <pre> {@code
 *  public class DeflectProjectorBuild extends Building implements ExtensionHolder, Ranged{
        public Extension ext;

        public float heat;
        public float hit;
        protected boolean deflected;

        {@literal @}Override
        public void created(){
            super.created();
            ext = Extension.create();
            ext.holder = this;
            ext.set(x, y);
            ext.add();
        }

        {@literal @}Override
        public void onRemoved(){
            super.onRemoved();
            ext.remove();
        }

        {@literal @}Override
        public float clipSizeExt(){
            return radf() * 2f;
        }

        {@literal @}Override
        public void drawExt(){
            float rad = radf();

            float z = Draw.z();
            Draw.z(UnityShaders.holoShield.getLayer());
            Draw.color(shieldColor, Color.white, Mathf.clamp(hit));

            if(Core.settings.getBool("animatedshields")){
                Fill.circle(x, y, rad);
            }else{
                Lines.stroke(1.5f);
                Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                Fill.circle(x, y, rad);
                Draw.alpha(1f);
                Lines.circle(x, y, rad);
                Draw.reset();
            }

            Draw.z(z);
            Draw.reset();
        }

        public float radf(){
            return radius * heat;
        }

        {@literal @}Override
        public void updateTile(){
            super.updateTile();

            heat = Mathf.lerpDelta(heat, efficiency(), warmup);
            if(timer.get(timerUse, deflectTime)){
                deflected = false;

                build = this;
                Groups.bullet.intersect(x - radf(), y - radf(), radf() * 2f, radf() * 2f, deflector);
            }

            if(!deflected){
                timer.reset(timerUse, 0f);
            }

            if(hit > 0f){
                hit = Mathf.lerpDelta(hit, 0f, 0.01f);
            }
        }

        {@literal @}Override
        public boolean shouldAmbientSound(){
            return radf() > 1f;
        }

        {@literal @}Override
        public float range(){
            return radf();
        }
    }
 * } </pre>
 * @author GlennFolker
 */
public interface ExtensionHolder{
    /** This will be called by {@link Extension#draw()} */
    void drawExt();

    /** @return The clipsize of the used {@link Extension} */
    float clipSizeExt();
}
