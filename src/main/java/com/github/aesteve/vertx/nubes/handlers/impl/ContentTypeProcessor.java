package com.github.aesteve.vertx.nubes.handlers.impl;

import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.util.ArrayList;
//import io.vertx.ext.apex.impl.Utils;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.handlers.AnnotationProcessor;

import io.vertx.ext.web.RoutingContext;

public class ContentTypeProcessor extends NoopAfterAllProcessor
		implements AnnotationProcessor<ContentType> {

	public static final String BEST_CONTENT_TYPE = "nubes-best-content-type";

	private final ContentType annotation;

	public ContentTypeProcessor(ContentType annotation)
	{
		this.annotation = annotation;
	}

	@Override
	public void preHandle(RoutingContext context)
	{
		String accept = context.request().getHeader(ACCEPT.toString());
		if (accept == null)
		{
			context.fail(406);
			return;
		}
		List<String> acceptableTypes = getSortedAcceptableMimeTypes(accept);
		Optional<String> bestType = acceptableTypes.stream()
				.filter(Arrays.asList(annotation.value())::contains).findFirst();
		if (bestType.isPresent())
		{
			ContentTypeProcessor.setContentType(context, bestType.get());
			context.next();
		} else
		{
			context.fail(406);
		}
	}

	@Override
	public void postHandle(RoutingContext context)
	{
		context.response().putHeader(CONTENT_TYPE, ContentTypeProcessor.getContentType(context));
		context.next();
	}

	public static String getContentType(RoutingContext context)
	{
		return context.get(BEST_CONTENT_TYPE);
	}

	private static void setContentType(RoutingContext context, String contentType)
	{
		context.put(BEST_CONTENT_TYPE, contentType);
	}

	private static final Pattern COMMA_SPLITTER = Pattern.compile(" *, *");
	private static final Pattern SEMICOLON_SPLITTER = Pattern.compile(" *; *");
	private static final Pattern EQUAL_SPLITTER = Pattern.compile(" *= *");
	private static final Comparator<String> ACCEPT_X_COMPARATOR = new Comparator<String>() {
		float getQuality(String s)
		{
			if (s == null)
			{
				return 0;
			}

			String[] params = SEMICOLON_SPLITTER.split(s);
			for (int i = 1; i < params.length; i++)
			{
				String[] q = EQUAL_SPLITTER.split(params[1]);
				if ("q".equals(q[0]))
				{
					return Float.parseFloat(q[1]);
				}
			}
			return 1;
		}

		@Override
		public int compare(String o1, String o2)
		{
			float f1 = getQuality(o1);
			float f2 = getQuality(o2);
			if (f1 < f2)
			{
				return 1;
			}
			if (f1 > f2)
			{
				return -1;
			}
			return 0;
		}
	};

	public static List<String> getSortedAcceptableMimeTypes(String acceptHeader)
	{
		// accept anything when accept is not present
		if (acceptHeader == null)
		{
			return Collections.emptyList();
		}

		// parse
		String[] items = COMMA_SPLITTER.split(acceptHeader);
		// sort on quality
		Arrays.sort(items, ACCEPT_X_COMPARATOR);

		List<String> list = new ArrayList<>(items.length);

		for (String item : items)
		{
			// find any ; e.g.: "application/json;q=0.8"
//			int space = item.indexOf(';');
//
//			if (space != 1)
//			{
//				list.add(item.substring(0, space));
//			} else
//			{
//				list.add(item);
//			}
			list.add(item.split(";")[0]);
		}

		return list;
	}

}
