package com.vicious.viciouslibkit.event.piston;

import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.util.nms.NMSHelper;
import com.vicious.viciouslibkit.util.vector.WorldPosI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.util.Vector;

import java.util.function.Consumer;


/**
 * Controls a piston in world.
 */
public class PistonController implements Consumer<BlockPistonEvent> {
    protected boolean allowRetraction = true;
    protected boolean allowExtension = true;
    protected boolean isSticky;
    /**
     * In extension events this will equal that of the piston.
     * In retract events this will equal that of the opposite of the piston.
     */
    protected BlockFace directionOfMovement;
    protected BlockFace pistonDirection;
    protected WorldPosI blockPosition;
    public PistonController(WorldPosI blockPosition){
        this.blockPosition=blockPosition;
        isSticky = getBlock().getType() == Material.STICKY_PISTON;
        BlockData dat = getBlock().getBlockData();
        if(dat instanceof Directional){
            pistonDirection=((Directional) dat).getFacing();
        }
    }
    public WorldPosI getPosition() {
        return blockPosition;
    }
    public Block getBlock(){
        return blockPosition.getBlock();
    }

    /**
     * Keeps a piston extended regardless of its powered state.
     * Note: on sticky pistons this requires the piston to be converted to a non-sticky one since bukkit is dumb.
     */
    public void keepExtended(){
        Block b = getBlock();
        //Unfortunately bukkit doesn't handle sticky pistons properly so we have to convert it to a normal piston to use this method.
        if(isSticky()){
            b.setType(Material.PISTON);
            Directional dat = (Directional) b.getBlockData();
            dat.setFacing(pistonDirection);
            b.setBlockData(dat);
        }
        haltRetract();
        allowExtend();
        attemptPush();
    }

    /**
     * Tries to retract the piston if possible.
     * Converts the piston back to sticky if necessary.
     */
    public void attemptRetract(){
        allowRetract();
        updatePiston();
    }

    public void haltRetract(){
        this.allowRetraction =false;
    }
    public void haltExtend(){
        this.allowExtension =false;
    }
    public void allowExtend(){
        this.allowExtension =true;
    }
    public void allowRetract(){
        this.allowRetraction =true;
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
        Block b = getBlock();
        if(isSticky() && b.getType() != Material.STICKY_PISTON){
            Block head = getHead();
            if(head == null || !(head.getBlockData() instanceof PistonHead)){
                b.setType(Material.STICKY_PISTON);
                Directional dat = (Directional) b.getBlockData();
                dat.setFacing(pistonDirection);
                b.setBlockData(dat);
            }
            else{
                NMSHelper.convertSticky(b);
                PistonHead hdat = (PistonHead) head.getBlockData();
                hdat.setType(TechnicalPiston.Type.STICKY);
                head.setBlockData(hdat);
            }
        }
        NMSHelper.updatePiston(b,getPosition().getWorld());
    }

    @Override
    public void accept(BlockPistonEvent ev) {
        this.directionOfMovement = ev.getDirection();
        if(ev instanceof BlockPistonRetractEvent){
            BlockPistonRetractEvent r = (BlockPistonRetractEvent) ev;
            r.setCancelled(!allowRetraction);
        }
        else if(ev instanceof BlockPistonExtendEvent){
            BlockPistonExtendEvent e = (BlockPistonExtendEvent) ev;
            e.setCancelled(!allowExtension);
        }
    }
    public Block getHead(){
        Vector vec = pistonDirection.getDirection();
        return blockPosition.translate(vec).getBlock();
    }
    public boolean isSticky(){
        return isSticky;
    }
}
