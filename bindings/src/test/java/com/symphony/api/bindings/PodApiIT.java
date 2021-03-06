package com.symphony.api.bindings;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.symphony.api.model.RoomSearchCriteria.SortOrderEnum;
import com.symphony.api.model.UserV2;
import com.symphony.api.model.V2RoomSearchCriteria;
import com.symphony.api.model.V3RoomSearchResults;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;


/**
 * Uses the TokenProvider to automatically configure token fields.
 * 
 * @author moffrob
 *
 */
public class PodApiIT extends AbstractIT {

	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testUserLookup(TestClientStrategy client) throws Exception {
		UsersApi uApi = client.getPodApi(UsersApi.class);
		UserV2 u = uApi.v2UserGet(null, null, client.getIdentity().getEmail(), null, false);
		System.out.println(u);
		Assertions.assertTrue(u.getDisplayName().contains("Symphony Practice"));
	}
	
	@ParameterizedTest
	@MethodSource("setupConfigurations")
	public void testRoomSearch(TestClientStrategy client) throws Exception {
		StreamsApi sApi = client.getPodApi(StreamsApi.class);
		V2RoomSearchCriteria criteria = new V2RoomSearchCriteria();
		criteria.setQuery("Demo");
		criteria.setPrivate(true);
		criteria.setActive(true);
		criteria.setLabels(Collections.emptyList());
		criteria.setSortOrder(SortOrderEnum.BASIC);
		V3RoomSearchResults res = sApi.v3RoomSearchPost(criteria, null, 0, 100);
		System.out.println(res);
	}
}
