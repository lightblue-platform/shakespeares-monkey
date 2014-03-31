package com.redhat.smonkey;

import java.util.Random;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class Utils {

    public static final Random rnd=new Random();

    public static Integer asInt(JsonNode value,Integer def) {
        return value==null?def:value.asInt();
    }

    public static Long asLong(JsonNode value,Long def) {
        return value==null?def:value.asLong();
    }

    public static String asString(JsonNode value,String def) {
        return value==null?def:value.asText();
    }

    public static double asDouble(JsonNode value,Double def) {
        return value==null?def:value.asDouble();
    }

    public static int rndi(int min,int max) {
        int n=max-min+1;
        int x=rnd.nextInt(n);
        return x+min;
    }
        
    public static long rndl(long min,long max) {
        long x=rnd.nextLong();
        if(x<0)
            x=-x;
        long n=max-min+1;
        return (x%n)+min;
    }
        

    /**
     * Returns true with 'percent' percent probability
     */
    public static boolean rndBool(int percent) {
        return rnd.nextInt(100)<percent;
    }

    /**
     * Pretty print a json doc
     */
    public static String prettyPrint(JsonNode node) {
        StringBuilder bld = new StringBuilder();
        prettyPrint(bld, node);
        return bld.toString();
    }

    /**
     * Pretty print a json doc
     */
    public static void prettyPrint(StringBuilder bld, JsonNode node) {
        toString(bld, node, 0, true);
    }


    private static boolean toString(StringBuilder bld,
                                    JsonNode node,
                                    int depth,
                                    boolean newLine) {
        if (node instanceof ArrayNode) {
            return arrayToString(bld, (ArrayNode) node, depth, newLine);
        } else if (node instanceof ObjectNode) {
            return objectToString(bld, (ObjectNode) node, depth, newLine);
        } else {
            return valueToString(bld, node, depth, newLine);
        }
    }

    private static boolean arrayToString(StringBuilder bld,
                                         ArrayNode node,
                                         int depth,
                                         boolean newLine) {
        if (newLine) {
            indent(bld, depth);
            newLine = false;
        }
        bld.append("[");
        boolean first = true;
        for (Iterator<JsonNode> itr = node.elements();
                itr.hasNext();) {
            if (first) {
                first = false;
            } else {
                bld.append(',');
            }
            newLine = toString(bld, itr.next(), depth + 1, newLine);
        }
        if (newLine) {
            indent(bld, depth);
        }
        bld.append(']');
        return false;
    }

    private static boolean objectToString(StringBuilder bld,
                                          ObjectNode node,
                                          int depth,
                                          boolean newLine) {
        if (newLine) {
            indent(bld, depth);
            newLine = false;
        }
        if (node.size() > 0) {
            bld.append("{\n");
            newLine = true;
        }
        boolean first = true;
        for (Iterator<Map.Entry<String, JsonNode>> itr = node.fields();
                itr.hasNext();) {
            if (first) {
                first = false;
            } else {
                if (newLine) {
                    indent(bld, depth);
                    newLine = false;
                }
                bld.append(',');
                bld.append('\n');
                newLine = true;
            }
            Map.Entry<String, JsonNode> entry = itr.next();
            indent(bld, depth);
            bld.append('\"');
            bld.append(entry.getKey());
            bld.append('\"');
            bld.append(':');
            newLine = toString(bld, entry.getValue(), depth + 1, false);
            if (newLine) {
                indent(bld, depth);
                newLine = false;
            }
        }
        if (node.size() > 0) {
            bld.append('\n');
            newLine = true;
        }
        if (newLine) {
            indent(bld, depth);
            newLine = false;
        }
        bld.append('}');
        return false;
    }

    private static boolean valueToString(StringBuilder bld,
                                         JsonNode node,
                                         int depth,
                                         boolean newLine) {
        if (newLine) {
            indent(bld, depth);
            newLine = false;
        }
        bld.append(node.toString());
        return newLine;
    }

    private static void indent(StringBuilder bld, int depth) {
        int n = depth * 2;
        for (int i = 0; i < n; i++) {
            bld.append(' ');
        }
    }

    private Utils() {}
}
