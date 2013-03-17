/*******************************************************************************
 * LastCalc - The last calculator you'll ever need
 * Copyright (C) 2011, 2012 Uprizer Labs LLC
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Affero General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE.  See the GNU Affero General Public License for more 
 * details.
 ******************************************************************************/
package com.lastcalc.cache;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

import javax.cache.*;

import com.google.common.collect.*;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

public class ObjectCache {
	private static final Logger log = Logger.getLogger(ObjectCache.class.getName());

	static Map<Integer, CachedObject> softMap = new MapMaker().softValues().makeMap();

	public static <T> T getFast(final long oldestAllowed, final Object... key) {
		return getFastWithHash(oldestAllowed, Arrays.hashCode(key));
	}

	@SuppressWarnings("unchecked")
	private static <T> T getFastWithHash(final long oldestAllowed, final int hash) {
		final CachedObject co = softMap.get(hash);
		if (co == null)
			return null;
		if (System.currentTimeMillis() - oldestAllowed > co.cacheTime)
			return (T) co.object;
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSlow(final long oldestAllowed, final Object... key) {
		final int keyHash = Arrays.hashCode(key);
		final T o = getFastWithHash(oldestAllowed, keyHash);
		if (o != null) {
			log.info("Got from local RAM");
			return o;
		}
		else {
			CacheFactory cacheFactory;
			try {
				cacheFactory = CacheManager.getInstance().getCacheFactory();
				final Cache memcache = cacheFactory.createCache(Collections.emptyMap());
				final CachedObject co = (CachedObject) memcache.get(keyHash);
				if (co != null) {
					log.info("Got from memcache");
					return (T) co.object;
				}
				else
					return null;
			} catch (final CacheException e) {
				log.warning("Exception retrieving object from cache: " + e);
				return null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static void put(final long expirationDelta, final Object value, final Object... key) {
		final int keyHash = Arrays.hashCode(key);
		final CachedObject co = new CachedObject(value);
		softMap.put(keyHash, co);
		CacheFactory cacheFactory;
		try {
			cacheFactory = CacheManager.getInstance().getCacheFactory();
			final Map<Object, Object> props = Maps.newHashMap();
			props.put(GCacheFactory.EXPIRATION_DELTA, 3600);
			final Cache memcache = cacheFactory.createCache(props);
			memcache.put(keyHash, co);
		} catch (final CacheException e) {
			log.warning("Exception retrieving object from cache: " + e);
		}
	}

	public static class CachedObject implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2851269118906160513L;

		public final long cacheTime;

		public final Object object;

		public CachedObject(final Object object) {
			this.object = object;
			cacheTime = System.currentTimeMillis();
		}
	}
}
