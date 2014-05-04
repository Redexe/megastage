package org.megastage.components;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.megastage.ecs.World;

public class RadarEcho extends BaseComponent {
    public int type;
    
    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws DataConversionException {
        type = getIntegerValue(element, "echo", 0);
        
        return null;
    }

    public String toString() {
        return "RadarEcho(" + type + ")";
    }
}
