/**
 * This file is part of Waarp Project.
 * 
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author tags. See the
 * COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Waarp Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Waarp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Waarp . If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.common.logging;

import java.util.logging.Level;

/**
 * Based on the Netty InternalLoggerFactory Based on The Netty Project (netty-dev@lists.jboss.org)
 * 
 * @author Trustin Lee (tlee@redhat.com)
 * @author Frederic Bregier
 * 
 */
public abstract class WaarpInternalLoggerFactory implements WaarpInternalLoggerFactoryInterface {
	private static volatile WaarpInternalLoggerFactoryInterface defaultFactory = new WaarpJdkLoggerFactory(
			Level.WARNING);

	/**
	 * Returns the default factory. The initial default factory is {@link WaarpJdkLoggerFactory}.
	 */
	public static WaarpInternalLoggerFactoryInterface getDefaultFactory() {
		return defaultFactory;
	}

	/**
	 * Changes the default factory.
	 */
	public static void setDefaultFactory(WaarpInternalLoggerFactoryInterface defaultFactory) {
		if (defaultFactory == null) {
			throw new NullPointerException("defaultFactory");
		}
		WaarpInternalLoggerFactory.defaultFactory = defaultFactory;
	}

	/**
	 * Creates a new logger instance with the name of the specified class.
	 */
	public static WaarpInternalLogger getInstance(Class<?> clazz) {
		return getInstance(clazz.getName());
	}

	/**
	 * Creates a new logger instance with the specified name.
	 */
	public static WaarpInternalLogger getInstance(String name) {
		final WaarpInternalLogger logger = (WaarpInternalLogger) getDefaultFactory().newInstance(
				name);
		return new WaarpInternalLogger() {

			public void debug(String msg) {
				logger.debug(msg);
			}

			public void error(String msg) {
				logger.error(msg);
			}

			public void info(String msg) {
				logger.info(msg);
			}

			public boolean isDebugEnabled() {
				return logger.isDebugEnabled();
			}

			public boolean isErrorEnabled() {
				return logger.isErrorEnabled();
			}

			public boolean isInfoEnabled() {
				return logger.isInfoEnabled();
			}

			public boolean isWarnEnabled() {
				return logger.isWarnEnabled();
			}

			public void warn(String msg) {
				logger.warn(msg);
			}

			public boolean isEnabled(InternalLogLevel level) {
				return logger.isEnabled(level);
			}

			public void log(InternalLogLevel level, String msg) {
				logger.log(level, msg);
			}

			public void log(InternalLogLevel level, String msg, Throwable cause) {
				logger.log(level, msg, cause);
			}

			@Override
			public void debug(String format, String arg1) {
				logger.debug(format, arg1);
			}

			@Override
			public void info(String format, String arg1) {
				logger.info(format, arg1);
			}

			@Override
			public void warn(String format, String arg1) {
				logger.warn(format, arg1);
			}

			@Override
			public void error(String format, String arg1) {
				logger.error(format, arg1);
			}

			@Override
			public void debug(String format, String arg1, String arg2) {
				logger.debug(format, arg1, arg2);
			}

			@Override
			public void info(String format, String arg1, String arg2) {
				logger.info(format, arg1, arg2);
			}

			@Override
			public void warn(String format, String arg1, String arg2) {
				logger.warn(format, arg1, arg2);
			}

			@Override
			public void error(String format, String arg1, String arg2) {
				logger.error(format, arg1, arg2);
			}

			@Override
			public void debug(String format, Object arg1, Object arg2) {
				logger.debug(format, arg1, arg2);
			}

			@Override
			public void info(String format, Object arg1, Object arg2) {
				logger.info(format, arg1, arg2);
			}

			@Override
			public void warn(String format, Object arg1, Object arg2) {
				logger.warn(format, arg1, arg2);
			}

			@Override
			public void error(String format, Object arg1, Object arg2) {
				logger.error(format, arg1, arg2);
			}

			@Override
			public void debug(String format, Object arg1) {
				logger.debug(format, arg1);
			}

			@Override
			public void info(String format, Object arg1) {
				logger.info(format, arg1);
			}

			@Override
			public void warn(String format, Object arg1) {
				logger.warn(format, arg1);
			}

			@Override
			public void error(String format, Object arg1) {
				logger.error(format, arg1);
			}
		};
	}

	/**
	 * Creates a new logger instance with the specified name.
	 */
	public abstract WaarpInternalLogger newInstance(String name);

	/**
	 * 
	 * @param clazz
	 * @return the WaarpInternalLogger
	 */
	public static WaarpInternalLogger getLogger(Class<?> clazz) {
		WaarpInternalLoggerFactory factory = (WaarpInternalLoggerFactory) getDefaultFactory();
		if (factory instanceof WaarpInternalLoggerFactoryInterface) {
			return (WaarpInternalLogger) factory.newInstance(clazz.getName());
		} else {
			// Should be set first so default = JDK support
			WaarpInternalLoggerFactory
					.setDefaultFactory((WaarpInternalLoggerFactoryInterface) new WaarpJdkLoggerFactory(
							null));
			return (WaarpInternalLogger) getDefaultFactory().newInstance(
					clazz.getName());
		}
	}

}
