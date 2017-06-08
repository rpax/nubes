package com.github.aesteve.vertx.nubes.utils.async;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.NoStackTraceThrowable;

public class SimpleFuture<T> implements Future<T> {

	protected void checkCallHandler()
	{
		if (handler != null && isComplete())
		{
			handler.handle(this);
		}
	}

	private boolean failed;
	private boolean succeeded;
	private Handler<AsyncResult<T>> handler;
	private T result;
	private Throwable throwable;

	/**
	 * Create a future that hasn't completed yet
	 */
	SimpleFuture()
	{
	}

	/**
	 * Create a future that has already failed
	 * 
	 * @param t
	 *            the throwable
	 */
	SimpleFuture(Throwable t)
	{
		fail(t != null ? t : new NoStackTraceThrowable(null));
	}

	/**
	 * Create a future that has already failed
	 * 
	 * @param failureMessage
	 *            the failure message
	 */
	SimpleFuture(String failureMessage)
	{
		this(new NoStackTraceThrowable(failureMessage));
	}

	/**
	 * Create a future that has already succeeded
	 * 
	 * @param result
	 *            the result
	 */
	SimpleFuture(T result)
	{
		complete(result);
	}

	/**
	 * The result of the operation. This will be null if the operation failed.
	 */
	public T result()
	{
		return result;
	}

	/**
	 * An exception describing failure. This will be null if the operation
	 * succeeded.
	 */
	public Throwable cause()
	{
		return throwable;
	}

	/**
	 * Did it succeeed?
	 */
	public boolean succeeded()
	{
		return succeeded;
	}

	/**
	 * Did it fail?
	 */
	public boolean failed()
	{
		return failed;
	}

	/**
	 * Has it completed?
	 */
	public boolean isComplete()
	{
		return failed || succeeded;
	}

	/**
	 * Set a handler for the result. It will get called when it's complete
	 */
	public Future<T> setHandler(Handler<AsyncResult<T>> handler)
	{
		this.handler = handler;
		checkCallHandler();
		return this;
	}

	@Override
	public void complete(T result)
	{
		if (!tryComplete(result))
		{
			throw new IllegalStateException(
					"Result is already complete: " + (succeeded ? "succeeded" : "failed"));
		}
	}

	@Override
	public void complete()
	{
		if (!tryComplete())
		{
			throw new IllegalStateException(
					"Result is already complete: " + (succeeded ? "succeeded" : "failed"));
		}
	}

	@Override
	public void fail(Throwable cause)
	{
		if (!tryFail(cause))
		{
			throw new IllegalStateException(
					"Result is already complete: " + (succeeded ? "succeeded" : "failed"));
		}
	}

	@Override
	public void fail(String failureMessage)
	{
		if (!tryFail(failureMessage))
		{
			throw new IllegalStateException(
					"Result is already complete: " + (succeeded ? "succeeded" : "failed"));
		}
	}

	@Override
	public boolean tryComplete(T result)
	{
		if (succeeded || failed)
		{
			return false;
		}
		this.result = result;
		succeeded = true;
		checkCallHandler();
		return true;
	}

	@Override
	public boolean tryComplete()
	{
		return tryComplete(null);
	}

	public void handle(Future<T> ar)
	{
		if (ar.succeeded())
		{
			complete(ar.result());
		} else
		{
			fail(ar.cause());
		}
	}

	@Override
	public Handler<AsyncResult<T>> completer()
	{
		return this;
	}

	@Override
	public void handle(AsyncResult<T> asyncResult)
	{
		if (asyncResult.succeeded())
		{
			complete(asyncResult.result());
		} else
		{
			fail(asyncResult.cause());
		}
	}

	@Override
	public boolean tryFail(Throwable cause)
	{
		if (succeeded || failed)
		{
			return false;
		}
		this.throwable = cause != null ? cause : new NoStackTraceThrowable(null);
		failed = true;
		checkCallHandler();
		return true;
	}

	@Override
	public boolean tryFail(String failureMessage)
	{
		return tryFail(new NoStackTraceThrowable(failureMessage));
	}

}
