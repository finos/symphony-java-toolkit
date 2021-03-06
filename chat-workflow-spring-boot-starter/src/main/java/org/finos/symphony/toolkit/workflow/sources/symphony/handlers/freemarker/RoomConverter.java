package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.form.RoomList;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RoomConverter extends AbstractClassConverter implements ApplicationContextAware {

	public RoomConverter() {
		super(LOW_PRIORITY, Room.class);
	}

	private SymphonyRooms rooms;
	private ApplicationContext ctx;
	
	@Override
	public String apply(Type t, boolean editMode, Variable v, EntityJson ej) {
		
		if (rooms == null) {
			// this is done late-binding to avoid dependency loops in spring.
			rooms = ctx.getBean(SymphonyRooms.class);
		}
		
		ej.putIfAbsent("room", new RoomList(rooms.getAllRooms()));
		
		if (editMode) {
			StringBuilder out = new StringBuilder();
			out.append(indent(v.depth) + "<select "+ attribute(v, "name", v.getFormFieldName()));
			out.append(attribute(v, "required", "false"));
			out.append(attribute(v, "data-placeholder", "Choose "+v.getDisplayName()));
			out.append(">");
			out.append(indent(v.depth) + "<#list entity.rooms as r>");
			out.append(indent(v.depth) + "<option ");
			out.append(attribute(v, "value", "hi"));
			out.append(attributeParam(v, "selected", "bingo"));
			out.append(indent(v.depth) + ">");
			out.append("</option>");
			out.append("</select>");
			return out.toString();
		} else {
			return text(v, "!''");
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

	
}
