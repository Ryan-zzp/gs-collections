/*
 * Copyright 2014 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.lazy;

import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.factory.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ZipWithIndexIterableTest
{
    private ZipWithIndexIterable<Integer> iterableUnderTest;
    private final StringBuilder buffer = new StringBuilder();

    @Before
    public void setUp()
    {
        this.iterableUnderTest = new ZipWithIndexIterable<Integer>(Lists.immutable.of(1, 2, 3, 4));
    }

    private void assertBufferContains(String expected)
    {
        Assert.assertEquals(expected, this.buffer.toString());
    }

    @Test
    public void forEach()
    {
        this.iterableUnderTest.forEach((Procedure<Pair<Integer, Integer>>) argument1 -> {
            this.buffer.append("(");
            this.buffer.append(argument1.toString());
            this.buffer.append(")");
        });
        this.assertBufferContains("(1:0)(2:1)(3:2)(4:3)");
    }

    @Test
    public void forEachWIthIndex()
    {
        this.iterableUnderTest.forEachWithIndex((each, index) -> {
            this.buffer.append("|(");
            this.buffer.append(each.toString());
            this.buffer.append("),");
            this.buffer.append(index);
        });
        this.assertBufferContains("|(1:0),0|(2:1),1|(3:2),2|(4:3),3");
    }

    @Test
    public void forEachWith()
    {
        this.iterableUnderTest.forEachWith((argument1, argument2) -> {
            this.buffer.append("|(");
            this.buffer.append(argument1.toString());
            this.buffer.append("),");
            this.buffer.append(argument2);
        }, "A");
        this.assertBufferContains("|(1:0),A|(2:1),A|(3:2),A|(4:3),A");
    }
}
