package betamindy.world.blocks.logic;

import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import betamindy.content.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.world.blocks.logic.*;

public class MessageSource extends MessageBlock {
    public MessageSource(String name) {
        super(name);
        update = true;
    }

    public static String quote(int length) {
        String[] quotes = new String[]{"you cannot kill me in a way that matters.", "just think, every step taken is another soul left behind", "everything burns every single day until it's reduced to dust", "this doesn't end well", "you think you're safe?", "one cannot create beauty without destruction", "every single moment has consequence", "you wouldn't want anyone to know what you're hiding.", "where are you right now? what do you fear?", "it doesn't make sense to save now.", "it's too late.", "where is it.", "there is no threat", "it's always been there", "never make another wish ever again.", "where are you right now?", "why? it will never end now.", "do not.", "they are not your enemy", "this is your fault.", "we are not dead yet.", "it's finally happening", "please verify your humanity", "no one will matter", "this is not a matter of caring.", "are you okay with what you just did?", "stop reading this.", "watch your head.", "if you see this", "do not look at it", "observation is prohibited.", "your mind is nonexistent"};
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            String[] quote = Structs.random(quotes).split(" ");
            int from = Mathf.random(0, Math.max(quote.length - 3, 0));
            int to = Mathf.random(from, quote.length - 1);
            for (int j = from; j <= to; ++j) {
                result.append(quote[j]);
                result.append(" ");
            }
        }
        return result.toString();
    }

    public class MessageSourceBuild extends MessageBuild {
        @Override
        public void updateTile() {
            int voids = 0;
            for(int i = 0; i < proximity.size; i++){
                Building other = proximity.get(i % proximity.size);
                if(other.block() == MindyBlocks.messageVoid){
                    voids += 1;
                }
            }
            if(Mathf.chance((float)voids / ((float)block().size * 4))) {
                if(!(message == null || message.length() == 0) && voids == ((float)block().size * 4)) { // surrounded by voids and not empty
                    message.setLength(0);
                } // if there's a void, do nothing
            } else {
                float chance = (float)Vars.state.wave / 200.0f;
                Tmp.c1.set(Color.scarlet).lerp(Color.yellow, Mathf.absin(10.0f - chance * 3.0f, 1.0f)).lerp(Color.white, 1.0f - chance);
                String q = quote(Mathf.random(1, Math.max(Mathf.random(100, 160) / 7, 2)));
                message.ensureCapacity(q.length() + 11);
                message.setLength(0);
                message.append("[#").append(Tmp.c1.toString()).append("]").append(q);
            }
        }
    }
}
