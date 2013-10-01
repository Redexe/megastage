package org.megastage.components.dcpu;

import com.artemis.Entity;
import com.artemis.World;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

import java.util.logging.Logger;

public class VirtualKeyboard extends DCPUHardware {
    private final static Logger LOG = Logger.getLogger(VirtualKeyboard.class.getName());

    public static final int KEY_BACKSPACE = 16;
    public static final int KEY_RETURN = 17;
    public static final int KEY_INSERT = 18;
    public static final int KEY_DELETE = 19;
    public static final int KEY_UP = 128;
    public static final int KEY_DOWN = 129;
    public static final int KEY_LEFT = 130;
    public static final int KEY_RIGHT = 131;
    public static final int KEY_SHIFT = 144;
    public static final int KEY_CONTROL = 145;
    private KeyMapping keyMapping;
    private char[] keyBuffer = new char[64];
    private int krp;
    private int kwp;
    private boolean[] isDown = new boolean[256];
    public char interruptMessage;
    public boolean doInterrupt;

    @Override
    public void init(World world, Entity parent, Element element) throws DataConversionException {
        type = TYPE_KEYBOARD;
        revision = 0x1337;
        manufactorer = MANUFACTORER_MACKAPAR;

        super.init(world, parent, element);

        this.keyMapping = new AWTKeyMapping(true);
    }

    public void keyTyped(int key) {
        LOG.finer("key ID: " + Integer.toHexString(key));

        if (key < 20 || key >= 127) return;
        if (keyBuffer[kwp & 0x3F] == 0) {
            LOG.finer(String.format("write keyBuffer[%d]=%s", kwp, Integer.toHexString(key)));
            keyBuffer[kwp++ & 0x3F] = (char) key;
            doInterrupt = true;
        }
    }

    public void keyPressed(int key) {
        LOG.finer("key ID: " + Integer.toHexString(key));

        int i = keyMapping.getKey(key);
        if (i < 0) return;
        if ((i < 20 || i >= 127) && keyBuffer[kwp & 0x3F] == 0) {
            LOG.finer(String.format("write keyBuffer[%d]=%s", kwp, Integer.toHexString(key)));
            keyBuffer[kwp++ & 0x3F] = (char) i;
        }
        isDown[i] = true;
        doInterrupt = true;
    }

    public void keyReleased(int key) {
        LOG.finer("key ID: " + Integer.toHexString(key));

        int i = keyMapping.getKey(key);
        if (i < 0) return;
        isDown[i] = false;
        doInterrupt = true;
    }

    public void interrupt() {
        int a = dcpu.registers[0];
        if (a == 0) {
            LOG.finest("reset keyBuffer");
            for (int i = 0; i < keyBuffer.length; i++) {
                keyBuffer[i] = 0;
            }
            krp = 0;
            kwp = 0;
        } else if (a == 1) {
            dcpu.registers[2] = keyBuffer[(krp & 0x3F)];
            if (dcpu.registers[2] != 0) {
                LOG.finest(String.format("read %s = keyBuffer[%d]", Integer.toHexString(dcpu.registers[2]), krp));
                keyBuffer[(krp++ & 0x3F)] = 0;
            }
        } else if (a == 2) {
            int key = dcpu.registers[1];
            if ((key >= 0) && (key < 256))
                dcpu.registers[2] = (char) (isDown[key] ? 1 : 0);
            else
                dcpu.registers[2] = 0;
        } else if (a == 3) {
            interruptMessage = dcpu.registers[1];
        }
    }

    @Override
    public void tick60hz() {
        if (doInterrupt) {
            if (interruptMessage != 0) dcpu.interrupt(interruptMessage);
            doInterrupt = false;
        }
    }


}
