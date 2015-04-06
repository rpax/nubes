package io.vertx.mvc.reflections.injectors.typed;

import io.vertx.ext.apex.RoutingContext;

public abstract class ContextDataParamInjector<T> implements ParamInjector<T> {

	protected abstract String dataAttr();
	
	@Override
	public T resolve(RoutingContext context) {
		return context.get(dataAttr());
	}

}