package org.megastage.util;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.cubes.*;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.*;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import java.util.Random;
import org.megastage.client.CubesManager;

public class CollisionTesterNew2 extends SimpleApplication {

    public static final int SIZE = 1;

    public static void main(String[] args) {
        CollisionTesterNew2 app = new CollisionTesterNew2();
        app.start();
    }

    private Ship ship;
    private Node shipNode;
    private Node terrainNode;
    private BlockTerrainControl ctrl;

    public CollisionTesterNew2() {
        showSettings = false;
        settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(400);
    }

    @Override
    public void simpleInitApp() {
        CubesManager.init(this);
        ctrl = CubesManager.getControl(1);
        ctrl.setBlock(0, 0, 0, CubesManager.getBlock('#'));

        ship = new Ship(1);
        ship.setBlock(new Vector3Int(0, 0, 0), '#');

        Log.info("Center: %s", ship.getCenterOfMass());

        shipNode = new Node();
        rootNode.attachChild(shipNode);

        terrainNode = new Node();
        terrainNode.setLocalTranslation(-SIZE / 2.0f, -SIZE / 2.0f, -SIZE / 2.0f);
        terrainNode.addControl(ctrl);

        shipNode.attachChild(terrainNode);

        cam.setLocation(new Vector3f(0, 0, 10));
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(10);

        inputManager.addMapping("pick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("rot", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "pick");
        inputManager.addListener(actionListener, "rot");
         
        createCrosshair();
    }

    private void createCrosshair() {
        Picture pic = new Picture("HUD Picture");
        pic.setImage(assetManager, "Textures/red_crosshair.png", true);
        pic.setWidth(100);
        pic.setHeight(100);
        pic.setPosition(settings.getWidth() / 2 - 50, settings.getHeight() / 2 - 50);
        guiNode.attachChild(pic);
    }
    Vector3Int prevCollision;

    @Override
    public void simpleUpdate(float tpf) {
        if(rotate) {
            shipNode.rotate(0, tpf, 0);
        }
    }

    private CollisionResults getRayCastingResults(Node node) {
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        CollisionResults results = new CollisionResults();
        node.collideWith(ray, results);
        return results;
    }

    private Vector3Int getCurrentPointedBlockLocation(boolean getNeighborLocation) {
        CollisionResults results = getRayCastingResults(terrainNode);
        if (results.size() > 0) {
            Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint().subtract(terrainNode.getWorldTranslation());
            Log.info("%s", collisionContactPoint);
            return BlockNavigator.getPointedBlockLocation(ctrl, collisionContactPoint, getNeighborLocation);
        }
        return null;
    }

    public void initGeometry() {
        int size = ship.getSize();

        terrainNode.removeControl(BlockTerrainControl.class);
        ctrl = CubesManager.getControl(size);
        terrainNode.addControl(ctrl);

        // convert block map to Cubes control
        for(int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
                for(int z = 0; z < size; z++) {
                    char c = ship.getBlock(x, y, z);
                    Class<? extends Block> block = CubesManager.getBlock(c);
                    if(block != null) {
                        ctrl.setBlock(x, y, z, block);
                    }
                }
            }
        }
    }

    boolean rotate = false;
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("pick") && !isPressed) {
                Vector3Int col = getCurrentPointedBlockLocation(true);
                Log.info("%s", col);

                if (col != null) {
                    Vector3f com = ship.getCenterOfMass();
                    int majorVersion = ship.majorVersion;
                    ship.setBlock(col, '#');
                    Log.info("MajorVersion: %d -> %d", majorVersion, ship.majorVersion);
                    Log.info("Center of Mass: %s -> %s", com, ship.getCenterOfMass());
                    
                    shipNode.move(ship.getPrevDelta());

                    if(majorVersion == ship.majorVersion) {
                        ctrl.setBlock(col, CubesManager.getBlock('#'));
                    } else {
                        initGeometry();
                    }
                }
            }
            if (name.equals("rot")) {
                rotate = isPressed;
            }
        }
    };
}