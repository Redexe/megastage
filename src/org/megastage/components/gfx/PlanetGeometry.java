/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.components.gfx;

import com.artemis.Entity;
import com.artemis.World;
import com.esotericsoftware.kryonet.Connection;
import java.util.concurrent.Callable;
import org.jdom2.Element;
import org.megastage.components.BaseComponent;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.client.ClientGlobals;


    
/**
 *
 * @author Orlof
 */
public class PlanetGeometry extends BaseComponent {
    public int center;
    public float radius;
    public String generator;
    public String color;

    @Override
    public BaseComponent[] init(World world, Entity parent, Element element) throws Exception {
        center = parent.getId();

        radius = getFloatValue(element, "radius", 10.0f);
        generator = getStringValue(element, "generator", "Earth");
        color = getStringValue(element, "color", "red");
        
        return null;
    }

    @Override
    public boolean replicate() {
        return true;
    }
    
    @Override
    public void receive(Connection pc, Entity entity) {
        // center = ClientGlobals.artemis.get(center).getId();
        ClientGlobals.spatialManager.setupPlanetLikeBody(entity, this);
    }
    
    public String toString() {
        return "PlanetGeometry(center=" + center + ", generator='" + generator + "', radius=" + radius + ")";
    }
}