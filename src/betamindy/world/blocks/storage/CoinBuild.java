package betamindy.world.blocks.storage;

import mindustry.gen.*;

public interface CoinBuild {
    int coins();
    void handleCoin(Building source, int amount);

    /** Returns how many coins the building can actually accept.
     * For giving, check if acceptCoin() > 0 and give that amount with handleCoin(+) */
    default int acceptCoin(Building source, int amount){
        return 0;
    }
    /** Returns whether this building has coins that can be taken by AnucoinNodes.
     * For taking: check if outputCoin() is true, then check coins() and get as much as the block has with handleCoin(-) */
    default boolean outputCoin(){
        return false;
    }
    /** The amount of coins that is actively in need, thus draining the bank by default e.g. turrets */
    default int requiredCoin(Building source){
        //always resolve debt
        if(coins() < 0) return -coins();
        return 0;
    }
}
