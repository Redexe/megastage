package org.megastage.client.controls;

import com.esotericsoftware.minlog.Log;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;
import org.megastage.client.ExplosionNode;
import org.megastage.components.Explosion;

public class ExplosionControl extends AbstractControl {
    private final ExplosionNode node;
    private final Explosion explosion;

    public ExplosionControl(Explosion explosion, ExplosionNode node) {
        this.explosion = explosion;
        this.node = node;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        if(explosion.clientState != explosion.serverState) {
            Log.info("clientState " + explosion.clientState+ " to " + explosion.serverState);
            explosion.clientState = explosion.serverState;
            switch(explosion.clientState) {
                case 0:
                    break;
                case 1:
                    node.sparks.emitAllParticles();
                    break;
                case 2:
                    node.burst.emitAllParticles();
                    node.light.setColor(ColorRGBA.Red);
                    node.light.setRadius(5000);
                    break;
                case 3:
                    node.shockwave.emitAllParticles();
                    node.fire.emitAllParticles();
                    node.embers.emitAllParticles();
                    node.smoke.emitAllParticles();
                    node.light.setColor(ColorRGBA.Yellow);
                    node.light.setRadius(10000);
                    break;
                case 4:
                    Log.trace(node.getParent().toString());
                    List<Spatial> children = new ArrayList<>( node.getParent().getChildren() );
                    for(Spatial s: children) {
                        if(s != spatial) {
                            Log.trace("remove spatial " + s.toString());
                            s.removeFromParent();
                        }
                        else {
                            Log.trace("dont remove " + s.toString());
                        }
                    }
                    break;
                case 5:
                    node.light.setColor(ColorRGBA.Red);
                    node.light.setRadius(5000);
                    node.burst.killAllParticles();
                    node.sparks.killAllParticles();
                    break;
                case 6:
                    // rewind the effect
                    node.fire.killAllParticles();
                    node.smoke.killAllParticles();
                    node.embers.killAllParticles();
                    node.shockwave.killAllParticles();
                    node.removeLight(node.light);
                    node.getParent().removeFromParent();
                    break;
                case 7:
                    break;
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}