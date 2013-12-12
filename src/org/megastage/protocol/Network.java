package org.megastage.protocol;

import com.artemis.Entity;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import org.megastage.client.controls.PositionControl;
import org.megastage.client.controls.RotationControl;
import org.megastage.components.EntityComponent;
import org.megastage.components.Mass;
import org.megastage.components.MonitorData;
import org.megastage.components.Orbit;
import org.megastage.components.FixedRotation;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.server.BindTo;
import org.megastage.components.server.MonitorGeometry;
import org.megastage.components.server.PlanetGeometry;
import org.megastage.components.server.ShipGeometry;
import org.megastage.components.server.SunGeometry;
import org.megastage.components.server.VoidGeometry;
import org.megastage.systems.ClientNetworkSystem;
import org.megastage.util.ClientGlobals;
import org.megastage.util.Globals;
import org.megastage.util.RAM;
import org.megastage.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: contko3
 * Date: 10/2/13
 * Time: 7:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class Network {
    public static String networkInterface = "localhost";

    public static String serverHost = "localhost";
    public static int serverPort = 12358;

    public static int clientPort = 0;

    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        for(Class<?> clazz: Network.class.getDeclaredClasses()) {
            kryo.register(clazz);
        }

        kryo.register(char[].class);
        kryo.register(Object[].class);
        kryo.register(EntityData.class);
        kryo.register(Mass.class);
        kryo.register(MonitorData.class);
        kryo.register(Orbit.class);
        kryo.register(FixedRotation.class);
        kryo.register(Position.class);
        kryo.register(Rotation.class);
        kryo.register(PlanetGeometry.class);
        kryo.register(SunGeometry.class);
        kryo.register(ShipGeometry.class);
        kryo.register(MonitorGeometry.class);
        kryo.register(RAM.class);
        kryo.register(BindTo.class);
        kryo.register(VoidGeometry.class);
        kryo.register(Vector.class);
        kryo.register(LoginResponse.class);
        kryo.register(UserCommand.class);
    }

    static public class Login extends EventMessage {}
    static public class Logout extends EventMessage {}
    
    static public abstract class KeyEvent extends EventMessage {
        public int key;
    }
    static public class KeyPressed extends KeyEvent {}
    static public class KeyTyped extends KeyEvent {}
    static public class KeyReleased extends KeyEvent {}

    static public class EntityData implements Message {
        public int entityID;
        public EntityComponent component;

        public EntityData() { /* required for Kryo */ }
        
        public EntityData(Entity entity, EntityComponent c) {
            entityID = entity.getId();
            component = c;
        }

        @Override
        public void receive(ClientNetworkSystem system, Connection pc) {
            Entity entity = system.cems.get(entityID);
            component.receive(system, pc, entity);
        }
        
        public String toString() {
            return "EntityData(" + entityID + ", " + component.toString() + ")";
        }
    }
    
    static public class TimeData extends EventMessage {
        public long time = Globals.time;

        @Override
        public void receive(ClientNetworkSystem system, Connection pc) {
            ClientGlobals.timeDiff = time - System.currentTimeMillis();
        }
    }
}