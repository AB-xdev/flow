/*
 * Copyright 2000-2025 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.internal;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JreJsonNull;

public class JsonUtilsTest {
    @Test
    public void testEquals() {
        // Equal
        Assert.assertTrue(
                JsonUtils.jsonEquals(Json.create(true), Json.create(true)));
        Assert.assertTrue(
                JsonUtils.jsonEquals(Json.create("foo"), Json.create("foo")));
        Assert.assertTrue(
                JsonUtils.jsonEquals(Json.create(3.14), Json.create(3.14)));
        Assert.assertTrue(
                JsonUtils.jsonEquals(Json.createNull(), Json.createNull()));
        Assert.assertTrue(
                JsonUtils.jsonEquals(createTestObject1(), createTestObject1()));
        Assert.assertTrue(
                JsonUtils.jsonEquals(createTestArray1(), createTestArray1()));

        // Non-equal with matching types
        Assert.assertFalse(
                JsonUtils.jsonEquals(Json.create(true), Json.create(false)));
        Assert.assertFalse(
                JsonUtils.jsonEquals(Json.create("foo"), Json.create("oof")));
        Assert.assertFalse(
                JsonUtils.jsonEquals(Json.create(3.14), Json.create(3.142)));
        Assert.assertFalse(
                JsonUtils.jsonEquals(createTestObject1(), createTestObject2()));
        Assert.assertFalse(
                JsonUtils.jsonEquals(createTestArray1(), createTestArray2()));

        // Non-equal with different types
        Assert.assertFalse(
                JsonUtils.jsonEquals(Json.create(true), Json.create("true")));
        Assert.assertFalse(
                JsonUtils.jsonEquals(Json.create(3.14), Json.create("3.14")));
        Assert.assertFalse(
                JsonUtils.jsonEquals(Json.createNull(), Json.create("null")));
        Assert.assertFalse(
                JsonUtils.jsonEquals(Json.createObject(), Json.create("{}")));
        Assert.assertFalse(
                JsonUtils.jsonEquals(Json.createArray(), Json.create(0)));
        Assert.assertFalse(
                JsonUtils.jsonEquals(createTestArray1(), createTestObject1()));
    }

    @Test(expected = AssertionError.class)
    public void testEquals_firstNull_throws() {
        JsonUtils.jsonEquals(null, Json.createNull());
    }

    @Test(expected = AssertionError.class)
    public void testEquals_secondNull_throws() {
        JsonUtils.jsonEquals(Json.createNull(), null);
    }

    private static JsonObject createTestObject1() {
        JsonObject object = Json.createObject();

        object.put("foo", "foo");
        object.put("bar", createTestArray1());
        object.put("baz", Json.createObject());

        return object;
    }

    private static JsonObject createTestObject2() {
        JsonObject object = Json.createObject();

        object.put("foo", "oof");
        object.put("bar", createTestArray2());
        object.put("baz", Json.createArray());

        return object;
    }

    private static JsonArray createTestArray1() {
        return Stream.of(Json.create("foo"), Json.createObject())
                .collect(JsonUtils.asArray());
    }

    private static JsonArray createTestArray2() {
        return Stream.of(Json.create("bar"), Json.createArray())
                .collect(JsonUtils.asArray());
    }

    private static JsonArray createNumberArray(double... items) {
        return DoubleStream.of(items).mapToObj(Json::create)
                .collect(JsonUtils.asArray());
    }

    @Test
    public void collectEmptyStream() {
        Stream<JsonValue> jsonValueStream = Stream.empty();
        JsonArray a = jsonValueStream.collect(JsonUtils.asArray());
        Assert.assertEquals(0, a.length());
    }

    @Test
    public void testStream() {
        JsonArray array = createTestArray1();
        List<JsonValue> list = JsonUtils.stream(array).toList();

        Assert.assertEquals(2, list.size());
        Assert.assertEquals("foo", list.get(0).asString());
        Assert.assertTrue(
                JsonUtils.jsonEquals(list.get(1), Json.createObject()));
    }

    @Test
    public void testObjectStream() {
        JsonArray array = Stream.of(Json.createObject(), createTestObject1(),
                createTestObject2()).collect(JsonUtils.asArray());

        List<JsonObject> objects = JsonUtils.objectStream(array).toList();

        Assert.assertEquals(3, objects.size());
        Assert.assertTrue(
                JsonUtils.jsonEquals(Json.createObject(), objects.get(0)));
        Assert.assertTrue(
                JsonUtils.jsonEquals(createTestObject1(), objects.get(1)));
        Assert.assertTrue(
                JsonUtils.jsonEquals(createTestObject2(), objects.get(2)));
    }

    @Test
    public void testNumberStream() {
        double[] values = new double[] { 3.14, 42, Double.MAX_VALUE };

        JsonArray array = DoubleStream.of(values).mapToObj(Json::create)
                .collect(JsonUtils.asArray());

        DoubleStream numberStream = JsonUtils.numberStream(array);

        Assert.assertArrayEquals(values, numberStream.toArray(), 0);
    }

    @Test
    public void testAsArray() {
        Stream<JsonValue> stream = JsonUtils.stream(createTestArray1());

        JsonArray array = stream.collect(JsonUtils.asArray());

        Assert.assertTrue(JsonUtils.jsonEquals(createTestArray1(), array));
    }

    @Test
    public void testCreateArray() {
        JsonArray array = JsonUtils.createArray(Json.create("string"),
                Json.createNull());

        Assert.assertEquals(2, array.length());
        Assert.assertEquals("string", array.getString(0));
        Assert.assertSame(JreJsonNull.class, array.get(1).getClass());
    }

    @Test
    public void testCreateEmptyArray() {
        JsonArray array = JsonUtils.createArray();

        Assert.assertEquals(0, array.length());
    }

    @Test
    public void createObject() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("integer", Integer.valueOf(3));
        map.put("string", "foo");

        JsonObject object = JsonUtils.createObject(map, item -> {
            if (item instanceof Integer) {
                return Json.create(((Integer) item).doubleValue());
            } else {
                return Json.create(String.valueOf(item));
            }
        });

        Assert.assertEquals(2, object.keys().length);
        Assert.assertEquals(3, object.getNumber("integer"), 0);
        Assert.assertEquals("foo", object.getString("string"));
    }

    @Test
    public void testCreateEmptyObject() {
        JsonObject object = JsonUtils.createObject(Collections.emptyMap(),
                item -> {
                    throw new AssertionError("Callback should not be called");
                });

        Assert.assertEquals(0, object.keys().length);
    }

    public static class SimpleBean {
        private String string = "value";
        private int number = 1;
        private float flt = 2.3f;
        private double dbl = 4.56;

        public String getString() {
            return string;
        }

        public int getNumber() {
            return number;
        }

        public float getFlt() {
            return flt;
        }

        public double getDbl() {
            return dbl;
        }
    }

    public static class ParentBean {
        private String parentValue = "parent";
        private ChildBean child = new ChildBean();

        public String getParentValue() {
            return parentValue;
        }

        public ChildBean getChild() {
            return child;
        }
    }

    public static class ChildBean {
        private String childValue = "child";

        public String getChildValue() {
            return childValue;
        }
    }

    public static class BeanWithTemporalFields {

        public LocalTime localTime = LocalTime.of(10, 23, 55);

        public LocalDate localDate = LocalDate.of(2024, 6, 26);

        public LocalDateTime localDateTime = localDate.atTime(localTime);

        public java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        public Date date = new Date(sqlDate.getTime());

        public ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime,
                ZoneId.of("Europe/Rome"));

        public Duration duration = Duration.ofSeconds(10);
    }

    public static class ListAndMapBean {
        private Map<String, Integer> integerMap = new HashMap<>();
        private Map<String, ChildBean> childBeanMap = new HashMap<>();

        private List<Integer> integerList = new ArrayList<>();
        private List<ChildBean> childBeanList = new ArrayList<>();
        {
            integerMap.put("one", 1);
            integerMap.put("two", 2);

            integerList.add(3);
            integerList.add(2);
            integerList.add(1);

            ChildBean firstChild = new ChildBean();
            firstChild.childValue = "firstChildValue";
            childBeanMap.put("First", firstChild);
            ChildBean secondChild = new ChildBean();
            secondChild.childValue = "secondChildValue";
            childBeanMap.put("Second", secondChild);

            childBeanList.add(firstChild);
            childBeanList.add(secondChild);
        }

        // these getters are needed for bean serialization:
        public Map<String, Integer> getIntegerMap() {
            return integerMap;
        }

        public List<ChildBean> getChildBeanList() {
            return childBeanList;
        }

        public List<Integer> getIntegerList() {
            return integerList;
        }

        public Map<String, ChildBean> getChildBeanMap() {
            return childBeanMap;
        }
    }

    @Test
    public void simpleBeanToJson() {
        JsonObject json = JsonUtils.beanToJson(new SimpleBean());
        Assert.assertEquals("value", json.getString("string"));
        Assert.assertEquals(1.0, json.getNumber("number"), 0.0);
        Assert.assertEquals(2.3, json.getNumber("flt"), 0.0);
        Assert.assertEquals(4.56, json.getNumber("dbl"), 0.0);
    }

    @Test
    public void nestedBeanToJson() {
        JsonObject json = JsonUtils.beanToJson(new ParentBean());
        Assert.assertEquals("parent", json.getString("parentValue"));
        JsonObject child = json.getObject("child");
        Assert.assertEquals("child", child.getString("childValue"));
    }

    @Test
    public void beanWithTimeFields() {
        BeanWithTemporalFields bean = new BeanWithTemporalFields();
        JsonObject json = JsonUtils.beanToJson(bean);

        Assert.assertTrue("LocalTime not serialized as expected",
                JsonUtils.jsonEquals(createNumberArray(10, 23, 55),
                        json.getArray("localTime")));
        Assert.assertTrue("LocalDate not serialized as expected",
                JsonUtils.jsonEquals(createNumberArray(2024, 6, 26),
                        json.getArray("localDate")));
        Assert.assertTrue("LocalDateTime not serialized as expected",
                JsonUtils.jsonEquals(createNumberArray(2024, 6, 26, 10, 23, 55),
                        json.getArray("localDateTime")));
        Assert.assertEquals("ZonedDateTime not serialized as expected",
                bean.zonedDateTime.toEpochSecond(),
                json.getNumber("zonedDateTime"), 0);
        Assert.assertEquals("ZonedDateTime not serialized as expected",
                bean.sqlDate.getTime(), json.getNumber("sqlDate"), 0);
        Assert.assertEquals("ZonedDateTime not serialized as expected",
                bean.date.getTime(), json.getNumber("date"), 0);
        Assert.assertEquals(10.0, json.getNumber("duration"), 0);
    }

    @Test
    public void nullChildBean() {
        ParentBean bean = new ParentBean();
        bean.child = null;

        JsonObject json = JsonUtils.beanToJson(bean);
        Assert.assertEquals(Json.createNull(), json.get("child"));
    }

    @Test
    public void beanWithListAndMap() {
        ListAndMapBean bean = new ListAndMapBean();

        JsonObject json = JsonUtils.beanToJson(bean);

        JsonObject integerMap = json.getObject("integerMap");
        Assert.assertEquals(1, integerMap.getNumber("one"), 0);
        Assert.assertEquals(2, integerMap.getNumber("two"), 0);

        JsonObject childBeanMap = json.getObject("childBeanMap");
        JsonObject firstChild = childBeanMap.getObject("First");
        Assert.assertEquals("firstChildValue",
                firstChild.getString("childValue"));
        JsonObject secondChild = childBeanMap.getObject("Second");
        Assert.assertEquals("secondChildValue",
                secondChild.getString("childValue"));

        JsonArray integerList = json.getArray("integerList");
        Assert.assertEquals(3, integerList.get(0).asNumber(), 0);
        Assert.assertEquals(2, integerList.get(1).asNumber(), 0);
        Assert.assertEquals(1, integerList.get(2).asNumber(), 0);

        JsonArray childBeanList = json.getArray("childBeanList");
        Assert.assertEquals("firstChildValue",
                ((JsonObject) childBeanList.get(0)).getString("childValue"));
        Assert.assertEquals("secondChildValue",
                ((JsonObject) childBeanList.get(1)).getString("childValue"));
    }

    @Test
    public void simpleBeanListToJson() {
        ArrayList<SimpleBean> list = new ArrayList<>();
        SimpleBean bean1 = new SimpleBean();
        bean1.string = "bean1";
        SimpleBean bean2 = new SimpleBean();
        bean2.string = "bean2";
        list.add(bean1);
        list.add(bean2);
        JsonArray json = JsonUtils.listToJson(list);

        Assert.assertEquals("bean1", json.getObject(0).getString("string"));
        Assert.assertEquals("bean2", json.getObject(1).getString("string"));
    }

    @Test
    public void simpleMapToJson() {
        Map<String, Object> map = new HashMap<>();
        SimpleBean bean1 = new SimpleBean();
        bean1.string = "bean1";
        SimpleBean bean2 = new SimpleBean();
        bean2.string = "bean2";

        map.put("one", bean1);
        map.put("two", bean2);
        JsonObject json = JsonUtils.mapToJson(map);

        Assert.assertEquals("bean1", json.getObject("one").getString("string"));
        Assert.assertEquals("bean2", json.getObject("two").getString("string"));
    }

    public record Person(String name, double age, boolean canSwim) {

    }

    @Test
    public void testReadObject() {
        JsonObject jsonObject = Json.createObject();
        jsonObject.put("name", "Foo");
        jsonObject.put("age", 30.5);
        jsonObject.put("canSwim", true);

        Person person = JsonUtils.readToObject(jsonObject, Person.class);

        Assert.assertEquals("Foo", person.name);
        Assert.assertEquals(30.5, person.age, 0.0);
        Assert.assertEquals(true, person.canSwim);
    }

}
