package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

import org.finos.symphony.toolkit.json.EntityJson;

/**
 * Outputs simple (i.e. single-field) type information.
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractSimpleTypeConverter extends AbstractTypeConverter {

	public AbstractSimpleTypeConverter(int priority) {
		super(priority);
	}

	@Override
	public final String apply(WithType controller, Type t, boolean editMode, Variable variable, EntityJson ej, WithField notUsed) {
		return apply(t, editMode, variable, ej);
	}

	protected abstract String apply(Type t, boolean editMode, Variable variable, EntityJson ej);

}
