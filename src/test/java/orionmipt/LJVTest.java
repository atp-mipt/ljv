package orionmipt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class LJVTest {

    static String expected_graph_0 = "digraph Java {\n" +
            "n550402284[label=\"java.lang.String|{coder: 0|hash: 0}\",shape=record];\n" +
            "n550402284 -> n1089418272[label=\"value\",fontsize=12];\n" +
            "n1089418272[shape=record, label=\"72|101|108|108|111\"];\n" +
            "}\n";

//     static String expected_graph_1 = "digraph Java {\n" + "n532854629[label=\"<f0>|<f1>\",shape=record];\n"
    //         + "n532854629:f0 -> n1971851377[label=\"0\",fontsize=12];\n"
    //         + "n1971851377[shape=record, label=\"a|b|c\"];\n"
    //         + "n532854629:f1 -> n712025048[label=\"1\",fontsize=12];\n" + "n712025048[shape=record, label=\"1|2|3\"];\n"
    //         + "}\n";

    // static String expected_graph_2 = "digraph Java {\n" + "n681384962[label=\"<f0>|<f1>\",shape=record];\n"
    //         + "n681384962:f0 -> n1453128758[label=\"0\",fontsize=12];\n"
    //         + "n1453128758[label=\"java.lang.String|{coder: 0|hash: 0|hashIsZero: false}\",shape=record];\n"
    //         + "n1453128758 -> n1131645570[label=\"value\",fontsize=12];\n"
    //         + "n1131645570[shape=record, label=\"72|101|108|108|111\"];\n"
    //         + "n681384962:f1 -> n1453128758[label=\"1\",fontsize=12];\n" + "}\n" + "";

    // static String expected_graph_3 = "digraph Java {\n" + "n770189387[label=\"<f0>|<f1>\",shape=record];\n"
    //         + "n770189387:f0 -> n1453128758[label=\"0\",fontsize=12];\n"
    //         + "n1453128758[label=\"java.lang.String|{coder: 0|hash: 0|hashIsZero: false}\",shape=record];\n"
    //         + "n1453128758 -> n1131645570[label=\"value\",fontsize=12];\n"
    //         + "n1131645570[shape=record, label=\"72|101|108|108|111\"];\n"
    //         + "n770189387:f1 -> n963522361[label=\"1\",fontsize=12];\n"
    //         + "n963522361[label=\"java.lang.String|{coder: 0|hash: 0|hashIsZero: false}\",shape=record];\n"
    //         + "n963522361 -> n1131645570[label=\"value\",fontsize=12];\n" + "}\n" + "";

    // static String expected_graph_4 = "digraph Java {\n" + "n175408781[label=\"<f0>|<f1>|<f2>|<f3>\",shape=record];\n"
    //         + "n175408781:f0 -> n315138752[label=\"0\",fontsize=12];\n"
    //         + "n315138752[shape=record, label=\"0|0|0|0|0\"];\n"
    //         + "n175408781:f1 -> n2114874018[label=\"1\",fontsize=12];\n"
    //         + "n2114874018[shape=record, label=\"0|0|0|0|0\"];\n"
    //         + "n175408781:f2 -> n911312317[label=\"2\",fontsize=12];\n"
    //         + "n911312317[shape=record, label=\"0|0|0|0|0\"];\n"
    //         + "n175408781:f3 -> n415186196[label=\"3\",fontsize=12];\n"
    //         + "n415186196[shape=record, label=\"0|0|0|0|0\"];\n" + "}\n" + "";

    // static String expected_graph_5 = "digraph Java {\n"
    //         + "n1337344609[label=\"Node|{top}\",color=pink,style=filled,shape=record];\n"
    //         + "n1337344609 -> n2015781843[label=\"left\",fontsize=12,color=red,fontcolor=red];\n"
    //         + "n2015781843[label=\"Node|{left|null|null}\",color=pink,style=filled,shape=record];\n"
    //         + "n1337344609 -> n428910174[label=\"right\",fontsize=12,color=blue,fontcolor=blue];\n"
    //         + "n428910174[label=\"Node|{right}\",color=pink,style=filled,shape=record];\n"
    //         + "n428910174 -> n1337344609[label=\"left\",fontsize=12,color=red,fontcolor=red];\n"
    //         + "n428910174 -> n1337344609[label=\"right\",fontsize=12,color=blue,fontcolor=blue];\n" + "}\n" + "";

    // static String expected_graph_6 = "digraph Java {\n"
    //         + "n1858609436[label=\"java.util.ArrayList|{size: 3}\",shape=record];\n"
    //         + "n1858609436 -> n1920387277[label=\"elementData\",fontsize=12];\n"
    //         + "n1920387277[label=\"<f0>|<f1>|<f2>|<f3>|<f4>|<f5>|<f6>|<f7>|<f8>|<f9>\",shape=record];\n"
    //         + "n1920387277:f0 -> n1414147750[label=\"0\",fontsize=12];\n"
    //         + "n1414147750[label=\"Person|{isMale: true|age: 35}\",shape=record];\n"
    //         + "n1414147750 -> n775931202[label=\"name\",fontsize=12];\n"
    //         + "n775931202[label=\"java.lang.String|{coder: 0|hashIsZero: false}\",shape=record];\n"
    //         + "n775931202 -> n22069592[label=\"value\",fontsize=12];\n"
    //         + "n22069592[shape=record, label=\"65|108|98|101|114|116\"];\n"
    //         + "n1920387277:f1 -> n1160003871[label=\"1\",fontsize=12];\n"
    //         + "n1160003871[label=\"Person|{isMale: false|age: 20}\",shape=record];\n"
    //         + "n1160003871 -> n1075738627[label=\"name\",fontsize=12];\n"
    //         + "n1075738627[label=\"java.lang.String|{coder: 0|hashIsZero: false}\",shape=record];\n"
    //         + "n1075738627 -> n282828951[label=\"value\",fontsize=12];\n"
    //         + "n282828951[shape=record, label=\"66|101|116|116|121\"];\n"
    //         + "n1920387277:f2 -> n394721749[label=\"2\",fontsize=12];\n"
    //         + "n394721749[label=\"java.awt.Point|{x: 100|y: -100}\",shape=record];\n" + "}\n" + "";

    @Test
    public void checkingExamples() {
        LJV.Context def = LJV.getDefaultContext();
        def.treatAsPrimitive(String.class);

        LJV.Context showAllCtx = new LJV.Context();
        showAllCtx.ignorePrivateFields = false;

        {
            // - String is not a primitive type
            String actual_graph_0 = LJV.drawGraph(showAllCtx, "Hello");
            // String expected_graph_0 = "";
            // try {
            //     expected_graph_0 = new String(Files.readAllBytes(Paths.get("graph_0.dot")));
            // } catch (IOException e) {e.printStackTrace();}

            // for (int i = 0; i < actual_graph_0.length(); i++) {
            //     if (actual_graph_0.charAt(i) != expected_graph_0.charAt(i)) {
            //         System.out.println("|" + actual_graph_0.charAt(i) + "| |" + expected_graph_0.charAt(i) + "| " + i);
            //     }
            // }

            // System.out.println(actual_graph_0);
            // System.out.println(expected_graph_0);

            assertEquals(expected_graph_0, actual_graph_0, "Hello case failed");
        }

        // - Object arrays hold references; primitive arrays hold values
        String actual_graph_1 = LJV.drawGraph(new Object[]{new String[]{"a", "b", "c"}, new int[]{1, 2, 3}});
        String expected_graph_1 = "";
        try {
            expected_graph_1 = new String(Files.readAllBytes(Paths.get("graph_1.dot")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected_graph_1, actual_graph_1, "Primitive array case failed");

        {
            // - Assignment does not create a new object
            String x = "Hello";
            String y = x;
            String actual_graph_2 = LJV.drawGraph(showAllCtx, new Object[]{x, y});
            String expected_graph_2 = "";
            try {
                expected_graph_2 = new String(Files.readAllBytes(Paths.get("graph_2.dot")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            assertEquals(expected_graph_2, actual_graph_2, "One link Hello case failed");
        }

        {
            String x = "Hello";
            String y = new String(x);
            String actual_graph_3 = LJV.drawGraph(showAllCtx, new Object[]{x, y});
            String expected_graph_3 = "";
            try {
                expected_graph_3 = new String(Files.readAllBytes(Paths.get("graph_3.dot")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            assertEquals(expected_graph_3, actual_graph_3, "Without duplicate hello case failed");
        }

        // - How multi-dimensional arrays are represented in Java.
        String actual_graph_4 = LJV.drawGraph(new int[4][5]);
        String expected_graph_4 = "";
        try {
            expected_graph_4 = new String(Files.readAllBytes(Paths.get("graph_4.dot")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected_graph_4, actual_graph_4, "Multiarray case failed");


        {
            // - Cyclical structures, classes (with and) without a toString.
            Node n = new Node("top", 2);
            n.left = new Node("left", 1);
            n.right = new Node("right", 1);
            n.right.left = n;
            n.right.right = n;

            LJV.Context ctx = new LJV.Context();
            ctx.setFieldAttribute("left", "color=red,fontcolor=red");
            ctx.setFieldAttribute("right", "color=blue,fontcolor=blue");
            ctx.setClassAttribute(Node.class, "color=pink,style=filled");
            ctx.ignoreField("level");
            ctx.ignoreField("ok");
            ctx.treatAsPrimitive(String.class);
            ctx.showFieldNamesInLabels = false;

            String actual_graph_5 = LJV.drawGraph(ctx, n);
            // assertEquals(expected_graph_5, actual_graph_5, "Nodes case failed");
        }

        {
            // - Paul's example
            LJV.Context ctx = new LJV.Context();
            ctx.ignoreField("hash");
            ctx.ignoreField("count");
            ctx.ignoreField("offset");

            ArrayList a = new ArrayList();
            a.add(new Person("Albert", true, 35));
            a.add(new Person("Betty", false, 20));
            a.add(new java.awt.Point(100, -100));
            String actual_graph_6 = LJV.drawGraph(ctx, a);
            // assertEquals(expected_graph_6, actual_graph_6, "Multiarray case failed");
        }
        assertTrue(true);
    }


    static class Node {
        String name;
        int level;
        boolean ok;
        Node left, right;

        public Node(String n, int l) {
            name = n;
            level = l;
            ok = l % 2 == 0;
        }

        public String toString() {
            return "";
        }
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
