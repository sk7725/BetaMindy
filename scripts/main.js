if(!Vars.headless){
  Events.on(ClientLoadEvent, () => {
    Core.app.post(() => {
      if(Vars.mods.locateMod("betamindy") == null){
        Vars.ui.showConfirm("$download.title", "$download.text", () => {
          Core.app.openURI("https://github.com/sk7725/BetaMindy");
        });
      }

      var mod = Vars.mods.locateMod("beta-mindy-r");
      mod.meta.displayName = "[gray]Beta[lightgray]Mindy[][]";
      mod.meta.description = "$download.text";
    });
  });
}
