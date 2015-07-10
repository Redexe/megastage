package org.megastage.components.transfer;

import org.megastage.components.client.ClientVideoMemory;
import org.megastage.components.dcpu.LEMUtil;
import org.megastage.ecs.CompType;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.World;
import org.megastage.util.RAM;

public class MonitorData extends BaseComponent {
    public char videoAddr = 0x8000;
    public RAM video = new RAM(LEMUtil.defaultVideo);

    public char fontAddr = 0x0000;
    public RAM font = new RAM(LEMUtil.defaultFont);

    public char paletteAddr = 0x0000;
    public RAM palette = new RAM(LEMUtil.defaultPalette);

    @Override
    public void receive(int eid, int cid) {
        ClientVideoMemory videoMemory = World.INSTANCE.getOrCreateComponent(eid, CompType.ClientVideoMemory, ClientVideoMemory.class);
        videoMemory.update(this);
    }
}
