package com.vicious.viciouslibkit.inventory.gui;

import com.vicious.viciouslibkit.event.Ticker;
import com.vicious.viciouslibkit.interfaces.ITickable;
import org.bukkit.inventory.ItemStack;

public class TickableGUIElement extends GUIElement<TickableGUIElement> implements ITickable {
    protected Runnable onTick;
    public int timer = 0;
    public TickableGUIElement(ItemStack stack) {
        super(stack);
        Ticker.add(this);
    }

    public TickableGUIElement(ItemStack stack, boolean cancelEvent) {
        super(stack, cancelEvent);
        Ticker.add(this);
    }

    public TickableGUIElement onTick(Runnable tickExecutable){
        this.onTick=tickExecutable;
        return this;
    }
    @Override
    public void tick() {
        timer++;
        onTick.run();
    }
}
