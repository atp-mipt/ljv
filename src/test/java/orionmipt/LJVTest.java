package orionmipt;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class LJVTest {
    @Test
    public void checkingExamples() {
        LJV.Context def = LJV.getDefaultContext();
        def.treatAsPrimitive(String.class);

        LJV.Context showAllCtx = LJV.newContext();
        showAllCtx.ignorePrivateFields = false;

        {
            // - String is not a primitive type
            LJV.drawGraph(showAllCtx, "Hello");
        }

        // - Object arrays hold references; primitive arrays hold values
        LJV.drawGraph(new Object[] { new String[] { "a", "b", "c" }, new int[] { 1, 2, 3 } });

        {
            // - Assignment does not create a new object
            String x = "Hello";
            String y = x;
            LJV.drawGraph(showAllCtx, new Object[] { x, y });
        }

        {
            String x = "Hello";
            String y = new String(x);
            LJV.drawGraph(showAllCtx, new Object[] { x, y });
        }

        // - How multi-dimensional arrays are represented in Java.
        LJV.drawGraph(new int[4][5]);

        {
            // - Cyclical structures, classes (with and) without a toString.
            Node n = new Node("top", 2);
            n.left = new Node("left", 1);
            n.right = new Node("right", 1);
            n.right.left = n;
            n.right.right = n;

            LJV.Context ctx = LJV.newContext();
            ctx.setFieldAttribute("left", "color=red,fontcolor=red");
            ctx.setFieldAttribute("right", "color=blue,fontcolor=blue");
            ctx.setClassAttribute(Node.class, "color=pink,style=filled");
            ctx.ignoreField("level");
            ctx.ignoreField("ok");
            ctx.treatAsPrimitive(String.class);
            ctx.showFieldNamesInLabels = false;

            LJV.drawGraph(ctx, n);
        }

        {
            // - Paul's example
            LJV.Context ctx = LJV.newContext();
            ctx.ignoreField("hash");
            ctx.ignoreField("count");
            ctx.ignoreField("offset");

            ArrayList a = new ArrayList();
            a.add(new Person("Albert", true, 35));
            a.add(new Person("Betty", false, 20));
            a.add(new java.awt.Point(100, -100));
            LJV.drawGraph(ctx, a);
        }

        String[] actual = new String[7];
        for (int i = 0; i < 7; i++) {
            try {
                actual[i] =
                    new String(Files.readAllBytes(Paths.get("graph-" + i + ".dot")));
            } catch (IOException e) { e.printStackTrace(); }
        }

        String[] expected = new String[7];
        for (int i = 0; i < 7; i++) {
            try {
                expected[i] =
                    new String(Files.readAllBytes(Paths.get("src/test/java/orionmipt/graph-" + i + ".dot")));
            } catch (IOException e) { e.printStackTrace(); }
        }

        for (int i = 0; i < 7; i++) {
            new File("graph-" + i + ".dot").delete();
        }

        assertArrayEquals(expected, actual, "One of the test end2end cases failed");
    }


    static class Node {
        String name;
        int level;
        boolean ok;
        Node left, right;
        public Node( String n, int l ) {
        name = n;
        level = l;
        ok = l%2 == 0;
        }
        public String toString( ) { return ""; }
    }



    static class Person {
        private String name;
        private boolean isMale;
        private int age;
        public Person(String n, boolean m, int a) {
        name = n;
        isMale = m;
        age = a;
        }
    }
}
