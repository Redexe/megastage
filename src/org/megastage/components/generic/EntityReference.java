package org.megastage.components.generic;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.ecs.DirtyComponent;
import org.megastage.server.TemplateManager;
import org.megastage.util.XmlUtil;

public class EntityReference extends DirtyComponent {
    public int eid;

    public void config(Element elem) {
        eid = TemplateManager.resolver.get(XmlUtil.getStringValue(elem, "name"));
    }

    public void setEid(int eid) {
        if(this.eid != eid) {
            this.eid = eid;
            this.dirty = true;
        }
    }
}