package betamindy.ui;

import arc.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.util.*;
import betamindy.content.*;
import betamindy.graphics.*;
import betamindy.world.blocks.campaign.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.ui.dialogs.*;

/**
 * @author Goober
 */
public class ManualDialog extends BaseDialog {
    int currentPage;
    int currentTopic; //current chapter
    protected LoreManual manual;

    public ManualDialog(LoreManual manual){
        super("Project E.S.O.T.");
        this.manual = manual;

        build();
        build(); //Run build twice to deal with strange issues that happen the first build.

        Events.on(EventType.ResizeEvent.class, e -> {
            build();
        });
    }

    public boolean exists(int chapter){
        return manual != null && (manual.defaultChapter.id == chapter || manual.pageBlocks.contains(p -> p.chapter.id == chapter));
    }

    @Override
    public Dialog show(){
        if(cont != null && manual != null) build();
        return super.show();
    }

    public void build(){
        // clear dialog contents
        cont.clearChildren();
        buttons.clearChildren();

        cont.table(t -> t.button(Icon.exit, this::hide)).top();
        if(manual == null){
            return; //should not happen
        }

        final LorePages.Chapter chapter = LorePages.idMap.get(currentTopic);
        currentPage %= chapter == null ? 1 : chapter.pages.length;

        // build main table
        cont.table(main -> {

            // page contents
            main.table(Tex.button, content -> {
                content.pane(Styles.defaultPane, t -> {
                    if(chapter == null || !exists(chapter.id) || (!chapter.unlocked() && chapter != manual.defaultChapter)) LorePages.addLocked(t);
                    else{
                        chapter.pages[currentPage].addContent(t);
                    }
                }).grow().fill().top().left();
            }).top().size(Core.scene.getWidth() * 0.6f, Core.scene.getHeight() * 0.8f).name("content");

            // page number
            main.row();
            main.table(page -> {
                page.add(chapter == null ? "?/?" : (currentPage + 1) + "/" + chapter.pages.length).color(Pal.darkishGray).align(Align.center).labelAlign(Align.center);
            }).center();
        });

        // navigation buttons
        // topic buttons
        cont.table(topics -> {
            topics.button(new TextureRegionDrawable(manual.defaultChapter.getIcon()), () -> {
                currentPage = 0;
                currentTopic = manual.defaultChapter.id;
                build();
            }).tooltip(manual.defaultChapter.name);
            for(ManualPiece p : manual.pageBlocks){
                topics.row();
                if(p.chapter.unlocked()){
                    topics.button(new TextureRegionDrawable(p.chapter.getIcon()), () -> {
                        currentPage = 0;
                        currentTopic = p.chapter.id;
                        build();
                    }).tooltip(p.chapter.name);
                }
                else{
                    topics.button(Icon.lock, () -> {

                    }).tooltip("???").color(Pal2.locked);
                }
            }
        }).top().name("topics");

        // page buttons
        if(chapter != null){
            buttons.button(Icon.left, () -> {
                currentPage--;
                build();
            }).disabled(e -> currentPage - 1 < 0).center().tooltip("Previous Page");
            buttons.button(Icon.home, () -> {
                currentPage = 0;
                build();
            }).disabled(e -> currentPage == 0).center().tooltip("First Page");
            buttons.button(Icon.right, () -> {
                currentPage++;
                build();
            }).disabled(e -> currentPage + 1 > chapter.pages.length - 1).center().tooltip("Next Page");
        }
        addCloseListener();
    }
}
