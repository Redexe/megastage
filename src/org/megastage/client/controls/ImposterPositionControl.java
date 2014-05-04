/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Position;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.util.Vector3d;

/**
 *
 * @author Orlof
 */
public class ImposterPositionControl extends AbstractControl {
    private final int eid;

    public ImposterPositionControl(int eid) {
        this.eid = eid;
        setEnabled(false);
    }

    @Override
    protected void controlUpdate(float tpf) {
        Position position = (Position) ClientGlobals.world.getComponent(eid, CompType.Position);
        if(position != null) {
            Vector3d coord = position.getVector3d();
            
            Position origoPos = (Position) ClientGlobals.world.getComponent(ClientGlobals.shipEntity, CompType.Position);
            if(origoPos == null) return;
            
            Vector3d origo = origoPos.getVector3d();
            
            Vector3d line = coord.sub(origo);

            double distance = line.length();

            if(distance > 1000000.0) {
                double scale = 1000000.0 / distance;
                line = line.multiply(scale).add(origo);
                spatial.setLocalTranslation(line.getVector3f());
            } else {
                spatial.setLocalTranslation(coord.getVector3f());
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}

}
