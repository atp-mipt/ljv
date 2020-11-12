package org.atpfivt.ljv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class LJVTest {

    String expected(TestInfo testInfo, String actualGraph) {
        var method = testInfo.getTestMethod();
        if (method.isEmpty()) {
            return null;
        }

        var name = method.get().getName();

        var file = new File(getClass().getResource("/").getPath() + "graphviz/" + name);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    var out = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                    out.write(actualGraph);
                    out.close();
                } else {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }

        InputStream input = getClass().getResourceAsStream("/graphviz/" + name);
        if (input == null) {
            return null;
        }
        try {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    @Test
    void stringIsNotAPrimitiveType(TestInfo testInfo) {
        String actualGraph = new LJV().drawGraph("Hello");

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Hello case failed");
    }

    @Test
    void objectArraysHoldReferencesPrimitiveArraysHoldValues(TestInfo testInfo) {
        String actualGraph = new LJV()
                .setTreatAsPrimitive(String.class)
                .setIgnorePrivateFields(false)
                .drawGraph(
                        new Object[]{new String[]{"a", "b", "c"}, new int[]{1, 2, 3}}
                );

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Primitive array case failed");
    }

    @Test
    void assignmentDoesNotCreateANewObject(TestInfo testInfo) {
        String x = "Hello";
        String y = x;
        String actualGraph = new LJV().drawGraph(new Object[]{x, y});

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "One link Hello case failed");
    }

    @Test
    void assignmentWithNewCreateANewObject(TestInfo testInfo) {
        String x = "Hello";
        String y = new String(x);
        String actualGraph = new LJV().drawGraph(new Object[]{x, y});

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Without duplicate hello case failed");
    }

    @Test
    void stringIntern(TestInfo testInfo) {
        String x = "Hello";
        String y = "Hello";
        String actualGraph = new LJV().drawGraph(new Object[]{x, y.intern()});

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Without duplicate hello case failed");
    }

    @Test
    void multiDimensionalArrays(TestInfo testInfo) {
        String actualGraph = new LJV().drawGraph(new int[4][5]);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Multiarray case failed");
    }

    @Test
    void reversedMultiDimensionalArrays(TestInfo testInfo) {
        String actualGraph = new LJV().setDirection(Direction.LR).drawGraph(new int[4][5]);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Multiarray case failed");
    }

    @Test
    void cyclicalStructuresClassesWithAndWithoutAToStringAndWithoutContext(TestInfo testInfo) {
        Node n1 = new Node("A");
        n1.level = 1;
        AnotherNode n2 = new AnotherNode("B");
        n2.level = 2;
        AnotherNode n3 = new AnotherNode("C");
        n3.level = 2;

        n1.left = n2;
        n1.right = n3;
        n1.right.left = n1;
        n1.right.right = n1;

        String actualGraph = new LJV()
                .addFieldAttribute("left", "color=red,fontcolor=red")
                .addFieldAttribute("right", "color=blue,fontcolor=blue")
                .addClassAttribute(Node.class, "color=pink,style=filled")
                .addIgnoreField("level")
                .setTreatAsPrimitive(String.class)
                .setShowFieldNamesInLabels(false)
                .drawGraph(n1);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Nodes case without context failed");
    }

    @Test
    void paulsExample(TestInfo testInfo) {
        ArrayList<Object> a = new ArrayList<>();
        a.add(new Person("Albert", Gender.MALE, 35));
        a.add(new Person("Betty", Gender.FEMALE, 20));
        a.add(new java.awt.Point(100, -100));

        String actualGraph = new LJV()
                .setTreatAsPrimitive(String.class)
                .setTreatAsPrimitive(Gender.class)
                .addIgnoreField("hash")
                .addIgnoreField("count")
                .addIgnoreField("offset")
                .drawGraph(a);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Multiarray case failed");
    }

    @Test
    void testNull(TestInfo testInfo) {
        String actualGraph = new LJV().drawGraph(null);

        assertEquals("digraph Java {\n" +
                "\trankdir=\"TB\";\n" +
                "\tnode[shape=plaintext]\n" +
                "\tNULL[label=\"null\", shape=plaintext];\n" +
                "}\n", actualGraph);
    }

    @Test
    void treeMap(TestInfo testInfo) {
        TreeMap<String, Integer> map = new TreeMap<>();

        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);

        String actualGraph = new LJV()
                .setTreatAsPrimitive(Integer.class)
                .setTreatAsPrimitive(String.class)
                .drawGraph(map);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph);
    }

    @Test
    @Disabled
    void concurrentSkipListMap(TestInfo testInfo) {
        ConcurrentSkipListMap<String, Integer> map = new ConcurrentSkipListMap<>();

        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);

        String actualGraph = new LJV()
                .addFieldAttribute("node", "height=1.0")
                .setTreatAsPrimitive(Integer.class)
                .setTreatAsPrimitive(String.class)
                .drawGraph(map);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Case with concurrentskiplistmap was failed");
    }

    // TODO FIX
    @Test
    void linkedHashMap(TestInfo testInfo) {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);

        String actualGraph = new LJV()
//                .setTreatAsPrimitive(String.class)
//                .setTreatAsPrimitive(Integer.class)
                .drawGraph(map);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Case with linkedHashMap was failed");
    }

    @Test
    void hashMap(TestInfo testInfo) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);

        String actualGraph = new LJV()
                .setTreatAsPrimitive(String.class)
                .setTreatAsPrimitive(Integer.class)
                .drawGraph(map);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Case with HashMap was failed");
    }

    @Test
    void hashMapCollision2(TestInfo testInfo) {
        List<String> collisionString = new HashCodeCollision().genCollisionString(3);
        HashMap<String, Integer> map = new HashMap<>();

        for (int i = 0; i < collisionString.size(); i++) {
            map.put(collisionString.get(i), i);
        }

        String actualGraph = new LJV()
                .setTreatAsPrimitive(String.class)
                .setTreatAsPrimitive(Integer.class)
                .drawGraph(map);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Case with hashMapCollision was failed");
    }


    @Test
    void hashMapCollision3(TestInfo testInfo) {
        List<String> collisionString = new HashCodeCollision().genCollisionString(6);
        HashMap<String, Integer> map = new HashMap<>();

        for (int i = 0; i < collisionString.size(); i++) {
            map.put(collisionString.get(i), i);
        }

        String actualGraph = new LJV()
                .setTreatAsPrimitive(String.class)
                .setTreatAsPrimitive(Integer.class)
                .drawGraph(map);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Case with hashMapCollision was failed");
    }

    @Test
    void hashMapCollision4(TestInfo testInfo) {
        List<String> collisionString = new HashCodeCollision().genCollisionString(8);
        HashMap<String, Integer> map = new HashMap<>();

        for (int i = 0; i < collisionString.size(); i++) {
            map.put(collisionString.get(i), i);
        }

        String actualGraph = new LJV()
                .setTreatAsPrimitive(String.class)
                .setTreatAsPrimitive(Integer.class)
                .drawGraph(map);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Case with hashMapCollision was failed");
    }

    @Test
    void wrappedObjects(TestInfo testInfo) {
        String actualGraph = new LJV().drawGraph(new Example());

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Case with wrapped objects was failed");
    }

    @Test
    void linkedList(TestInfo testInfo) {
        LinkedList<Integer> linkedList = new LinkedList<>();
        linkedList.add(1);
        linkedList.add(42);
        linkedList.add(21);

        String actualGraph = new LJV()
                .setTreatAsPrimitive(Integer.class)
                .addFieldAttribute("next", "color=red,fontcolor=red")
                .addFieldAttribute("prev", "color=blue,fontcolor=blue")
                .addFieldAttribute("first", "color=red,fontcolor=red")
                .addFieldAttribute("last", "color=red,fontcolor=red")
                .drawGraph(linkedList);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Case with linked list was failed");
    }

    @Test
    void arrayDeque(TestInfo testInfo) {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 20; i++) {
            arrayDeque.addLast(i);
        }
        for (int i = 0; i < 18; i++) {
            arrayDeque.removeFirst();
        }

        String actualGraph = new LJV()
                .setTreatAsPrimitive(Integer.class).drawGraph(arrayDeque);

        String expectedGraph = expected(testInfo, actualGraph);

        assertEquals(expectedGraph, actualGraph, "Case with arrayDeque was failed");
    }
}
