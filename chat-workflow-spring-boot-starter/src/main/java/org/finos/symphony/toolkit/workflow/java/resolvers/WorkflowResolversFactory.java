package org.finos.symphony.toolkit.workflow.java.resolvers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Returns the {@link WorkflowResolvers}, which chains together lots of {@link WorkflowResolver} calls into a single one.
 * 
 * @author moffrob
 *
 */
public class WorkflowResolversFactory implements ApplicationContextAware {
	
	/**
	 * Using the application context here allows us to avoid a dependency loop, where we don't have all the {@link WorkflowResolverFactory}'s
	 * ready at the same time.
	 */
	private ApplicationContext beanFactory;

	public WorkflowResolversFactory() {
		super();
	}
	

	public WorkflowResolvers createResolvers(Action originatingAction) {
		final List<WorkflowResolver> resolvers = beanFactory.getBeansOfType(WorkflowResolverFactory.class)
				.values().stream()
				.sorted((a,b) -> Integer.compare(a.priority(), b.priority()))
				.map(wrf -> wrf.createResolver(originatingAction))
				.collect(Collectors.toList());
		
		return new WorkflowResolvers() {
			
			@Override
			public Optional<Object> resolve(Class<?> arg0, Addressable arg1, boolean isTarget) {
				for (WorkflowResolver workflowResolver : resolvers) {
					Optional<Object> oo = workflowResolver.resolve(arg0, arg1, isTarget);
					if (oo.isPresent()) {
						return oo;
					}
				}
				
				return Optional.empty();
			}

			@Override
			public boolean canResolve(Class<?> c) {
				for (WorkflowResolver workflowResolver : resolvers) {
					boolean b = workflowResolver.canResolve(c);
					if (b) {
						return true;
					}
				}
				
				return false;
			}

		};
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.beanFactory = applicationContext;
	}
}
