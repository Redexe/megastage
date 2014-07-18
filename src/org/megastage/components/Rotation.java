package org.megastage.components;

import com.jme3.math.Quaternion;
import org.megastage.ecs.BaseComponent;
import org.jdom2.Element;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.ReplicatedComponent;
import org.megastage.ecs.World;

public class Rotation extends ReplicatedComponent {
    public Quaternion value = Quaternion.IDENTITY;

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        float x = (float) Math.toRadians(getFloatValue(element, "x", 0.0f));
        float y = (float) Math.toRadians(getFloatValue(element, "y", 0.0f));
        float z = (float) Math.toRadians(getFloatValue(element, "z", 0.0f));

        value = new Quaternion().fromAngles(x, y, z);
        
        return null;
    }

    @Override
    public void receive(int eid) {
        if(eid == ClientGlobals.playerEntity) {
            if(World.INSTANCE.hasComponent(eid, CompType.Rotation)) {
                return;
            }
        }

        World.INSTANCE.setComponent(eid, CompType.Rotation, this);
    }
}
