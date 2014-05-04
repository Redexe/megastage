package org.megastage.components.gfx;

import com.esotericsoftware.kryonet.Connection;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;

public class ImposterGeometry extends BaseComponent {
    public float radius;
    public double cutoff;
    public float red, green, blue, alpha;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        radius = getFloatValue(element, "radius", 20.0f);
        cutoff = getDoubleValue(element, "cutoff", 500000.0);
        red = getFloatValue(element, "red", 1.0f); 
        green = getFloatValue(element, "green", 1.0f); 
        blue = getFloatValue(element, "blue", 1.0f); 
        alpha = getFloatValue(element, "alpha", 1.0f); 
        
        return null;
    }

    @Override
    public Message replicate(int eid) {
        return always(eid);
    }
    
    @Override
    public void receive(World world, Connection pc, int eid) {
        ClientGlobals.spatialManager.setupImposter(eid, this);
        super.receive(world, pc, eid);
    }
    
    @Override
    public void delete(World world, Connection pc, int eid) {
        ClientGlobals.spatialManager.deleteEntity(eid);
        world.deleteEntity(eid);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ImposterGeometry(");
        sb.append("radius=").append(radius);
        sb.append("cutoff=").append(cutoff);
        sb.append(", red=").append(red);
        sb.append(", green=").append(green);
        sb.append(", blue=").append(blue);
        sb.append(", alpha=").append(alpha);
        sb.append(")");
        return sb.toString();
    }
}
