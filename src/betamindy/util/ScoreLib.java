package betamindy.util;

import arc.struct.*;
import mindustry.Vars;
import mindustry.entities.bullet.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.units.*;
import mindustry.world.consumers.*;

//ScoreLib originally made by EyeOfDarkness. Ported to v7 and improved by ThePythonGuy3
public class ScoreLib {
    public OrderedMap<Item, Float> itemScores = new OrderedMap<>();
    public OrderedMap<Liquid, Float> liquidScores = new OrderedMap<>();
    public OrderedMap<UnitType, Float> unitScores = new OrderedMap<>();

    public int scanLayer = 5;

    public int liquidDivides = 6;
    public boolean loaded = false;

    public float getDefaultScoreLiquid(Liquid liquid){
        float temprC = Math.abs(liquid.temperature - 0.5f) * 2f;
        float viscC = Math.abs(liquid.viscosity - 0.5f) * 2f;

        float explosivenessC = Math.max(liquid.explosiveness * 1.2f, 0);
        float flammabilityC = Math.max(liquid.flammability * 1.2f, 0f) * 0.3f;
        float heatCapacityC = Math.max(liquid.heatCapacity * 1.2f, 0f) * 2.5f;

        return (0.5f + temprC + viscC + explosivenessC + flammabilityC + heatCapacityC) / liquidDivides;
    }

    public float getDefaultScoreItem(Item item){
        float hardnessC = Math.max(item.hardness, 0f) * 3.5f;

        float explosivenessC = Math.max(item.explosiveness, 0f) * 1.5f;
        float flammabilityC = Math.max(item.flammability, 0f);
        float chargeC = Math.max(item.charge, 0f) * 1.5f;

        float score = item.cost * (hardnessC + explosivenessC + flammabilityC + chargeC) * (item.alwaysUnlocked ? 1f : 1.5f) * (item.lowPriority ? 0.5f : 1f);
        return score + 0.5f;
    }

    public float getScoreBullet(BulletType bullet){
        if(bullet == null) return 1024f; //if a bullet is null the turret/unit is actually full-on java, and its creator is definitely up to something horrible.
        return (
                bullet.healPercent / 100f +
                        bullet.buildingDamageMultiplier * bullet.damage +
                        bullet.splashDamage * bullet.splashDamageRadius * 0.1f +
                        (bullet.pierce ? Math.max(bullet.pierceCap, 1.5f) : 1f) +
                        (bullet.status == null ? 1f : bullet.status.damage * bullet.statusDuration) +
                        bullet.healPercent
        ) / (bullet.ammoMultiplier * 0.9f + bullet.inaccuracy * 0.5f);

    }

    public float getScoreUnitWeapons(UnitType unit){
        float score = 0f;

        for(Weapon weapon : unit.weapons){
            score += (getScoreBullet(weapon.bullet) / Math.max(weapon.reload, 0.2f)) * unit.hitSize * 0.5f;
        }

        return score;
    }

    public float getStacksCost(ItemStack[] stacks){
        float score = 0f;

        for(ItemStack stack : stacks){
            score += itemScores.get(stack.item) * stack.amount;
        }

        return score;
    }

    public float getScoreUnit(UnitType unit){
        float score = getScoreUnitWeapons(unit);

        score += (unit.health * 0.3f + unit.dpsEstimate * 0.2f) * unit.speed * Math.max(unit.mineTier, 1f) * Math.max(unit.abilities.size, 1);
        score += getStacksCost(unit.getTotalRequirements());

        return score;
    }

    public void itemsLoad(){
        Seq<Item> tmpItemArray = new Seq<>();
        Seq<Float> tmpItemScores = new Seq<>();
        Seq<Integer> tmpItemTypes = new Seq<>();

        Seq<Liquid> tmpLiquidArray = new Seq<>();
        Seq<Float> tmpLiquidScores = new Seq<>();
        Seq<Integer> tmpLiquidTypes = new Seq<>();

        for(int i = 0; i < Vars.content.items().size; i++){
            Item item = Vars.content.item(i);
            if(item == null) continue;

            float score = getDefaultScoreItem(item);

            tmpItemArray.add(item);
            tmpItemScores.add(score);
            tmpItemTypes.add(0);
        }

        for(int i = 0; i < Vars.content.liquids().size; i++){
            Liquid liquid = Vars.content.liquid(i);
            if(liquid == null) continue;

            float score = getDefaultScoreLiquid(liquid);

            tmpLiquidArray.add(liquid);
            tmpLiquidScores.add(score);
            tmpLiquidTypes.add(0);
        }

        for(int j = 0; j < scanLayer; j++) {
            for(int i = 0; i < Vars.content.blocks().size; i++) {
                Block block = Vars.content.block(i);
                if(block == null) continue;

                //Usage of the item in crafting
                if(block instanceof GenericCrafter) {
                    GenericCrafter crafter = (GenericCrafter) block;

                    float tmpScoreA = 0;
                    float tmpScoreB = 0;

                    if(crafter.outputItem != null) {
                        if(crafter.findConsumer(c -> c instanceof ConsumeItems) instanceof ConsumeItems itemStacks){
                            for (int f = 0; f < itemStacks.items.length; f++) {
                                int id = tmpItemArray.indexOf(itemStacks.items[f].item);
                                tmpScoreA += (tmpItemScores.get(id == -1 ? tmpItemScores.size -1 : id) * Math.max(itemStacks.items[f].amount, 1));
                            }
                        }

                        if(crafter.findConsumer(c -> c instanceof ConsumeLiquidBase) instanceof ConsumeLiquid liquidStacks){
                            int id = tmpLiquidArray.indexOf(liquidStacks.liquid);
                            tmpScoreB += tmpLiquidScores.get(id == -1 ? tmpLiquidScores.size -1 : id) * liquidStacks.amount;
                        }

                        int id = tmpItemArray.indexOf(crafter.outputItem.item);
                        id = id == -1 ? tmpItemScores.size -1 : id;
                        if(tmpItemTypes.get(id) == 0){
                            tmpItemScores.set(id, Math.max((tmpScoreA + tmpScoreB) / crafter.outputItem.amount, getDefaultScoreItem(crafter.outputItem.item)));
                            tmpItemTypes.set(id, 2);
                        }
                    }

                    if(crafter.outputLiquid != null){
                        float tmpScoreC = 0;
                        float tmpScoreD = 0;

                        //TODO what was this for, exactly? -Anuke
                        float liquidConvAmount = 0;

                        if(crafter.findConsumer(c -> c instanceof ConsumeItems) instanceof ConsumeItems itemStacks){
                            for(int f = 0; f < itemStacks.items.length; f++){
                                int id = tmpItemArray.indexOf(itemStacks.items[f].item);
                                tmpScoreC += (tmpItemScores.get(id == -1 ? tmpItemScores.size -1 : id) * Math.max(itemStacks.items[f].amount, 1));
                            }
                        }

                        if(crafter.findConsumer(c -> c instanceof ConsumeLiquidBase) instanceof ConsumeLiquid liquidStacks){
                            int id = tmpLiquidArray.indexOf(liquidStacks.liquid);
                            tmpScoreD += tmpLiquidScores.get(id == -1 ? tmpLiquidScores.size -1 : id) * liquidStacks.amount;
                            liquidConvAmount = liquidStacks.amount;
                        }

                        float trueAmount = crafter.outputLiquid.amount;

                        int id = tmpLiquidArray.indexOf(crafter.outputLiquid.liquid);
                        id = id == -1 ? tmpLiquidScores.size -1 : id;
                        if(tmpLiquidTypes.get(id) == 0){
                            tmpLiquidScores.set(id, Math.max((tmpScoreC + tmpScoreD) / trueAmount, getDefaultScoreLiquid(crafter.outputLiquid.liquid)));
                            tmpLiquidTypes.set(id, 2);
                        }
                    }
                }

                //Usage of the item in mineable floors
                if(block instanceof Floor){
                    Floor floor = (Floor) block;

                    if(floor.itemDrop != null){
                        Item itemB = floor.itemDrop;

                        int id = tmpItemArray.indexOf(itemB);
                        id = id == -1 ? tmpItemScores.size -1 : id;
                        tmpItemScores.set(id, getDefaultScoreItem(itemB));
                        tmpItemTypes.set(id, 1);
                    }

                    if(floor.liquidDrop != null){
                        Liquid liquidB = floor.liquidDrop;

                        int id = tmpLiquidArray.lastIndexOf(liquidB, true);
                        id = id == -1 ? tmpLiquidScores.size -1 : id;
                        tmpLiquidScores.set(id, Math.max(getDefaultScoreLiquid((liquidB)), 0.1f));
                        tmpLiquidTypes.set(id, 1);
                    }
                }

                //Usage of the item in bullets
                if(block instanceof ItemTurret){
                    ObjectMap<Item, BulletType> ammoTypes = ((ItemTurret)block).ammoTypes;

                    for(Item item : Vars.content.items()){
                        if(ammoTypes.containsKey(item) && tmpItemArray.contains(item)){
                            BulletType bullet = ammoTypes.get(item);

                            if (bullet == null) continue;

                            //Bullet score
                            float score = getScoreBullet(bullet);

                            int id = tmpItemArray.indexOf(item);
                            tmpItemScores.set(id, tmpItemScores.get(id) + Math.min(score * 0.3f, 2.5f));

                        }
                    }
                }

                //Usage of the item in unit creation
                if(block instanceof UnitFactory){
                    Seq<UnitFactory.UnitPlan> unitPlans = ((UnitFactory)block).plans;

                    for(UnitFactory.UnitPlan unitPlan : unitPlans){
                        float score = unitPlan.unit.hitSize;

                        if(unitPlan == null) continue;

                        score = getScoreUnitWeapons(unitPlan.unit) * 0.1f;

                        for(ItemStack itemStack : unitPlan.requirements){
                            float tmpScore = score;
                            if(tmpItemArray.contains(itemStack.item)){
                                tmpScore += itemStack.amount * 0.1f;
                            }

                            int id = tmpItemArray.indexOf(itemStack.item);
                            tmpItemScores.set(id, tmpItemScores.get(id) + Math.min(tmpScore * 0.3f, 2.5f));
                        }
                    }
                }
            }
        }

        for(int k = 0; k < tmpItemArray.size; k++){
            if(tmpItemArray.get(k) == null || tmpItemArray.get(k).isHidden()) continue;
            itemScores.put(tmpItemArray.get(k), tmpItemScores.get(k));
        }

        for(int k = 0; k < tmpLiquidArray.size; k++){
            if(tmpLiquidArray.get(k) == null || tmpLiquidArray.get(k).isHidden()) continue;
            liquidScores.put(tmpLiquidArray.get(k), tmpLiquidScores.get(k));
        }

        for(int k = 0; k < Vars.content.units().size; k++) {
            UnitType unit = Vars.content.units().get(k);
            if (unit == null || unit.isHidden()) continue;
            unitScores.put(unit, getScoreUnit(unit));
        }
    }

    public void loadItems(){
        if(loaded) return;
        itemsLoad();

        loaded = true;
    }

    public float getLiquidDiv(){
        return liquidDivides;
    }

    public OrderedMap<Liquid, Float> liquidScores(){
        return liquidScores;
    }

    public OrderedMap<UnitType, Float> unitScores(){
        return unitScores;
    }

    public OrderedMap<Item, Float> scores(){
        return itemScores;
    }
}