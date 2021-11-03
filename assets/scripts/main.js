print("hi from BetaMindy");

if(false && Version.number > 6 && !Vars.headless){
    print("Setting up 7.0 compatibility JS...");

    function bmItem(name){
        return Vars.content.getByName(ContentType.item, "betamindy-" + name);
    }

    //sets the destroySound and breakSound to the former breakSound(destroyed), also does the breakEffect
    function setProp(name){
        try{
            let b = Vars.content.getByName(ContentType.block, name);
            b.destroySound = b.breakSound;
            b.breakEffect = b.destroyEffect;
            b.instantDeconstruct = true;
        }
        catch(ignore){}
    }

    //sets the destroySound to breakSound, defaults the breakSound
    function swapBreakSound(name){
        try{
            let b = Vars.content.getByName(ContentType.block, name);
            b.destroySound = b.breakSound;
            b.breakSound = Sounds.breaks;
        }
        catch(ignore){}
    }

    //does the breakEffect
        function setBreakFx(name){
            try{
                let b = Vars.content.getByName(ContentType.block, name);
                b.breakEffect = b.destroyEffect;
                b.instantDeconstruct = true;
            }
            catch(ignore){}
        }

    let animbois = null;
    let animn = 0;

    Events.on(ClientLoadEvent, () => {
        setBreakFx("betamindy-spore-slime");
        setBreakFx("betamindy-spore-slime-sided");
        setBreakFx("betamindy-surge-slime");
        setProp("betamindy-pyra-crystal");
        setProp("betamindy-cryo-crystal");
        setProp("betamindy-scalar-crystal");
        setProp("betamindy-vector-crystal");
        setProp("betamindy-tensor-crystal");
        setProp("betamindy-space-crystal");
        setProp("betamindy-bittrium-crystal");
        setProp("betamindy-box");

        swapBreakSound("betamindy-cryo-wall");
        swapBreakSound("betamindy-cryo-wall-large");

        animbois = [bmItem("bittrium"), bmItem("tensor"), bmItem("source"), bmItem("star-stone")];
        animn = animbois.length;
        try{
            Vars.ui.settings.graphics.sliderPref("animlevel", 2, 0, 3, i => Core.bundle.get("slider.level." + i, "" + i));
            Core.settings.put("uiscalechanged", false);
        }
        catch(ignore){}
    });

    Events.run(Trigger.update, () => {
        if (animbois == null) return;
        try{
            let l = Core.settings.getInt("animlevel", 2);
            if(l <= 0) return;
            for(let i = 0; i < animn; i++){
                if(animbois[i].animIcon == null) continue;
                animbois[i].fullIcon = animbois[i].animIcon;
                if(l >= 2) animbois[i].uiIcon = animbois[i].animIcon;
            }
        }
        catch(ignore){}
    });
}