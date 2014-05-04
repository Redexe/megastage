package org.megastage.client.controls;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import org.megastage.client.ClientGlobals;
import org.megastage.components.transfer.RadarTargetData;
import org.megastage.ecs.CompType;

public class LookAtControl extends AbstractControl {
    private final int eid;
    
    public LookAtControl(int eid) {
         this.eid = eid; 
    }

    @Override
    protected void controlUpdate(float tpf) {
        if(eid == 0) {
            return;
        }
        
        RadarTargetData rtd = (RadarTargetData) ClientGlobals.world.getComponent(eid, CompType.RadarTargetData);
        if(rtd == null || rtd.eid == 0) return;

        Node tn = ClientGlobals.spatialManager.getNode(rtd.eid);
        
        spatial.lookAt(tn.getWorldTranslation(), Vector3f.UNIT_Y.clone());
        //spatial.lookAt(new Vector3f(0,10000,0), Vector3f.UNIT_Y.clone());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
