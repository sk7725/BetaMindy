package betamindy.content;

import arc.struct.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static mindustry.content.Items.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.TechTree.*;
import static betamindy.content.MindyBlocks.*;

public class MindyTechTree implements ContentList{
    static TechTree.TechNode context = null;

    @Override
    public void load(){
        margeNode(payloadConveyor, () -> {
            node(blockLoader, () -> {
                node(blockPacker);
                node(payDeconstructor, () ->{
                    node(payDestroyer, () -> {
                        node(payEradicator);
                    });
                    node(blockForge, () -> {
                        node(blockWorkshop, () -> {
                            node(blockFactory);
                        });
                    });
                });
            });
            node(blockUnloader, () -> {
                node(blockUnpacker);
            });
        });

        margeNode(laserDrill, () -> {
            node(drillMini, () -> {
               node(drillMega);
            });
            node(mynamite, () -> {
               node(mynamiteLarge);
            });
        });

        margeNode(repairPoint, () -> {
            node(repairTurret);
        });

        margeNode(massDriver, () -> {
            node(payCannon, () -> {
                node(payCatapult);
            });
        });

        margeNode(door, () -> {
            node(teamWall);
        });

        margeNode(pyratite, () -> {
            node(pyraWall, () -> {
                node(pyraWallLarge);
            });
        });

        margeNode(metaglass, () -> {
            node(metaglassWall, () -> {
                node(metaglassWallLarge);
            });
        });

        margeNode(silicon, () -> {
            node(siliconWall, () -> {
                node(siliconWallLarge);
            });
        });

        margeNode(coal, () -> {
            node(coalWall, () -> {
                node(coalWallLarge);
            });
        });

        margeNode(blastCompound, () -> {
            node(blastWall, () -> {
                node(blastWallLarge);
            });
        });

        margeNode(vault, () -> {
            node(silo, () -> {
                node(warehouse);
            });
        });

        margeNode(memoryCell, () -> {
            node(linkPin);
        });

        margeNode(solarPanel, () -> {
            node(button, () -> {
                node(accel);
                node(buttonLarge);
                node(pressurePad, () -> {
                    node(pressurePadLarge);
                });
            });
        });

        margeNode(commandCenter, () -> {
            node(boostPad, () -> {
                node(bumper, () -> {
                    node(bumperBlue);
                    node(bumperPlus);
                });
            });
        });

        margeNode(switchBlock, () -> {
            node(present);
        });

        margeNode(itemBridge, () -> {
            node(piston, () -> {
                node(stickyPiston, () -> {
                    node(cloner, () -> {
                        node(spinner);
                    });
                });

                node(sporeSlime, () -> {
                    node(sporeSlimeSided);
                    node(surgeSlime);
                });
            });
        });
    }
    private static void margeNode(UnlockableContent parent, Runnable children){
        TechNode parnode = TechTree.all.find(t -> t.content == parent);
        context = parnode;
        children.run();
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Seq<Objective> objectives, Runnable children){
        TechNode node = new TechNode(context, content, requirements);
        if(objectives != null) node.objectives = objectives;

        TechNode prev = context;
        context = node;
        children.run();
        context = prev;
    }

    private static void node(UnlockableContent content, ItemStack[] requirements, Runnable children){
        node(content, requirements, null, children);
    }

    private static void node(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives, children);
    }

    private static void node(UnlockableContent content, Runnable children){
        node(content, content.researchRequirements(), children);
    }

    private static void node(UnlockableContent block){
        node(block, () -> {});
    }

    private static void nodeProduce(UnlockableContent content, Seq<Objective> objectives, Runnable children){
        node(content, content.researchRequirements(), objectives.and(new Produce(content)), children);
    }

    private static void nodeProduce(UnlockableContent content, Runnable children){
        nodeProduce(content, Seq.with(), children);
    }

    private static void nodeProduce(UnlockableContent content){
        nodeProduce(content, Seq.with(), () -> {});
    }
}