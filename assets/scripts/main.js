print("hi from BetaMindy");

//sets the destroySound and breakSound to the former breakSound(destroyed), also does the breakEffect
function setProp(name){
    try{
        let b = Vars.content.getByName(ContentType.block, name);
        b.destroySound = b.breakSound;
        b.breakEffect = b.destroyEffect;
        b.instantDeconstruct = true;
    }
    catch(ignore){
        print(ignore);
    }
}

//sets the destroySound to breakSound, defaults the breakSound
function swapBreakSound(name){
    try{
        let b = Vars.content.getByName(ContentType.block, name);
        b.destroySound = b.breakSound;
        b.breakSound = Sounds.breaks;
    }
    catch(ignore){
        print(ignore);
    }
}

if(Version.build >= 127){
    Events.on(ClientLoadEvent, () => {
        setProp("betamindy-pyra-crystal");
        setProp("betamindy-cryo-crystal");
        setProp("betamindy-scalar-crystal");
        setProp("betamindy-vector-crystal");
        setProp("betamindy-tensor-crystal");

        swapBreakSound("betamindy-cryo-wall");
        swapBreakSound("betamindy-cryo-wall-large");
    });
}