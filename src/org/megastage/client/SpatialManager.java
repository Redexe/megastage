/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.megastage.client;

import org.megastage.client.controls.ExplosionControl;
import com.artemis.Entity;
import com.cubes.BlockTerrainControl;
import com.cubes.test.blocks.Block_Wood;
import com.esotericsoftware.minlog.Log;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.LightNode;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.PQTorus;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.BufferUtils;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import jmeplanet.FractalDataSource;
import jmeplanet.Planet;
import jmeplanet.PlanetAppState;
import jmeplanet.test.Utility;
import org.megastage.client.controls.EngineControl;
import org.megastage.client.controls.GyroscopeControl;
import org.megastage.client.controls.ImposterPositionControl;
import org.megastage.client.controls.LookAtControl;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RandomSpinnerControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.components.client.ClientRaster;
import org.megastage.components.gfx.MonitorGeometry;
import org.megastage.components.gfx.PlanetGeometry;
import org.megastage.components.gfx.CharacterGeometry;
import org.megastage.components.gfx.EngineGeometry;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.components.gfx.SunGeometry;
import org.megastage.components.UsableFlag;
import org.megastage.components.gfx.VoidGeometry;
import org.megastage.components.Explosion;
import org.megastage.components.gfx.GyroscopeGeometry;
import org.megastage.components.gfx.ImposterGeometry;
import org.megastage.components.gfx.PPSGeometry;
import org.megastage.components.gfx.RadarGeometry;
import org.megastage.util.ID;

/**
 *
 * @author Orlof
 */
public class SpatialManager {

    private final SimpleApplication app;
    private final AssetManager assetManager;

    private HashMap<Integer, Node> nodes = new HashMap<>();
    private HashMap<Node, Entity> entities = new HashMap<>();
    
    public SpatialManager(SimpleApplication app) {
        this.app = app;
        assetManager = app.getAssetManager();
        
        ExplosionNode.initialize(assetManager);
    }

    public void deleteEntity(Entity entity) {
        int id = entity.id;
        final Node node = nodes.get(id);

        if(node != null) {
            app.enqueue(new Callable() {
                @Override
                public Object call() throws Exception {
                    node.removeFromParent();
                    //TODO remove lights
                    return null;
                }
            });
        }
    }
    
    public Entity getEntity(Node node) {
        Entity entity = entities.get(node);
        if(entity == null) {
            return null;
        }

        UsableFlag use = entity.getComponent(UsableFlag.class);
        if(use != null) {
            return entity;
        }
        
        return null;
    }
    
    public Node getNode(int id) {
        Node node = nodes.get(id);
 
        if(node == null) {
            Entity entity = ClientGlobals.artemis.world.getEntity(id);
            node = new Node(ID.get(entity));
            nodes.put(id, node);
            entities.put(node, entity);
        }
        
        return node;
    }
    
    public Node getNode(Entity entity) {
        int id = entity.id;
        Node node = nodes.get(id);
 
        if(node == null) {
            node = new Node(ID.get(entity));
            nodes.put(id, node);
            entities.put(node, entity);
        }
        
        return node;
    }
    
    public void changeShip(final Entity shipEntity) {
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                leaveShip();
                enterShip(shipEntity);
                return null;
            }
        });
    }
    
    public void bindTo(final Entity parentEntity, final Entity childEntity) {
        Log.trace(ID.get(childEntity) + " to " + ID.get(parentEntity));

        Node tmp = getNode(parentEntity);
        Node main = (Node) tmp.getChild("offset");
        final Node parentNode = main == null ? tmp: main; 
        
        final Node childNode = getNode(childEntity);
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                parentNode.attachChild(childNode);
                return null;
            }
        });
    }

    private Geometry createSphere(float radius, ColorRGBA color, boolean shaded) {
        Sphere mesh = new Sphere(
                ClientGlobals.gfxSettings.SPHERE_Z_SAMPLES,
                ClientGlobals.gfxSettings.SPHERE_RADIAL_SAMPLES, 
                radius);
        
        Geometry geom = new Geometry("gfx");
        geom.setMesh(mesh);

        if(shaded) {
            Material mat = material(color, true);
            mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/rock.jpg")); // with Lighting.j3md
            geom.setMaterial(mat);               // Use new material on this Geometry.
        } else {
            geom.setMaterial(material(color, false));
        }

        return geom;
    }

    private Material getMaterial(String name) {
        Material mat = material(ColorRGBA.Gray, true);
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/" + name));
        return mat;
    }
    
    public void setupSunLikeBody(final Entity entity, final SunGeometry data) {
        Log.info(data.toString());

        ColorRGBA colorRGBA = new ColorRGBA(data.red, data.green, data.blue, data.alpha);

        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));
        
        node.attachChild(createSphere(data.radius, colorRGBA, false));

        final PointLight light = new PointLight();
        light.setColor(colorRGBA);
        light.setRadius(data.lightRadius);

        LightNode lightNode = new LightNode(entity.toString() + " LightNode", light);
        node.attachChild(lightNode);

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                ClientGlobals.rootNode.addLight(light);
                ClientGlobals.sysMovNode.attachChild(node);
                return null;
            }
        });
    }

    public void setupPlanetLikeBody(Entity entity, PlanetGeometry data) {
        // Add planet
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        if(ClientGlobals.gfxSettings.ENABLE_PLANETS) {
            final Planet planet = createPlanet(data);
            node.attachChild(planet);

            app.enqueue(new Callable() {
                @Override
                public Object call() throws Exception {
                    PlanetAppState appState = app.getStateManager().getState(PlanetAppState.class);
                    if(appState != null) appState.addPlanet(planet);

                    if(node.getParent() == null) {
                        ClientGlobals.sysMovNode.attachChild(node);
                    }
                    return null;
                }
            });
        } else {
            ColorRGBA color = null;
            try {
                color = (ColorRGBA) ColorRGBA.class.getDeclaredField(data.color).get(null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            node.attachChild(createSphere(data.radius, color, true));
            app.enqueue(new Callable() {
                @Override
                public Object call() throws Exception {
                    if(node.getParent() == null) {
                        ClientGlobals.sysMovNode.attachChild(node);
                    }
                    return null;
                }
            });
        }
    }
    
    public void setupVoidNode(Entity entity, VoidGeometry data) {
        getNode(entity);
    }
    
    public void setupShip(Entity entity, ShipGeometry data) {
        Log.info("" + entity.toString());

        BlockTerrainControl blockControl = new BlockTerrainControl(ClientGlobals.cubesSettings, data.map.getChunkSizes());
        for(int x = 0; x <= data.map.xsize; x++) {
            for(int y = 0; y <= data.map.ysize; y++) {
                for(int z = 0; z <= data.map.zsize; z++) {
                    if(data.map.get(x, y, z) == '#') {
                        blockControl.setBlock(x, y, z, Block_Wood.class);
                    }
                }
            }
        }
        
        final Node offset = new Node("offset");
        offset.addControl(blockControl);
        //shipNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        node.attachChild(offset);
        offset.setLocalTranslation(data.map.getCenter().negateLocal());

        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                // it is possible that lem and other equipment is already BindTo ship
                // they have been bound to wrong node -> move to offset node
                // TODO always create main node AND offset node
                for(Spatial s: new ArrayList<>(node.getChildren())) {
                    if(!s.getName().equals("imposter") && !s.getName().equals("offset")) {
                        Log.info("MOVING FROM MAIN TO OFFSET " + s.getName());
                        offset.attachChild(s);
                    } else {
                        Log.info("NOT MOVING " + s.getName());
                    }
                }

                if(node.getParent() == null) {
                    ClientGlobals.sysMovNode.attachChild(node);
                }
                return null;
            }
        });
    }

    public void setupEngine(Entity entity, EngineGeometry data) {
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        Node burn = (Node) assetManager.loadModel("Scenes/testScene.j3o"); 
        ParticleEmitter emitter = (ParticleEmitter) burn.getChild("Emitter");
        emitter.addControl(new EngineControl(entity));
        emitter.setEnabled(true);
        node.attachChild(burn);

        Geometry geom = new Geometry("", new Cylinder(16, 16, 0.5f, 1, true));
        geom.setMaterial(material(ColorRGBA.Gray, true));
        
        node.attachChild(geom);
    }
    
    public void setupMonitor(Entity entity, MonitorGeometry data) {
        Log.info("LEM for entity " + ID.get(entity));
        Geometry geom = new Geometry(entity.toString(), new Quad(data.width, data.height, true));
        
        BufferedImage img = new BufferedImage(128, 96, BufferedImage.TYPE_INT_ARGB);
        Image img2 = new AWTLoader().load(img, false);
        ImageRaster raster = ImageRaster.create(img2);
        
        Texture2D tex = new Texture2D(img2);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setMinFilter(Texture.MinFilter.Trilinear);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        geom.setMaterial(mat);
        
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));
        Log.info("LEM node " + node.toString());

        node.attachChild(geom);
        geom.setLocalTranslation(-0.5f, -0.5f, 0f);

        ClientRaster rasterComponent = ClientGlobals.artemis.getComponent(entity, ClientRaster.class);
        rasterComponent.raster = raster;
    }

    public void setupCharacter(Entity entity, CharacterGeometry data) {
        Material mat = material(new ColorRGBA(data.red, data.green, data.blue, data.alpha), true);

        Geometry body = new Geometry(entity.toString(), new Box(0.25f, 0.5f, 0.25f));
        body.setMaterial(mat);

        Geometry head = new Geometry(entity.toString(), new Box(0.25f, 0.25f, 0.25f));
        head.setMaterial(mat);
        head.setLocalTranslation(0, 1.0f, 0);
        
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        node.attachChild(body);
        node.attachChild(head);
    }

    private Material material(ColorRGBA color, boolean lighting) {
        ColorRGBA c = new ColorRGBA(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() * .3f);
        if(lighting) {
            Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
            mat.setBoolean("UseMaterialColors",true);
            mat.setColor("Ambient", c);
            mat.setColor("Diffuse", c);
            return mat;
        } else {
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", color);
            return mat;           
        }
    }
    
    private Planet createPlanet(PlanetGeometry data) {
        Log.info(data.toString());
        if(data.generator.equalsIgnoreCase("Earth")) {
            FractalDataSource planetDataSource = new FractalDataSource(4);
            planetDataSource.setHeightScale(data.radius / 100f);
            return Utility.createEarthLikePlanet(assetManager, data.radius, null, planetDataSource);
        } else if(data.generator.equalsIgnoreCase("Moon")) {
            FractalDataSource planetDataSource = new FractalDataSource(4);
            planetDataSource.setHeightScale(data.radius / 20f);
            return Utility.createMoonLikePlanet(assetManager, data.radius, planetDataSource);
        } else if(data.generator.equalsIgnoreCase("Water")) {
            return Utility.createWaterPlanet(assetManager, data.radius, null);
        } 

        throw new RuntimeException("Unknown planet generator: " + data.generator);
    }

    private void leaveShip() {
        Entity shipEntity = ClientGlobals.shipEntity;
        
        if(shipEntity != null) {
            Log.debug(shipEntity.toString());

            ClientGlobals.shipEntity = null;

            Node shipNode = getNode(shipEntity);
            ClientGlobals.sysMovNode.attachChild(shipNode);
            ClientGlobals.fixedNode.attachChild(ClientGlobals.playerNode);
        }
    }

    private void enterShip(Entity shipEntity) {
        Log.info(shipEntity.toString());

        ClientGlobals.shipEntity = shipEntity;

        Node shipNode = getNode(shipEntity);
        ClientGlobals.fixedNode.attachChild(shipNode);
        ((Node) shipNode.getChild("offset")).attachChild(ClientGlobals.playerNode);
    }

    public void setupPlayer(final Entity entity) {
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                ClientGlobals.playerNode.addControl(new PositionControl(entity));
                ClientGlobals.playerNode.addControl(new RotationControl(entity));
                return null;
            }
        });
    }

    public void setupExplosion(final Entity entity, final Explosion explosion) {
        final ExplosionNode node = new ExplosionNode("ExplosionFX");
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                Log.info("Attached explosion node " + entity + " " + node);
                getNode(entity).attachChild(node);
                node.addControl(new ExplosionControl(explosion, node));
                return null;
            }
        });
    }

    
    public void imposter(Entity entity, boolean gfxVisible) {
        Node node = nodes.get(entity.id);

        if(node == null) return;
        
        for(Spatial s: node.getChildren()) {
            Log.trace(ID.get(entity) + s.getName() + " " + s.getCullHint());
            if(s.getName().equals("imposter")) {
                boolean imposterVisible = !gfxVisible;
                boolean imposterDraw = draw(s);
                if(!imposterVisible && imposterDraw) {
                    if(Log.INFO) Log.info(ID.get(entity) + "imposter disabled");

                    s.setCullHint(Spatial.CullHint.Always);
                    s.getParent().getControl(ImposterPositionControl.class).setEnabled(false);
                    s.getParent().getControl(PositionControl.class).setEnabled(true);
                } else if(imposterVisible && !imposterDraw) {
                    if(Log.INFO) Log.info(ID.get(entity) + "imposter enabled");

                    s.setCullHint(Spatial.CullHint.Inherit);
                    s.getParent().getControl(ImposterPositionControl.class).setEnabled(true);
                    s.getParent().getControl(PositionControl.class).setEnabled(false);
                }
            } else {
                boolean gfxDraw = draw(s);
                if(gfxVisible && !gfxDraw) {
                    if(Log.INFO) Log.info(ID.get(entity) + "gfx enabled");

                    s.setCullHint(Spatial.CullHint.Inherit);
                } else if(!gfxVisible && gfxDraw) {
                    if(Log.INFO) Log.info(ID.get(entity) + "gfx disabled");

                    s.setCullHint(Spatial.CullHint.Always);
                }
            }
        }
    }
    
    private boolean draw(Spatial s) {
        return s.getCullHint() != Spatial.CullHint.Always;
    }
    
    private Geometry createImposter(float size, ColorRGBA color) {
        Mesh q = new Mesh();

        Vector3f [] vertices = new Vector3f[] { new Vector3f(0,0,0) };
        q.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));

        q.setMode(Mesh.Mode.Points);
        q.setPointSize(size);
        q.updateBound();
        q.setStatic();

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        
        Geometry geom = new Geometry("imposter", q);
        geom.setMaterial(mat);

        return geom;
    }

    public void setupImposter(final Entity entity, final ImposterGeometry data) {
        ColorRGBA colorRGBA = new ColorRGBA(data.red, data.green, data.blue, data.alpha);

        final Geometry imposter = createImposter(data.radius, colorRGBA);
        final Node node = getNode(entity);
        
        app.enqueue(new Callable() {
            @Override
            public Object call() throws Exception {
                node.addControl(new ImposterPositionControl(entity));
                node.attachChild(imposter);
                return null;
            }
        });
    }

    public void setupPPS(Entity entity, PPSGeometry aThis) {
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        Geometry base = new Geometry("base", new Box(0.5f, 0.05f, 0.5f));
        //base.setMaterial(material(new ColorRGBA(0.7f, 0.7f, 0.7f, 0.5f), true));
        base.setMaterial(getMaterial("rock09.jpg"));
        base.setLocalTranslation(0,-0.45f,0);
        node.attachChild(base);
        
        Geometry spinner = new Geometry("spinner", new Torus(12, 12, 0.05f, 0.2f));
        spinner.setMaterial(material(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f), true));
        node.attachChild(spinner);
        
        spinner.addControl(new RandomSpinnerControl());
    }

    public void setupRadar(Entity entity, RadarGeometry aThis) {
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));
        node.addControl(new RotationControl(entity));

        Geometry base = new Geometry("base", new Box(0.5f, 0.05f, 0.5f));
        //base.setMaterial(material(new ColorRGBA(0.7f, 0.7f, 0.7f, 0.5f), true));
        base.setMaterial(getMaterial("rock09.jpg"));
        base.setLocalTranslation(0,-0.45f,0);
        node.attachChild(base);

        Node spinnerRotator = new Node("Spinner Align");
        node.attachChild(spinnerRotator);
        
        Node spinner = new Node("spinner");
        spinnerRotator.attachChild(spinner);
        Quaternion t = new Quaternion().fromAngles((float) (-Math.PI / 2.0), 0, 0);
        spinner.setLocalRotation(t);
        
        Geometry inside = new Geometry("inside", new Dome(Vector3f.ZERO, 12, 12, 0.38f, true));
        inside.setMaterial(material(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f), false));
        spinner.attachChild(inside);

        Geometry outside = new Geometry("outside", new Dome(Vector3f.ZERO, 12, 12, 0.39f, false));
        outside.setMaterial(material(new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f), true));
        spinner.attachChild(outside);
        
        spinnerRotator.addControl(new LookAtControl(entity));
    }

    public void setupGyroscope(Entity entity, GyroscopeGeometry aThis) {
        final Node node = getNode(entity);
        node.addControl(new PositionControl(entity));

        Geometry base = new Geometry("base", new Box(0.5f, 0.05f, 0.5f));
        //base.setMaterial(material(new ColorRGBA(0.7f, 0.7f, 0.7f, 0.5f), true));
        base.setMaterial(getMaterial("rock09.jpg"));
        base.setLocalTranslation(0, -0.45f, 0);
        node.attachChild(base);

        Geometry wheel = new Geometry("wheel", new Cylinder(5, 5, 0.35f, 0.35f, 0.45f, true, false));
        wheel.setMaterial(getMaterial("rock09.jpg"));
        node.attachChild(wheel);

        wheel.addControl(new GyroscopeControl(entity));
        wheel.addControl(new RotationControl(entity));
    }
}
