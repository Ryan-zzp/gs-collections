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

package com.gs.collections.impl.map.mutable.primitive;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.primitive.ObjectBooleanMap;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.api.tuple.primitive.ObjectBooleanPair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Functions0;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.Assert;
import org.junit.Test;

/**
 * Abstract JUnit test for {@link ObjectBooleanMap#keyValuesView()}.
 */
public abstract class AbstractObjectBooleanMapKeyValuesViewTestCase
{
    public abstract <K> ObjectBooleanMap<K> newWithKeysValues(K key1, boolean value1, K key2, boolean value2, K key3, boolean value3);

    public abstract <K> ObjectBooleanMap<K> newWithKeysValues(K key1, boolean value1, K key2, boolean value2);

    public abstract <K> ObjectBooleanMap<K> newWithKeysValues(K key1, boolean value1);

    public abstract <K> ObjectBooleanMap<K> newEmpty();

    public RichIterable<ObjectBooleanPair<Object>> newWith()
    {
        return this.newEmpty().keyValuesView();
    }

    public <K> RichIterable<ObjectBooleanPair<K>> newWith(K key1, boolean value1)
    {
        return this.newWithKeysValues(key1, value1).keyValuesView();
    }

    public <K> RichIterable<ObjectBooleanPair<K>> newWith(K key1, boolean value1, K key2, boolean value2)
    {
        return this.newWithKeysValues(key1, value1, key2, value2).keyValuesView();
    }

    public <K> RichIterable<ObjectBooleanPair<K>> newWith(K key1, boolean value1, K key2, boolean value2, K key3, boolean value3)
    {
        return this.newWithKeysValues(key1, value1, key2, value2, key3, value3).keyValuesView();
    }

    @Test
    public void containsAllIterable()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assert.assertTrue(collection.containsAllIterable(FastList.newListWith(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false))));
        Assert.assertFalse(collection.containsAllIterable(FastList.newListWith(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(1.0, Integer.valueOf(5)))));
    }

    @Test
    public void containsAllArray()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assert.assertTrue(collection.containsAllArguments(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Assert.assertFalse(collection.containsAllArguments(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(1.0, Integer.valueOf(5))));
    }

    @Test
    public void forEach()
    {
        MutableList<ObjectBooleanPair<Integer>> result = Lists.mutable.of();
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        collection.forEach(CollectionAddProcedure.on(result));
        Verify.assertSize(3, result);
        Verify.assertContainsAll(result, PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true));
    }

    @Test
    public void forEachWith()
    {
        MutableBag<ObjectBooleanPair<Integer>> result = Bags.mutable.of();
        MutableBag<Integer> result2 = Bags.mutable.of();
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        collection.forEachWith((argument1, argument2) -> {
            result.add(argument1);
            result2.add(argument2);
        }, 0);

        Assert.assertEquals(Bags.immutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true)), result);
        Assert.assertEquals(Bags.immutable.of(0, 0, 0), result2);
    }

    @Test
    public void forEachWithIndex()
    {
        MutableBag<ObjectBooleanPair<Integer>> elements = Bags.mutable.of();
        MutableBag<Integer> indexes = Bags.mutable.of();
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        collection.forEachWithIndex((object, index) -> {
            elements.add(object);
            indexes.add(index);
        });
        Assert.assertEquals(Bags.mutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true)), elements);
        Assert.assertEquals(Bags.mutable.of(0, 1, 2), indexes);
    }

    @Test
    public void select()
    {
        MutableList<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).select(Predicates.equal(PrimitiveTuples.pair(Integer.valueOf(2), false))).toList();
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(2), false), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(1), true), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(3), true), result);
    }

    @Test
    public void selectWith()
    {
        MutableList<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).selectWith(Predicates2.equal(), PrimitiveTuples.pair(Integer.valueOf(2), false)).toList();
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(2), false), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(1), true), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(3), true), result);
    }

    @Test
    public void selectWith_target()
    {
        HashBag<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).selectWith(Predicates2.notEqual(), PrimitiveTuples.pair(Integer.valueOf(2), false), HashBag.<ObjectBooleanPair<Integer>>newBag());
        Assert.assertEquals(Bags.immutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(3), true)), result);
    }

    @Test
    public void reject()
    {
        MutableList<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).reject(Predicates.notEqual(PrimitiveTuples.pair(Integer.valueOf(2), false))).toList();
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(2), false), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(1), true), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(3), true), result);
    }

    @Test
    public void rejectWith()
    {
        MutableList<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).rejectWith(Predicates2.notEqual(), PrimitiveTuples.pair(Integer.valueOf(2), false)).toList();
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(2), false), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(1), true), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(3), true), result);
    }

    @Test
    public void rejectWith_target()
    {
        HashBag<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).rejectWith(Predicates2.equal(), PrimitiveTuples.pair(Integer.valueOf(2), false), HashBag.<ObjectBooleanPair<Integer>>newBag());
        Assert.assertEquals(Bags.immutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(3), true)), result);
    }

    @Test
    public void selectInstancesOf()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(1, true, 2, false, 3, true);
        Verify.assertIterableEmpty(pairs.selectInstancesOf(Integer.class));
        Verify.assertContainsAll(pairs.selectInstancesOf(ObjectBooleanPair.class), PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(3), true), PrimitiveTuples.pair(Integer.valueOf(2), false));
    }

    @Test
    public void collect()
    {
        RichIterable<Integer> result1 = this.newWith(2, true, 3, false, 4, true).collect(object -> (int) object.getOne());

        Assert.assertEquals(HashBag.newBagWith(2, 3, 4), result1.toBag());
    }

    @Test
    public void flatCollect()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Function<ObjectBooleanPair<Integer>, MutableList<String>> function =
                object -> FastList.newListWith(String.valueOf(object));

        Verify.assertListsEqual(
                FastList.newListWith("1:true", "2:false", "3:true"),
                collection.flatCollect(function).toSortedList());

        Verify.assertSetsEqual(
                UnifiedSet.newSetWith("1:true", "2:false", "3:true"),
                collection.flatCollect(function, UnifiedSet.<String>newSet()));
    }

    @Test
    public void detect()
    {
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(2), false), this.newWith(1, true, 2, false, 3, true).detect(Predicates.equal(PrimitiveTuples.pair(Integer.valueOf(2), false))));
        Assert.assertNull(this.newWith(1, true, 2, false, 3, true).detect(Predicates.equal(PrimitiveTuples.pair(true, Integer.valueOf(4)))));
    }

    @Test(expected = NoSuchElementException.class)
    public void min_empty_throws()
    {
        this.newWith().min(Comparators.naturalOrder());
    }

    @Test(expected = NoSuchElementException.class)
    public void max_empty_throws()
    {
        this.newWith().max(Comparators.naturalOrder());
    }

    @Test
    public void min()
    {
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(1), true), this.newWith(1, true, 2, false, 3, true).min(Comparators.naturalOrder()));
    }

    @Test
    public void max()
    {
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(3), true), this.newWith(1, true, 2, false, 3, true).max(Comparators.naturalOrder()));
    }

    @Test
    public void min_without_comparator()
    {
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(1), true), this.newWith(1, true, 2, false, 3, true).min(Comparators.naturalOrder()));
    }

    @Test
    public void max_without_comparator()
    {
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(3), true), this.newWith(1, true, 2, false, 3, true).max(Comparators.naturalOrder()));
    }

    @Test
    public void minBy()
    {
        Function<ObjectBooleanPair<Integer>, Integer> function = object -> object.getOne();
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(2), true), this.newWith(2, true, 3, false, 4, true).minBy(function));
    }

    @Test
    public void maxBy()
    {
        Function<ObjectBooleanPair<Integer>, Integer> function = object -> (int) object.getOne() & 1;
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(3), false), this.newWith(2, true, 3, false, 4, true).maxBy(function));
    }

    @Test
    public void detectWith()
    {
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(2), false), this.newWith(1, true, 2, false, 3, true).detectWith(Predicates2.equal(), PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Assert.assertNull(this.newWith(1, true, 2, false, 3, true).detectWith(Predicates2.equal(), PrimitiveTuples.pair(true, Integer.valueOf(4))));
    }

    @Test
    public void detectIfNone()
    {
        Function0<ObjectBooleanPair<Integer>> function = Functions0.value(PrimitiveTuples.pair(Integer.valueOf(5), true));
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(2), false), this.newWith(1, true, 2, false, 3, true).detectIfNone(Predicates.equal(PrimitiveTuples.pair(Integer.valueOf(2), false)), function));
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(5), true), this.newWith(1, true, 2, false, 3, true).detectIfNone(Predicates.equal(PrimitiveTuples.pair(true, Integer.valueOf(4))), function));
    }

    @Test
    public void detectWithIfNoneBlock()
    {
        Function0<ObjectBooleanPair<Integer>> function = Functions0.value(PrimitiveTuples.pair(Integer.valueOf(5), true));
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(2), false), this.newWith(1, true, 2, false, 3, true).detectWithIfNone(Predicates2.equal(),
                PrimitiveTuples.pair(Integer.valueOf(2), false),
                function));
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(5), true), this.newWith(1, true, 2, false, 3, true).detectWithIfNone(Predicates2.equal(),
                PrimitiveTuples.pair(true, Integer.valueOf(4)),
                function));
    }

    @Test
    public void allSatisfy()
    {
        Assert.assertTrue(this.newWith(1, true, 2, false, 3, true).allSatisfy(Predicates.instanceOf(ObjectBooleanPair.class)));
        Assert.assertFalse(this.newWith(1, true, 2, false, 3, true).allSatisfy(Predicates.equal(PrimitiveTuples.pair(Integer.valueOf(2), false))));
    }

    @Test
    public void allSatisfyWith()
    {
        Assert.assertTrue(this.newWith(1, true, 2, false, 3, true).allSatisfyWith(Predicates2.instanceOf(), ObjectBooleanPair.class));
        Assert.assertFalse(this.newWith(1, true, 2, false, 3, true).allSatisfyWith(Predicates2.equal(), PrimitiveTuples.pair(Integer.valueOf(2), false)));
    }

    @Test
    public void noneSatisfy()
    {
        Assert.assertTrue(this.newWith(1, true, 2, false, 3, true).noneSatisfy(Predicates.instanceOf(Boolean.class)));
        Assert.assertFalse(this.newWith(1, true, 2, false, 3, true).noneSatisfy(Predicates.equal(PrimitiveTuples.pair(Integer.valueOf(2), false))));
    }

    @Test
    public void noneSatisfyWith()
    {
        Assert.assertTrue(this.newWith(1, true, 2, false, 3, true).noneSatisfyWith(Predicates2.instanceOf(), Boolean.class));
        Assert.assertFalse(this.newWith(1, true, 2, false, 3, true).noneSatisfyWith(Predicates2.equal(), PrimitiveTuples.pair(Integer.valueOf(2), false)));
    }

    @Test
    public void anySatisfy()
    {
        Assert.assertTrue(this.newWith(1, true, 2, false, 3, true).anySatisfy(Predicates.equal(PrimitiveTuples.pair(Integer.valueOf(2), false))));
        Assert.assertFalse(this.newWith(1, true, 2, false, 3, true).anySatisfy(Predicates.equal(PrimitiveTuples.pair(true, Integer.valueOf(5)))));
    }

    @Test
    public void anySatisfyWith()
    {
        Assert.assertTrue(this.newWith(1, true, 2, false, 3, true).anySatisfyWith(Predicates2.equal(), PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Assert.assertFalse(this.newWith(1, true, 2, false, 3, true).anySatisfyWith(Predicates2.equal(), PrimitiveTuples.pair(true, Integer.valueOf(5))));
    }

    @Test
    public void count()
    {
        Assert.assertEquals(0, this.newWith(1, true, 2, false, 3, true).count(Predicates.instanceOf(Boolean.class)));
        Assert.assertEquals(3, this.newWith(1, true, 2, false, 3, true).count(Predicates.instanceOf(ObjectBooleanPair.class)));
        Assert.assertEquals(1, this.newWith(1, true, 2, false, 3, true).count(Predicates.equal(PrimitiveTuples.pair(Integer.valueOf(2), false))));
    }

    @Test
    public void countWith()
    {
        Assert.assertEquals(0, this.newWith(1, true, 2, false, 3, true).countWith(Predicates2.instanceOf(), Boolean.class));
        Assert.assertEquals(3, this.newWith(1, true, 2, false, 3, true).countWith(Predicates2.instanceOf(), ObjectBooleanPair.class));
        Assert.assertEquals(1, this.newWith(1, true, 2, false, 3, true).countWith(Predicates2.equal(), PrimitiveTuples.pair(Integer.valueOf(2), false)));
    }

    @Test
    public void collectIf()
    {
        Verify.assertContainsAll(
                this.newWith(1, true, 2, false, 3, true).collectIf(
                        Predicates.instanceOf(ObjectBooleanPair.class),
                        Functions.getToString()),
                "1:true", "2:false", "3:true");
        Verify.assertContainsAll(
                this.newWith(1, true, 2, false, 3, true).collectIf(
                        Predicates.instanceOf(ObjectBooleanPair.class),
                        Functions.getToString(),
                        UnifiedSet.<String>newSet()),
                "1:true", "2:false", "3:true");
    }

    @Test
    public void collectWith()
    {
        Assert.assertEquals(
                Bags.mutable.of(5L, 7L, 9L),
                this.newWith(2, true, 3, false, 4, true).collectWith((argument1, argument2) -> (long) (argument1.getOne() + argument1.getOne() + argument2), 1L).toBag());
    }

    @Test
    public void collectWith_target()
    {
        Assert.assertEquals(
                Bags.mutable.of(5L, 7L, 9L),
                this.newWith(2, true, 3, false, 4, true).collectWith((argument1, argument2) -> (long) (argument1.getOne() + argument1.getOne() + argument2), 1L, HashBag.<Long>newBag()));
    }

    @Test
    public void getFirst()
    {
        ObjectBooleanPair<Integer> first = this.newWith(1, true, 2, false, 3, true).getFirst();
        Assert.assertTrue(PrimitiveTuples.pair(Integer.valueOf(1), true).equals(first)
                || PrimitiveTuples.pair(Integer.valueOf(2), false).equals(first)
                || PrimitiveTuples.pair(Integer.valueOf(3), true).equals(first));
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(1), true), this.newWith(1, true).getFirst());
    }

    @Test
    public void getLast()
    {
        ObjectBooleanPair<Integer> last = this.newWith(1, true, 2, false, 3, true).getLast();
        Assert.assertTrue(PrimitiveTuples.pair(Integer.valueOf(1), true).equals(last)
                || PrimitiveTuples.pair(Integer.valueOf(2), false).equals(last)
                || PrimitiveTuples.pair(Integer.valueOf(3), true).equals(last));
        Assert.assertEquals(PrimitiveTuples.pair(Integer.valueOf(1), true), this.newWith(1, true).getLast());
    }

    @Test
    public void isEmpty()
    {
        Verify.assertIterableEmpty(this.newWith());
        Verify.assertIterableNotEmpty(this.newWith(1, true));
        Assert.assertTrue(this.newWith(1, true).notEmpty());
    }

    @Test
    public void iterator()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(1, true, 2, false, 3, true);
        MutableBag<ObjectBooleanPair<Integer>> actual = Bags.mutable.of();
        Iterator<ObjectBooleanPair<Integer>> iterator = objects.iterator();
        for (int i = objects.size(); i-- > 0; )
        {
            Assert.assertTrue(iterator.hasNext());
            actual.add(iterator.next());
        }

        Assert.assertFalse(iterator.hasNext());
        Verify.assertThrows(UnsupportedOperationException.class, (Runnable) () -> {iterator.remove();});
        Assert.assertEquals(objects.toBag(), actual);
    }

    @Test(expected = NoSuchElementException.class)
    public void iterator_throws()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(1, true, 2, false, 3, true);
        Iterator<ObjectBooleanPair<Integer>> iterator = objects.iterator();
        for (int i = objects.size(); i-- > 0; )
        {
            Assert.assertTrue(iterator.hasNext());
            iterator.next();
        }
        Assert.assertFalse(iterator.hasNext());
        iterator.next();
    }

    @Test
    public void injectInto()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        Long result = objects.injectInto(1L, (Long argument1, ObjectBooleanPair<Integer> argument2) -> (long) (argument1 + argument2.getOne() + argument2.getOne()));
        Assert.assertEquals(Long.valueOf(19), result);
    }

    @Test
    public void injectIntoInt()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        int result = objects.injectInto(1, (int intParameter, ObjectBooleanPair<Integer> argument2) -> (int) (intParameter + argument2.getOne() + argument2.getOne()));
        Assert.assertEquals(19, result);
    }

    @Test
    public void injectIntoLong()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        long result = objects.injectInto(1L, (long parameter, ObjectBooleanPair<Integer> argument2) -> (long) (parameter + argument2.getOne() + argument2.getOne()));
        Assert.assertEquals(19, result);
    }

    @Test
    public void injectIntoDouble()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        double result = objects.injectInto(1.0, (parameter, argument2) -> (double) (parameter + argument2.getOne() + argument2.getOne()));
        Assert.assertEquals(19.0, result, 0.0);
    }

    @Test
    public void injectIntoFloat()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        float result = objects.injectInto(1.0f, (float parameter, ObjectBooleanPair<Integer> argument2) -> (float) (parameter + argument2.getOne() + argument2.getOne()));
        Assert.assertEquals(19.0, result, 0.0);
    }

    @Test
    public void sumFloat()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        double actual = objects.sumOfFloat(each -> (float) (each.getOne() + each.getOne()));
        Assert.assertEquals(18.0, actual, 0.0);
    }

    @Test
    public void sumDouble()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        double actual = objects.sumOfDouble(each -> (double) (each.getOne() + each.getOne()));
        Assert.assertEquals(18.0, actual, 0.0);
    }

    @Test
    public void sumInteger()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        long actual = objects.sumOfInt(each -> (int) (each.getOne() + each.getOne()));
        Assert.assertEquals(18, actual);
    }

    @Test
    public void sumLong()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        long actual = objects.sumOfLong(each -> (long) (each.getOne() + each.getOne()));
        Assert.assertEquals(18, actual);
    }

    @Test
    public void toArray()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(1, true, 2, false, 3, true);
        Object[] array = objects.toArray();
        Verify.assertSize(3, array);
        ObjectBooleanPair<Integer>[] array2 = objects.toArray(new ObjectBooleanPair[3]);
        Verify.assertSize(3, array2);
    }

    @Test
    public void partition()
    {
        PartitionIterable<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).partition(Predicates.equal(PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(2), false), result.getSelected().toList());
        Verify.assertIterableSize(1, result.getSelected());
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(1), true), result.getRejected().toList());
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(3), true), result.getRejected().toList());
        Verify.assertIterableSize(2, result.getRejected());
    }

    @Test
    public void toList()
    {
        MutableList<ObjectBooleanPair<Integer>> list = this.newWith(1, true, 2, false, 3, true).toList();
        Verify.assertContainsAll(list, PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true));
    }

    @Test
    public void toBag()
    {
        MutableBag<ObjectBooleanPair<Integer>> bag = this.newWith(1, true, 2, false, 3, true).toBag();
        Verify.assertContainsAll(bag, PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true));
    }

    @Test
    public void toSortedList_natural_ordering()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableList<ObjectBooleanPair<Integer>> list = pairs.toSortedList();
        Assert.assertEquals(Lists.mutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(5), false)), list);
    }

    @Test
    public void toSortedList_with_comparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableList<ObjectBooleanPair<Integer>> list = pairs.toSortedList(Comparators.reverseNaturalOrder());
        Assert.assertEquals(Lists.mutable.of(PrimitiveTuples.pair(Integer.valueOf(5), false), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(1), true)), list);
    }

    @Test
    public void toSortedListBy()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableList<ObjectBooleanPair<Integer>> list = pairs.toSortedListBy(Functions.getToString());
        Assert.assertEquals(Lists.mutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(5), false)), list);
    }

    @Test
    public void toSortedSet_natural_ordering()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableSortedSet<ObjectBooleanPair<Integer>> set = pairs.toSortedSet();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(5), false)), set);
    }

    @Test
    public void toSortedSet_with_comparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableSortedSet<ObjectBooleanPair<Integer>> set = pairs.toSortedSet(Comparators.reverseNaturalOrder());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(),
                PrimitiveTuples.pair(Integer.valueOf(5), false),
                PrimitiveTuples.pair(Integer.valueOf(2), true),
                PrimitiveTuples.pair(Integer.valueOf(1), true)),
                set);
    }

    @Test
    public void toSortedSetBy()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableSortedSet<ObjectBooleanPair<Integer>> set = pairs.toSortedSetBy(Functions.getToString());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(5), false)), set);
    }

    @Test
    public void toSet()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(1, true, 2, false, 3, true);
        MutableSet<ObjectBooleanPair<Integer>> set = pairs.toSet();
        Verify.assertContainsAll(set, PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true));
    }

    @Test
    public void toMap()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(1, true, 2, false, 3, true);
        MutableMap<String, String> map =
                pairs.toMap(Functions.getToString(), Functions.getToString());
        Assert.assertEquals(UnifiedMap.newWithKeysValues("1:true", "1:true", "2:false", "2:false", "3:true", "3:true"), map);
    }

    @Test
    public void toSortedMap()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(1, true, 2, false, 3, true);
        MutableSortedMap<String, String> map =
                pairs.toSortedMap(Functions.getToString(), Functions.getToString());
        Assert.assertEquals(TreeSortedMap.newMapWith("1:true", "1:true", "2:false", "2:false", "3:true", "3:true"), map);
    }

    @Test
    public void toSortedMap_with_comparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(1, true, 2, false, 3, true);
        MutableSortedMap<String, String> map =
                pairs.toSortedMap(Comparators.reverseNaturalOrder(), Functions.getToString(), Functions.getToString());
        Assert.assertEquals(TreeSortedMap.newMapWith(Comparators.reverseNaturalOrder(), "1:true", "1:true", "2:false", "2:false", "3:true", "3:true"), map);
    }

    @Test
    public void testToString()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false);
        Assert.assertTrue("[1:true, 2:false]".equals(collection.toString())
                || "[2:false, 1:true]".equals(collection.toString()));
    }

    @Test
    public void makeString()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assert.assertEquals(collection.toString(), '[' + collection.makeString() + ']');
    }

    @Test
    public void makeStringWithSeparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assert.assertEquals(collection.toString(), '[' + collection.makeString(", ") + ']');
    }

    @Test
    public void makeStringWithSeparatorAndStartAndEnd()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assert.assertEquals(collection.toString(), collection.makeString("[", ", ", "]"));
    }

    @Test
    public void appendString()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Appendable builder = new StringBuilder();
        collection.appendString(builder);
        Assert.assertEquals(collection.toString(), '[' + builder.toString() + ']');
    }

    @Test
    public void appendStringWithSeparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Appendable builder = new StringBuilder();
        collection.appendString(builder, ", ");
        Assert.assertEquals(collection.toString(), '[' + builder.toString() + ']');
    }

    @Test
    public void appendStringWithSeparatorAndStartAndEnd()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Appendable builder = new StringBuilder();
        collection.appendString(builder, "[", ", ", "]");
        Assert.assertEquals(collection.toString(), builder.toString());
    }

    @Test
    public void groupBy()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Function<ObjectBooleanPair<Integer>, Boolean> function = object -> PrimitiveTuples.pair(Integer.valueOf(1), true).equals(object);

        Multimap<Boolean, ObjectBooleanPair<Integer>> multimap = collection.groupBy(function);
        Assert.assertEquals(3, multimap.size());
        Assert.assertTrue(multimap.containsKeyAndValue(Boolean.TRUE, PrimitiveTuples.pair(Integer.valueOf(1), true)));
        Assert.assertTrue(multimap.containsKeyAndValue(Boolean.FALSE, PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Assert.assertTrue(multimap.containsKeyAndValue(Boolean.FALSE, PrimitiveTuples.pair(Integer.valueOf(3), true)));
    }

    @Test
    public void groupByEach()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Function<ObjectBooleanPair<Integer>, MutableList<Boolean>> function = object -> Lists.mutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true).equals(object));

        Multimap<Boolean, ObjectBooleanPair<Integer>> multimap = collection.groupByEach(function);
        Assert.assertEquals(3, multimap.size());
        Assert.assertTrue(multimap.containsKeyAndValue(Boolean.TRUE, PrimitiveTuples.pair(Integer.valueOf(1), true)));
        Assert.assertTrue(multimap.containsKeyAndValue(Boolean.FALSE, PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Assert.assertTrue(multimap.containsKeyAndValue(Boolean.FALSE, PrimitiveTuples.pair(Integer.valueOf(3), true)));
    }

    @Test
    public void zip()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false);
        RichIterable<Pair<ObjectBooleanPair<Integer>, Integer>> result = collection.zip(Interval.oneTo(5));

        Assert.assertTrue(Bags.mutable.of(Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(1), true), 1), Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(2), false), 2)).equals(result.toBag())
                || Bags.mutable.of(Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(2), false), 1), Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(1), true), 2)).equals(result.toBag()));
    }

    @Test
    public void zipWithIndex()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false);
        RichIterable<Pair<ObjectBooleanPair<Integer>, Integer>> result = collection.zipWithIndex();
        Assert.assertTrue(Bags.mutable.of(Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(1), true), 0), Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(2), false), 1)).equals(result.toBag())
                || Bags.mutable.of(Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(2), false), 0), Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(1), true), 1)).equals(result.toBag()));
    }

    @Test
    public void chunk()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assert.assertEquals(Bags.immutable.of(FastList.newListWith(PrimitiveTuples.pair(Integer.valueOf(1), true)),
                FastList.newListWith(PrimitiveTuples.pair(Integer.valueOf(2), false)),
                FastList.newListWith(PrimitiveTuples.pair(Integer.valueOf(3), true))),
                collection.chunk(1).toBag());
    }

    @Test(expected = IllegalArgumentException.class)
    public void chunk_zero_throws()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        collection.chunk(0);
    }

    @Test
    public void chunk_large_size()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Verify.assertIterableSize(3, collection.chunk(10).getFirst());
    }

    @Test
    public void empty()
    {
        Verify.assertIterableEmpty(this.newWith());
        Assert.assertTrue(this.newWith().isEmpty());
        Assert.assertFalse(this.newWith().notEmpty());
    }

    @Test
    public void notEmpty()
    {
        RichIterable<ObjectBooleanPair<Integer>> notEmpty = this.newWith(1, true);
        Verify.assertIterableNotEmpty(notEmpty);
    }

    @Test
    public void aggregateByMutating()
    {
        Function0<AtomicInteger> valueCreator = Functions0.zeroAtomicInteger();
        Procedure2<AtomicInteger, ObjectBooleanPair<Integer>> sumAggregator = (aggregate, value) -> {
            aggregate.addAndGet((int) value.getOne());
        };
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(2, true, 3, false, 4, true);
        MapIterable<String, AtomicInteger> aggregation = collection.aggregateInPlaceBy(Functions.getToString(), valueCreator, sumAggregator);
        Assert.assertEquals(4, aggregation.get("4:true").intValue());
        Assert.assertEquals(3, aggregation.get("3:false").intValue());
        Assert.assertEquals(2, aggregation.get("2:true").intValue());
    }

    @Test
    public void aggregateByNonMutating()
    {
        Function0<Integer> valueCreator = Functions0.value(0);
        Function2<Integer, ObjectBooleanPair<Integer>, Integer> sumAggregator = (aggregate, value) -> (int) (aggregate + value.getOne());
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, false, 1, false, 2, true);
        MapIterable<String, Integer> aggregation = collection.aggregateBy(Functions.getToString(), valueCreator, sumAggregator);
        Assert.assertEquals(2, aggregation.get("2:true").intValue());
        Assert.assertEquals(1, aggregation.get("1:false").intValue());
    }
}
