package ljv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class LJVTest {

    @Test
    void stringIsNotAPrimitiveType() {
        String actual_graph = new LJV().drawGraph("Hello");

        String expected_graph = "digraph Java {\n" +
                "node[shape=plaintext]\n" +
                "n1[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='2'>java.lang.String</td></tr>\n" +
                "<tr><td>coder: 0</td>\n" +
                "<td>hash: 0</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n2[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>72</td><td>101</td><td>108</td><td>108</td><td>111</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n1 -> n2[label=\"value\",fontsize=12];\n" +
                "}\n";

        assertEquals(expected_graph, actual_graph, "Hello case failed");
    }

    @Test
    void objectArraysHoldReferencesPrimitiveArraysHoldValues() {
        String actual_graph = new LJV()
                .setTreatAsPrimitive(String.class)
                .setIgnorePrivateFields(false)
                .drawGraph(
                new Object[]{new String[]{"a", "b", "c"}, new int[]{1, 2, 3}}
                );

        String expected_graph = "digraph Java {\n" +
                "node[shape=plaintext]\n" +
                "n1[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0' cellpadding='9'>\n" +
                "<tr>\n" +
                "<td port=\"f0\"></td><td port=\"f1\"></td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n2[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>a</td><td>b</td><td>c</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n1:f0 -> n2[label=\"0\",fontsize=12];\n" +
                "n3[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>1</td><td>2</td><td>3</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n1:f1 -> n3[label=\"1\",fontsize=12];\n" +
                "}\n";

        assertEquals(expected_graph, actual_graph, "Primitive array case failed");
    }

    @Test
    void assignmentDoesNotCreateANewObject() {
        String x = "Hello";
        String y = x;
        String actual_graph = new LJV().drawGraph(new Object[]{x, y});

        String expected_graph = "digraph Java {\n" +
                "node[shape=plaintext]\n" +
                "n1[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0' cellpadding='9'>\n" +
                "<tr>\n" +
                "<td port=\"f0\"></td><td port=\"f1\"></td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n2[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='2'>java.lang.String</td></tr>\n" +
                "<tr><td>coder: 0</td>\n" +
                "<td>hash: 0</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n3[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>72</td><td>101</td><td>108</td><td>108</td><td>111</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n2 -> n3[label=\"value\",fontsize=12];\n" +
                "n1:f0 -> n2[label=\"0\",fontsize=12];\n" +
                "n1:f1 -> n2[label=\"1\",fontsize=12];\n" +
                "}\n";


        assertEquals(expected_graph, actual_graph, "One link Hello case failed");
    }

    @Test
    void assignmentWithNewCreateANewObject() {
        String x = "Hello";
        String y = new String(x);
        String actual_graph = new LJV().drawGraph(new Object[]{x, y});

        String expected_graph = "digraph Java {\n" +
                "node[shape=plaintext]\n" +
                "n1[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0' cellpadding='9'>\n" +
                "<tr>\n" +
                "<td port=\"f0\"></td><td port=\"f1\"></td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n2[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='2'>java.lang.String</td></tr>\n" +
                "<tr><td>coder: 0</td>\n" +
                "<td>hash: 0</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n3[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>72</td><td>101</td><td>108</td><td>108</td><td>111</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n2 -> n3[label=\"value\",fontsize=12];\n" +
                "n1:f0 -> n2[label=\"0\",fontsize=12];\n" +
                "n4[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='2'>java.lang.String</td></tr>\n" +
                "<tr><td>coder: 0</td>\n" +
                "<td>hash: 0</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n4 -> n3[label=\"value\",fontsize=12];\n" +
                "n1:f1 -> n4[label=\"1\",fontsize=12];\n" +
                "}\n";


        assertEquals(expected_graph, actual_graph, "Without duplicate hello case failed");
    }

    @Test
    void multiDimensionalArrays() {
        String actual_graph = new LJV().drawGraph(new int[4][5]);

        String expected_graph = "digraph Java {\n" +
                "node[shape=plaintext]\n" +
                "n1[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0' cellpadding='9'>\n" +
                "<tr>\n" +
                "<td port=\"f0\"></td><td port=\"f1\"></td><td port=\"f2\"></td><td port=\"f3\"></td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n2[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>0</td><td>0</td><td>0</td><td>0</td><td>0</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n1:f0 -> n2[label=\"0\",fontsize=12];\n" +
                "n3[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>0</td><td>0</td><td>0</td><td>0</td><td>0</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n1:f1 -> n3[label=\"1\",fontsize=12];\n" +
                "n4[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>0</td><td>0</td><td>0</td><td>0</td><td>0</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n1:f2 -> n4[label=\"2\",fontsize=12];\n" +
                "n5[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>0</td><td>0</td><td>0</td><td>0</td><td>0</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n1:f3 -> n5[label=\"3\",fontsize=12];\n" +
                "}\n";

        assertEquals(expected_graph, actual_graph, "Multiarray case failed");
    }

    @Test
    void cyclicalStructuresClassesWithAndWithoutAToString() {
        Node n = new Node("top", 2);
        n.left = new Node("left", 1);
        n.right = new Node("right", 1);
        n.right.left = n;
        n.right.right = n;

        String actual_graph = new LJV()
                .addFieldAttribute("left", "color=red,fontcolor=red")
                .addFieldAttribute("right", "color=blue,fontcolor=blue")
                .addClassAttribute(Node.class, "color=pink,style=filled")
                .addIgnoreField("level")
                .addIgnoreField("ok")
                .setTreatAsPrimitive(String.class)
                .setShowFieldNamesInLabels(false)
                .drawGraph(n);

        String expected_graph = "digraph Java {\n" +
                "node[shape=plaintext]\n" +
                "n1[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='1'>Node</td></tr>\n" +
                "<tr><td>top</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">,color=pink,style=filled];\n" +
                "n2[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='3'>Node</td></tr>\n" +
                "<tr><td>left</td>\n" +
                "<td>null</td>\n" +
                "<td>null</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">,color=pink,style=filled];\n" +
                "n1 -> n2[label=\"left\",fontsize=12,color=red,fontcolor=red];\n" +
                "n3[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='1'>Node</td></tr>\n" +
                "<tr><td>right</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">,color=pink,style=filled];\n" +
                "n3 -> n1[label=\"left\",fontsize=12,color=red,fontcolor=red];\n" +
                "n3 -> n1[label=\"right\",fontsize=12,color=blue,fontcolor=blue];\n" +
                "n1 -> n3[label=\"right\",fontsize=12,color=blue,fontcolor=blue];\n" +
                "}\n";


        assertEquals(expected_graph, actual_graph, "Nodes case with context failed");
    }

    @Test
    void cyclicalStructuresClassesWithAndWithoutAToStringAndWithoutContext() {
        Node n = new Node("top", 2);
        n.left = new Node("left", 1);
        n.right = new Node("right", 1);
        n.right.left = n;
        n.right.right = n;

        String actual_graph = new LJV()
                .addFieldAttribute("left", "color=red,fontcolor=red")
                .addFieldAttribute("right", "color=blue,fontcolor=blue")
                .addClassAttribute(Node.class, "color=pink,style=filled")
                .addIgnoreField("level")
                .addIgnoreField("ok")
                .setTreatAsPrimitive(String.class)
                .setShowFieldNamesInLabels(false)
                .drawGraph(n);

        String expected_graph = "digraph Java {\n" +
                "node[shape=plaintext]\n" +
                "n1[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='1'>Node</td></tr>\n" +
                "<tr><td>top</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">,color=pink,style=filled];\n" +
                "n2[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='3'>Node</td></tr>\n" +
                "<tr><td>left</td>\n" +
                "<td>null</td>\n" +
                "<td>null</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">,color=pink,style=filled];\n" +
                "n1 -> n2[label=\"left\",fontsize=12,color=red,fontcolor=red];\n" +
                "n3[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='1'>Node</td></tr>\n" +
                "<tr><td>right</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">,color=pink,style=filled];\n" +
                "n3 -> n1[label=\"left\",fontsize=12,color=red,fontcolor=red];\n" +
                "n3 -> n1[label=\"right\",fontsize=12,color=blue,fontcolor=blue];\n" +
                "n1 -> n3[label=\"right\",fontsize=12,color=blue,fontcolor=blue];\n" +
                "}\n";


        assertEquals(expected_graph, actual_graph, "Nodes case without context failed");
    }

    @Test
    void paulsExample() {
        ArrayList<Object> a = new ArrayList<>();
        a.add(new Person("Albert", true, 35));
        a.add(new Person("Betty", false, 20));
        a.add(new java.awt.Point(100, -100));

        String actual_graph = new LJV()
                .addIgnoreField("hash")
                .addIgnoreField("count")
                .addIgnoreField("offset")
                .drawGraph(a);

        String expected_graph = "digraph Java {\n" +
                "node[shape=plaintext]\n" +
                "n1[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='1'>java.util.ArrayList</td></tr>\n" +
                "<tr><td>size: 3</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n2[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0' cellpadding='9'>\n" +
                "<tr>\n" +
                "<td port=\"f0\"></td><td port=\"f1\"></td><td port=\"f2\"></td><td port=\"f3\"></td><td port=\"f4\"></td><td port=\"f5\"></td><td port=\"f6\"></td><td port=\"f7\"></td><td port=\"f8\"></td><td port=\"f9\"></td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n3[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='2'>Person</td></tr>\n" +
                "<tr><td>isMale: true</td>\n" +
                "<td>age: 35</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n4[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='1'>java.lang.String</td></tr>\n" +
                "<tr><td>coder: 0</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n5[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>65</td><td>108</td><td>98</td><td>101</td><td>114</td><td>116</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n4 -> n5[label=\"value\",fontsize=12];\n" +
                "n3 -> n4[label=\"name\",fontsize=12];\n" +
                "n2:f0 -> n3[label=\"0\",fontsize=12];\n" +
                "n6[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='2'>Person</td></tr>\n" +
                "<tr><td>isMale: false</td>\n" +
                "<td>age: 20</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n7[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='1'>java.lang.String</td></tr>\n" +
                "<tr><td>coder: 0</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n8[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr>\n" +
                "<td>66</td><td>101</td><td>116</td><td>116</td><td>121</td></tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n7 -> n8[label=\"value\",fontsize=12];\n" +
                "n6 -> n7[label=\"name\",fontsize=12];\n" +
                "n2:f1 -> n6[label=\"1\",fontsize=12];\n" +
                "n9[label=<\n" +
                "<table border='0' cellborder='1' cellspacing='0'>\n" +
                "<tr><td colspan='2'>java.awt.Point</td></tr>\n" +
                "<tr><td>x: 100</td>\n" +
                "<td>y: -100</td>\n" +
                "</tr>\n" +
                "</table>\n" +
                ">];\n" +
                "n2:f2 -> n9[label=\"2\",fontsize=12];\n" +
                "n1 -> n2[label=\"elementData\",fontsize=12];\n" +
                "}\n";

        assertEquals(expected_graph, actual_graph, "Multiarray case failed");
    }

    @Test
    void testNull() {
        String actualGraph = new LJV().drawGraph(null);

        assertEquals("digraph Java {\n" +
                "node[shape=plaintext]\n" +
                "NULL[label=\"null\", shape=plaintext];\n" +
                "}\n", actualGraph);
    }

}
