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
package com.lastcalc.parsers;

import java.util.*;

import junit.framework.Assert;

import com.google.common.collect.*;

import org.junit.Test;

public class UserDefinedParserParserTest {

	@Test
	public void bindMapTest() throws Exception {
		final PreParser.MapWithTail mwt = new PreParser.MapWithTail(Maps.newLinkedHashMap(), "tailvar");
		mwt.map.put("key1", "value1var");
		mwt.map.put("key2var", "value2var");
		final Set<String> variables = Sets.newHashSet("value1var", "key2var", "value2var", "tailvar");
		final LinkedHashMap<Object, Object> bindMap = Maps.newLinkedHashMap();
		bindMap.put("key1", "value1");
		bindMap.put("key2", "value2");
		bindMap.put("key3", "value3");
		final Map<String, Object> bound = Maps.newHashMap();
		UserDefinedParserParser.bind(mwt, bindMap, variables, bound);
		Assert.assertEquals(variables.size(), bound.size());
		Assert.assertEquals("value1", bound.get("value1var"));
		Assert.assertEquals("key2", bound.get("key2var"));
		Assert.assertEquals("value2", bound.get("value2var"));
		Assert.assertTrue(bound.get("tailvar") instanceof Map);
		final Map<Object, Object> tailMap = (Map<Object, Object>) bound.get("tailvar");
		Assert.assertEquals(1, tailMap.size());
		Assert.assertEquals("value3", tailMap.get("key3"));

		// TODO: Test with just map, rather than MapWithTail
	}

	@Test
	public void bindListTest() throws Exception {
		final PreParser.ListWithTail lwt = new PreParser.ListWithTail(Lists.<Object> newArrayList("var1", "var2",
				"var3"), "tailvar");
		final List<Object> bindList = Lists.<Object> newArrayList("val1", "val2", "val3", "val4");
		final Set<String> variables = Sets.newHashSet("var1", "var2", "var3", "tailvar");
		final LinkedHashMap<Object, Object> bindMap = Maps.newLinkedHashMap();
		final Map<String, Object> bound = Maps.newHashMap();
		UserDefinedParserParser.bind(lwt, bindList, variables, bound);
		Assert.assertEquals(variables.size(), bound.size());
		Assert.assertEquals("val1", bound.get("var1"));
		Assert.assertEquals("val2", bound.get("var2"));
		Assert.assertEquals("val3", bound.get("var3"));
		Assert.assertTrue(bound.get("tailvar") instanceof List);
		final List<Object> tailList = (List<Object>) bound.get("tailvar");
		Assert.assertEquals(1, tailList.size());
		Assert.assertEquals("val4", tailList.get(0));
	}
}
