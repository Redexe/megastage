package org.megastage.components.dcpu;

import com.esotericsoftware.minlog.Log;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.components.Position;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class VirtualPPS extends DCPUHardware {
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_PPS, 0x6509, MANUFACTORER_TALON_NAVIGATION);
        
        return null;
    }

    @Override
    public void interrupt(DCPU dcpu) {
        Log.info("" + (int) dcpu.registers[0]);
        switch(dcpu.registers[0]) {
            case 0:
                if(getSectorNumber(dcpu)) {
                    // _
                } else {
                    // _
                }
                break;
            case 1:
                if(storeCoordinates(shipEID, dcpu, World.INSTANCE.time)) {
                    dcpu.cycles += 7;
                } else {
                    // _
                }
                break;
        }
    }
    
    private boolean storeCoordinates(int ship, DCPU dcpu, long time) {
        writeCoordinatesToMemory(time, dcpu.ram, dcpu.registers[1], ship);
        return true;
    }
    
    private boolean writeCoordinatesToMemory(long time, char[] mem, char ptr, int ship) {
        Position position = (Position) World.INSTANCE.getComponent(ship, CompType.Position);

        long x = position.x / 100000; // 100m
        mem[ptr++] = (char) (x >> 16);
        mem[ptr++] = (char) x;

        long y = position.y / 100000; // 100m
        mem[ptr++] = (char) (y >> 16);
        mem[ptr++] = (char) y;

        long z = position.z / 100000; // 100m
        mem[ptr++] = (char) (z >> 16);
        mem[ptr++] = (char) z;

        mem[ptr++] = (char) time;
        
        return true;
    }

    private boolean getSectorNumber(DCPU dcpu) {
        Log.info("");
        dcpu.registers[1] = 0x0000;
        return true;
    }
}
