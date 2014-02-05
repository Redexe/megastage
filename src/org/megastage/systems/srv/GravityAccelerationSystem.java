package org.megastage.systems.srv;

import org.megastage.systems.srv.GravityFieldSystem;
import org.megastage.components.srv.Acceleration;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import org.megastage.components.*;
import org.megastage.util.Vector3d;

public class GravityAccelerationSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<Acceleration> ACCELERATION;
    @Mapper ComponentMapper<Position> POSITION;

    private GravityFieldSystem gravityFieldSystem;

    public GravityAccelerationSystem() {
        super(Aspect.getAspectForAll(Acceleration.class, Position.class));
    }

    @Override
    public void initialize() {
        gravityFieldSystem = world.getSystem(GravityFieldSystem.class);
    }

    @Override
    protected void process(Entity entity) {
        Position position = POSITION.get(entity);
        Acceleration acceleration = ACCELERATION.get(entity);

        Vector3d gravityField = gravityFieldSystem.getGravityField(position);
        acceleration.add(gravityField);
    }
}
