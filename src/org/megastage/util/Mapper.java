package org.megastage.util;

import com.artemis.ComponentMapper;
import com.artemis.World;
import org.megastage.components.CollisionSphere;
import org.megastage.components.DeleteFlag;
import org.megastage.components.Energy;
import org.megastage.components.Explosion;
import org.megastage.components.Mass;
import org.megastage.components.Mode;
import org.megastage.components.Orbit;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.SpawnPoint;
import org.megastage.components.UsableFlag;
import org.megastage.components.dcpu.DCPU;
import org.megastage.components.dcpu.VirtualEngine;
import org.megastage.components.dcpu.VirtualGyroscope;
import org.megastage.components.dcpu.VirtualMonitor;
import org.megastage.components.dcpu.VirtualRadar;
import org.megastage.components.gfx.BindTo;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.components.srv.Acceleration;
import org.megastage.components.srv.Identifier;
import org.megastage.components.srv.SphereOfInfluence;
import org.megastage.components.Velocity;
import org.megastage.components.dcpu.VirtualFloppyDrive;
import org.megastage.components.dcpu.VirtualForceField;
import org.megastage.components.dcpu.VirtualPowerController;
import org.megastage.components.dcpu.VirtualPowerPlant;
import org.megastage.components.dcpu.VirtualThermalLaser;
import org.megastage.components.transfer.EngineData;
import org.megastage.components.transfer.ForceFieldData;
import org.megastage.components.transfer.GyroscopeData;
import org.megastage.components.transfer.RadarTargetData;
import org.megastage.components.transfer.ThermalLaserData;

public class Mapper {
    public static ComponentMapper<Energy> ENERGY;
    public static ComponentMapper<VirtualPowerController> VIRTUAL_POWER_CONTROLLER;
    public static ComponentMapper<VirtualPowerPlant> VIRTUAL_POWER_PLANT;
    public static ComponentMapper<VirtualForceField> VIRTUAL_FORCE_FIELD;
    public static ComponentMapper<ForceFieldData> FORCE_FIELD_DATA;
    public static ComponentMapper<CollisionSphere> COLLISION_SPHERE;
    public static ComponentMapper<VirtualThermalLaser> VIRTUAL_THERMAL_LASER;
    public static ComponentMapper<DeleteFlag> DELETE_FLAG;
    public static ComponentMapper<Acceleration> ACCELERATION;
    public static ComponentMapper<Position> POSITION;
    public static ComponentMapper<Rotation> ROTATION;
    public static ComponentMapper<UsableFlag> USABLE_FLAG;
    public static ComponentMapper<ThermalLaserData> THERMAL_LASER_DATA;
    public static ComponentMapper<EngineData> ENGINE_DATA;
    public static ComponentMapper<GyroscopeData> GYROSCOPE_DATA;
    public static ComponentMapper<RadarTargetData> RADAR_TARGET_DATA;
    public static ComponentMapper<Explosion> EXPLOSION;
    public static ComponentMapper<Mass> MASS;
    public static ComponentMapper<DCPU> DCPU;
    public static ComponentMapper<Velocity> VELOCITY;
    public static ComponentMapper<Orbit> ORBIT;
    public static ComponentMapper<VirtualEngine> VIRTUAL_ENGINE;
    public static ComponentMapper<VirtualFloppyDrive> VIRTUAL_FLOPPY_DRIVE;
    public static ComponentMapper<VirtualGyroscope> VIRTUAL_GYROSCOPE;
    public static ComponentMapper<VirtualRadar> VIRTUAL_RADAR;
    public static ComponentMapper<VirtualMonitor> VIRTUAL_MONITOR;
    public static ComponentMapper<BindTo> BIND_TO;
    public static ComponentMapper<Identifier> IDENTIFIER;
    public static ComponentMapper<SpawnPoint> SPAWN_POINT;
    public static ComponentMapper<ShipGeometry> SHIP_GEOMETRY;
    public static ComponentMapper<Mode> MODE;
    public static ComponentMapper<SphereOfInfluence> SPHERE_OF_INFLUENCE;

    public static void init(World world) {
        ENERGY = world.getMapper(Energy.class);
        VIRTUAL_FLOPPY_DRIVE = world.getMapper(VirtualFloppyDrive.class);
        VIRTUAL_POWER_CONTROLLER = world.getMapper(VirtualPowerController.class);
        VIRTUAL_POWER_PLANT = world.getMapper(VirtualPowerPlant.class);
        VIRTUAL_FORCE_FIELD = world.getMapper(VirtualForceField.class);
        FORCE_FIELD_DATA = world.getMapper(ForceFieldData.class);
        COLLISION_SPHERE = world.getMapper(CollisionSphere.class);
        VIRTUAL_THERMAL_LASER = world.getMapper(VirtualThermalLaser.class);
        DELETE_FLAG = world.getMapper(DeleteFlag.class);
        ACCELERATION = world.getMapper(Acceleration.class);
        POSITION = world.getMapper(Position.class);
        ROTATION = world.getMapper(Rotation.class);
        VELOCITY = world.getMapper(Velocity.class);
        USABLE_FLAG = world.getMapper(UsableFlag.class);
        ENGINE_DATA = world.getMapper(EngineData.class);
        GYROSCOPE_DATA = world.getMapper(GyroscopeData.class);
        RADAR_TARGET_DATA = world.getMapper(RadarTargetData.class);
        THERMAL_LASER_DATA = world.getMapper(ThermalLaserData.class);
        EXPLOSION = world.getMapper(Explosion.class);
        MASS = world.getMapper(Mass.class);
        DCPU = world.getMapper(DCPU.class);
        ORBIT = world.getMapper(Orbit.class);
        VIRTUAL_ENGINE = world.getMapper(VirtualEngine.class);
        VIRTUAL_GYROSCOPE = world.getMapper(VirtualGyroscope.class);
        VIRTUAL_RADAR = world.getMapper(VirtualRadar.class);
        VIRTUAL_MONITOR = world.getMapper(VirtualMonitor.class);
        BIND_TO = world.getMapper(BindTo.class);
        IDENTIFIER = world.getMapper(Identifier.class);
        SPAWN_POINT = world.getMapper(SpawnPoint.class);
        SHIP_GEOMETRY = world.getMapper(ShipGeometry.class);
        MODE = world.getMapper(Mode.class);
        SPHERE_OF_INFLUENCE = world.getMapper(SphereOfInfluence.class);
    }
}
