package org.finos.symphony.toolkit.workflow;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.fixture.TestObjects;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.MessageHistory;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.symphony.api.model.MessageSearchQuery;
import com.symphony.api.model.V4Message;
import com.symphony.api.model.V4MessageList;

public class TestHistory extends AbstractMockSymphonyTest {
	
	
	MessageHistory mh;
	
	@Autowired
	Workflow wf;
	
	@MockBean
	SymphonyRooms ru;
	
	EntityJsonConverter ejc;
	
	@BeforeEach
	public void setup() {
		ejc = new EntityJsonConverter(wf);
		mh = new MessageHistory(wf, ejc, messagesApi, ru);
	}
	

	@Test
	public void testGetLast() {
		TestObjects to = new TestObjects();
		Mockito.when(messagesApi.v1MessageSearchPost(Mockito.any(), 
			Mockito.isNull(), Mockito.isNull(), Mockito.anyInt(), 
			Mockito.anyInt(), Mockito.any(), Mockito.any())).thenAnswer(a -> {
				V4MessageList out = new V4MessageList();
				out.addAll(Arrays.asList(makeMessage(to)));
					
				return out;
			});
		
		TestObjects out = mh.getLastFromHistory(TestObjects.class, new RoomDef("someroom", "", true, "abc123"))
			.orElseThrow(() -> new RuntimeException());
		
		Assertions.assertEquals(out, to);
		Assertions.assertFalse(out == to);
	}
	
	@Test
	public void testFindInHistory() {
		
		TestObjects one = new TestObjects();
		TestObjects two = new TestObjects();
		TestObjects three = new TestObjects();

		Mockito.when(messagesApi.v1MessageSearchPost(Mockito.any(), 
			Mockito.isNull(), Mockito.isNull(), Mockito.anyInt(), 
			Mockito.anyInt(), Mockito.any(), Mockito.any())).thenAnswer(a -> {
				V4MessageList out = new V4MessageList();
				out.addAll(Arrays.asList(makeMessage(one), makeMessage(two), makeMessage(three)));
					
				return out;
			});
		
		
		List<TestObjects> out = mh.getFromHistory(
				TestObjects.class, 
				new RoomDef("someroom", "", true, "abc123"),
				Instant.now().minus(10, ChronoUnit.DAYS));
		
		Assertions.assertEquals(3, out.size());
		Assertions.assertEquals(one, out.get(0));
		Assertions.assertEquals(two, out.get(1));
		Assertions.assertEquals(three, out.get(2));
		
		Mockito.verify(messagesApi).v1MessageSearchPost(
			Mockito.argThat(e -> {
				MessageSearchQuery msq = (MessageSearchQuery) e;
				return msq.getHashtag().equals(TestObjects.class.getCanonicalName().replace(".", "-").toLowerCase());
			}),
			Mockito.isNull(),
			Mockito.isNull(),
			Mockito.eq(0), 
			Mockito.eq(50), 
			Mockito.isNull(),
			Mockito.isNull());
		
	}


	protected V4Message makeMessage(TestObjects one) {
		return new V4Message()
			.message("some stuff")
			.data(ejc.toWorkflowJson(one));
	}
}
