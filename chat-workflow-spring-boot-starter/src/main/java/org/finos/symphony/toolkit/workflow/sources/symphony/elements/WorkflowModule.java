package org.finos.symphony.toolkit.workflow.sources.symphony.elements;

import java.io.IOException;

import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.node.LongNode;

/**
 * Handles conversion of symphony elements' user picker back to User objects.
 * @author Rob Moffat
 *
 */
public class WorkflowModule extends Module {

	private static final String NAME = "Symphony Workflow Module";
	private static final Version VERSION = new Version(1, 0, 0, "", 
			WorkflowModule.class.getPackage().getName().toLowerCase(), 
			"workflow-module");


	@Override
	public String getModuleName() {
		return NAME;
	}

	@Override
	public Version version() {
		return VERSION;
	}
	
	private SymphonyRooms rooms;
	
	public WorkflowModule(SymphonyRooms rooms) {
		super();
		this.rooms = rooms;
	}

	@Override
	public void setupModule(SetupContext context) {
		context.addDeserializers(new Deserializers.Base() {

			@Override
			public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
					BeanDescription beanDesc) throws JsonMappingException {
				
				if (User.class.isAssignableFrom(type.getRawClass())) {
					return new JsonDeserializer<User>() {

						@Override
						public User deserialize(JsonParser p, DeserializationContext ctxt)
								throws IOException, JsonProcessingException {
							
							TreeNode tn = p.readValueAsTree();
							if (tn.size() > 0) {
								long ul = ((LongNode) tn.get(0)).asLong();
								return rooms.loadUserById(ul);
							} else {
								return null;
							}
							
						}
					};
				} else if (Room.class.isAssignableFrom(type.getRawClass())) {
					return new JsonDeserializer<Room>() {

						@Override
						public Room deserialize(JsonParser p, DeserializationContext ctxt)
								throws IOException, JsonProcessingException {
							return rooms.loadRoomById(p.getValueAsString());
						}
					};
					
				} else {
					return null;
				}
			}
		});
	}

}
