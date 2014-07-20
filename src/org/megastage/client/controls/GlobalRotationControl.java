package org.megastage.client.controls;

import com.jme3.math.Quaternion;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import org.megastage.components.Rotation;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class GlobalRotationControl extends AbstractControl {
    
    public GlobalRotationControl() {
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(ClientGlobals.baseEntity == 0) {
            spatial.setLocalRotation(Quaternion.IDENTITY);
            return;
        }

        Rotation rot = (Rotation) World.INSTANCE.getComponent(ClientGlobals.baseEntity, CompType.Rotation);
        if(rot == null) {
            spatial.setLocalRotation(Quaternion.IDENTITY);
            return;
        }

        spatial.setLocalRotation(rot.get().inverse());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}

