package org.megastage.components;

import org.megastage.client.ClientGlobals;
import org.megastage.ecs.BaseComponent;
import org.megastage.client.ClientMode;

public class Mode extends BaseComponent {
    public ClientMode value = ClientMode.WALK; 

    public void setMode(ClientMode newValue) {
        if(value != newValue) {
            value = newValue;
            dirty = true;
        }
    }

    @Override
    public void receive(int eid) {
        super.receive(eid);
        
        if(ClientGlobals.playerEntity == eid) {
            ClientMode.change(value);
        }
    }
}
