package com.vicious.viciouslibkit.event.piston;

import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.util.nms.NMSHelper;
import com.vicious.viciouslibkit.util.vector.WorldPosI;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Piston;
import org.bukkit.event.block.BlockPistonEvent;

import java.util.function.Consumer;


/**
 * Controls a piston in world.
 */
public abstract class PistonController<T extends BlockPistonEvent> implements Consumer<T> {
    protected BlockFace direction;
    protected WorldPosI blockPosition;
    public PistonController(WorldPosI blockPosition){
        this.blockPosition=blockPosition;
    }
    public WorldPosI getPosition() {
        return blockPosition;
    }
    public Block getBlock(){
        return blockPosition.getBlock();
    }
    public void attemptRetract(){
        updatePiston();
    }
    public void attemptPush(){
        Block piston = getBlock();
        if(piston.getBlockData() instanceof Piston) {
            try {
                NMSHelper.setExtended(piston, true);
                Piston pdat = (Piston) piston.getBlockData();
                pdat.setExtended(true);
                piston.setBlockData(pdat);
            } catch (Exception ignored){}
        }
        else{
            //TODO: find a better fix for this problem (piston block having different data while moving.
            Bukkit.getScheduler().scheduleSyncDelayedTask(ViciousLibKit.INSTANCE,()->{
                try {
                    NMSHelper.setExtended(piston,true);
                    Piston pdat = (Piston) piston.getBlockData();
                    pdat.setExtended(true);
                    piston.setBlockData(pdat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },2);
        }
    }

    public void updatePiston() {
        NMSHelper.updatePiston(getBlock(),getPosition().getWorld());
    }
}
