package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.minlog.Log;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;

import java.io.*;
import org.megastage.util.GlobalTime;

/**
 * Experimental 1.7 update to Notch's 1.4 emulator
 *
 * @author Notch, Herobrine
 */
public class DCPU extends BaseComponent {
    public transient static final int HZ = 100000;

    public int hz = HZ;
    public int hardwareTickInterval = hz / 60;
    
    public int shipEID;
    public String rom;
    
    public EntityList hardware = new EntityList();

    final public char[] ram = new char[65536];
    final public char[] registers = new char[8];

    public char pc;
    public char sp;
    public char ex;
    public char ia;

    public long powerOnTime;
    public long startupTime;
    public long nextHardwareTick;
    public long cycles;

    public boolean isSkipping = false;
    public boolean isOnFire = false;
    public boolean queueingEnabled = false; //TODO: Verify implementation
    public char[] interrupts = new char[256];
    public int ip;
    public int iwp;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws IOException {
        this.shipEID = parent.id;

        rom = getStringValue(element, "bootrom", "media/bootrom.bin");

        load(new File(rom));

        powerOnTime = GlobalTime.value + 2500;
        startupTime = powerOnTime + 2500;
        nextHardwareTick = hardwareTickInterval;
        
        return null;
    }

    public void interrupt(char a) {
        interrupts[iwp = iwp + 1 & 0xFF] = a;
        if (iwp == ip) {
            isOnFire = true;
        }
    }
    
    public void skip() {
        isSkipping = true;
    }

    public void reset(char[] image) {
        System.arraycopy(image, 0, ram, 0, image.length);

        powerOnTime = GlobalTime.value + 2500;
        startupTime = powerOnTime + 2500;
        
        for(int i=0; i < registers.length; i++) {
            registers[i]=0;
        }
        for(int i=0; i < interrupts.length; i++) {
            interrupts[i]=0;
        }
        pc=sp=ex=ia=0;
        isSkipping = isOnFire = queueingEnabled = false;
        ip=iwp=0;
        cycles = 0;
        nextHardwareTick = hardwareTickInterval;
    }
    
    public boolean connectHardware(int eid) {
        hardware.add(eid);
        return true;
    }

    public void load(File file) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        int i= 0;

        try {
            for (; i < ram.length; i++) {
                ram[i] = dis.readChar();
            }
        } catch (IOException e) {}

        Log.info("Boot image: " + i + " words");
        
        try { dis.close(); } catch (IOException e) {}
        
        for(; i < ram.length; i++) {
            ram[i] = 0;
        }
    }

    public void save(File file) throws IOException {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        try {
            for (int i = 0; i < ram.length; i++) {
                dos.writeChar(ram[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        dos.close();
    }


}
