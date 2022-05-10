package betamindy.content;

import arc.struct.*;
import betamindy.util.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.game.Objectives.*;
import mindustry.type.*;

import static mindustry.content.Items.*;
import static mindustry.content.Blocks.*;
import static mindustry.content.TechTree.*;
import static betamindy.content.MindyBlocks.*;
import static betamindy.content.MindyItems.*;

public class MindyTechTree{
    static TechTree.TechNode context = null;

    public static void load(){
        //Shar branch
        margeNode(mechanicalDrill, () -> {
            node(isotopeReactor, () -> {
                node(arcKiln, () -> {
                    node(lancerKiln);
                    node(fusionChamber, () -> {
                        node(scalarFurnace);
                    });
                    node(electroRefiner, () -> {
                        node(siliconCondenser, () -> {

                        });
                    });
                });
            });
        });
        margeNode(duo, () -> {
            node(spectrum, () -> {
                node(ray, () -> {
                    node(astro);
                });
            });
            node(nebula, () -> {
                node(sequence);
            });
        });
        //end region

        margeNode(payloadConveyor, () -> {
            //todo re-add payload forge blocks when Anuke does so
        });

        margeNode(laserDrill, () -> {
            node(drillMini, () -> {
               node(drillMega);
            });
        });
        margeNode(pneumaticDrill, () -> {
            node(mynamite, () -> {
                node(mynamiteLarge);
            });
        });

        margeNode(repairTurret, () -> {
            node(rejuvenator);
        });

        margeNode(massDriver, () -> {
            node(payCannon, () -> {
                node(payCatapult);
            });
        });

        margeNode(door, () -> {
            node(siliconWall, () -> {
                node(siliconWallLarge);
            });
            node(teamWall);
        });
        margeNode(copperWall, () -> {
            node(leadWall, () -> {
                node(leadWallLarge);
                node(coalWall, () -> {
                    node(coalWallLarge);
                });
                node(metaglassWall, () -> {
                    node(metaglassWallLarge);
                    node(spikeClear);
                });
                node(pyraWall, () -> {
                    node(pyraWallLarge);
                    node(blastWall, () -> {
                        node(blastWallLarge);
                    });
                    node(spikePyra);
                });
            });
            node(spikeScrap, () -> {
                node(crusher, () -> {
                    node(crusherPyra);
                });
            });
        });
        margeNode(titaniumWall, () -> {
            node(graphiteWall, () -> {
                node(graphiteWallLarge);
            });
        });
        margeNode(surgeWall, () -> {
            node(cryoWall, () -> {
                node(cryoWallLarge);
                node(spikeCryo);
            });
            node(spikeSurge);
        });

        margeNode(ripple, () -> {
            node(anchor, () -> {
                node(bermuda);
            });
        });
        margeNode(wave ,() -> {
            node(fan, () -> {
                node(fanMega);
            });
        });
        margeNode(tsunami, () -> {
            node(propaganda);
        });

        margeNode(vault, () -> {
            node(silo, () -> {
                node(warehouse);
            });
            node(chest, () -> {
                node(largeChest);
            });
        });
        margeNode(container, () -> {
            node(anucoinNode, () -> {
                node(anucoinSafe, () -> {
                    node(anucoinVault);
                });
                node(cafe);
                nodePortal(ancientStore, 5);
                node(tradingPost);
                node(itemShop, () -> {
                    node(unitShop);
                });
                node(credit, () -> {
                    node(taxation);
                    node(brokerage);
                });
            });
        });

        margeNode(microProcessor, () -> {
            node(heatSink, () -> {
                node(heatFan);
                node(heatSinkLarge);
            });
        });
        margeNode(memoryCell, () -> {
            node(linkPin);
            node(nullifier);
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

        node(boostPad, () -> {
            node(driftPad, () -> {
                nodePortal(teleportPad, 9);
            });
            node(bumper, () -> {
                node(bumperBlue);
                node(bumperPlus);
            });
            node(claw, () -> {
                node(phaseClaw);
            });
            node(clearPipe, () -> {
                node(clearDuct);
            });
        });

        margeNode(itemBridge, () -> {
            node(piston, () -> {
                node(stickyPiston, () -> {
                    node(cloner);
                });

                node(sporeSlime, () -> {
                    node(sporeSlimeSided);
                    node(surgeSlime);
                });
            });
        });

        margeNode(conveyor, () -> {
            node(cog, () -> {
                node(titaniumCog, () -> {
                    node(plastaniumCog, () -> {
                        node(spinner, () -> {
                            node(spinnerInert);
                        });
                    });
                    node(armoredCog);
                });
            });

            node(payloadRail, () -> {
                node(railSorter);
            });
        });

        margeNode(battery, () -> {
            node(discharger);
            node(capacitor, () -> {
                node(megaCapacitor);
            });
        });

        margeNode(incinerator, () -> {
            node(fireCan, () -> {
                node(campfire);
            });
        });

        margeNode(logicDisplay, () -> {
            node(noteBlock, () -> {
                node(starNoteBlock);
                node(sfxBlock);
            });
            node(pen, () -> {
                node(colorModule);
                node(strokeModule);
            });
        });

        margeNode(pyratite, () -> {
            node(crystalPyra);
        });

        margeNode(blastCompound, () -> {
            nodeProduce(cryonite, () -> {
                node(crystalCryo);
            });
        });

        margeNode(Liquids.water, () -> {
            nodeProduce(MindyLiquids.coffee);
        });
    }

    //TODO: replace this with the standard TechTree API, it's public now -Anuke

    private static void margeNode(UnlockableContent parent, Runnable children){
        context = TechTree.all.find(t -> t.content == parent);
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
        node(content, content.researchRequirements(), objectives.add(new Produce(content)), children);
    }

    private static void nodeProduce(UnlockableContent content, Runnable children){
        nodeProduce(content, Seq.with(), children);
    }

    private static void nodeProduce(UnlockableContent content){
        nodeProduce(content, Seq.with(), () -> {});
    }

    private static void nodePortal(UnlockableContent content, int level, Runnable children){
        node(content, content.researchRequirements(), Seq.with(new ObjectivesM.PortalLevel(level)), children);
    }

    private static void nodePortal(UnlockableContent content, int level){
        nodePortal(content, level, () -> {});
    }
}