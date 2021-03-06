package org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.AbstractElementsConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.FormConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class TableDeleteRows extends AbstractElementsConsumer {
	
	SpelExpressionParser spel = new SpelExpressionParser();

	public static final String ACTION_SUFFIX = "table-delete-rows";
	public static final String SELECT_SUFFIX = "selected";


	@SuppressWarnings("unchecked")
	@Override
	public List<Response> apply(ElementsAction ea) {
		Workflow wf = ea.getWorkflow();
		String verb = ea.getAction();
		if (verb == null) {
			return null;
		}
		
		EntityJson ej = ea.getData();
		Object data = ej.get(EntityJsonConverter.WORKFLOW_001);
		
		if (verb.endsWith(ACTION_SUFFIX)) {
			
			// get the table to modify
			String tableLocation = verb.substring(0, verb.length() - ACTION_SUFFIX.length()-1);
			tableLocation = TableEditRow.fixSpel(tableLocation);
			Expression e = spel.parseExpression(tableLocation);
			List<Object> table = (List<Object>) e.getValue(data);
			Object deleteStructure = ((FormConverter.UnconvertedContent)ea.getFormData()).structure;
			
			String mapLocation = convertSpelToMapSpel(tableLocation);
			e = spel.parseExpression(mapLocation);
			List<Integer> toRemove = getRowsToDelete((List<Object>) e.getValue(deleteStructure));
			
			for (Integer i : toRemove) {
				table.remove((int) i);
			}
			
			return Collections.singletonList(
				new FormResponse(wf, ea.getAddressable(), ej, 
						wf.getName(data.getClass()), 
						wf.getInstructions(data.getClass()), 
						data, false, 
						wf.gatherButtons(data, ea.getAddressable())));
		}
		
		return null;
	}

	private String convertSpelToMapSpel(String tableLocation) {
		return String.join(".", Arrays.stream(tableLocation.split("\\."))
			.map(i -> i.startsWith("[") ? i : "['" + i + "']")
			.toArray(i -> new String[i]));
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getRowsToDelete(List<Object> value) {
		return IntStream.range(0, value.size())
			.filter(i -> {
				Object val = value.get(i);
				return (val != null) && ((Map<String, Object>) val).containsKey("selected");
			})
			.mapToObj(i -> i)
			.sorted(Collections.reverseOrder())
			.collect(Collectors.toList());
	}
}
