/*
 Copyright 2013 Red Hat, Inc. and/or its affiliates.

 This file is part of lightblue.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.redhat.smonkey;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Monkey {

    private final ObjectNode template;
    private final GeneratorRegistry registry;
    private static final JsonNodeFactory nodeFactory=JsonNodeFactory.withExactBigDecimals(true);

    public static final JsonNode FIELD_DOES_NOT_EXIST=nodeFactory.objectNode();

    public Monkey(GeneratorRegistry registry,ObjectNode template) {
        this.registry=registry;
        this.template=template;
    }

    static public Monkey getInstance(GeneratorRegistry registry,String file) throws IOException {
        return new Monkey(registry,(ObjectNode)loadJson(file));
    }

    public JsonNode generate() {
        return generateNode(template);
    }

    public static JsonNode json(String s) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        return mapper.readTree(s);
    }

    public static String loadFile(String file) throws IOException {
        StringBuilder buff = new StringBuilder();
        try (InputStream is = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader reader = new BufferedReader(isr)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    buff.append(line).append("\n");
                }
            } 
        return buff.toString();
    }

    public static JsonNode loadJson(String file) throws IOException {
        return json(loadFile(file));
    }

    public JsonNode generateNode(JsonNode templateNode) {
        if(templateNode instanceof ObjectNode)
            return generateObjectNode((ObjectNode)templateNode);
        else if(templateNode instanceof ArrayNode) 
            return generateArrayNode((ArrayNode)templateNode);
        else {
            JsonNode node=attemptGenerateValue(templateNode);
            if(node==null)
                return templateNode.deepCopy();
            else
                return node;
        }
    }
    
    private JsonNode attemptGenerateValue(JsonNode templateNode) {
        String fname=null;
        if(templateNode.size()==1) {
            fname=templateNode.fieldNames().next();
        } else if(!templateNode.isContainerNode()) {
            fname=templateNode.asText();
        }
            // Check if this is an escaped name
        if(fname!=null&&!fname.startsWith("\\")) {
            JsonNode dataNode=templateNode.get(fname);
            if(dataNode==null)
                dataNode=templateNode;
            JsonNode value=generateValue(fname,dataNode);
            if(value!=null)
                return value;
        }
        return null;
    }

    private JsonNode generateObjectNode(ObjectNode templateNode) {
        JsonNode newValue=attemptGenerateValue(templateNode);
        if(newValue==null) {
            ObjectNode node=nodeFactory.objectNode();
            for(Iterator<Map.Entry<String,JsonNode>> fields=templateNode.fields();fields.hasNext();) {
                Map.Entry<String,JsonNode> field=fields.next();
                String fieldName=unescape(field.getKey());
                JsonNode fieldValue=field.getValue();
                JsonNode newNode=generateNode(fieldValue);
                if(newNode!=FIELD_DOES_NOT_EXIST)
                    node.put(fieldName,newNode);
            }
            newValue=node;
        }
        return newValue;
    }

    private String unescape(String s) {
        if(s.length()>0)
            if(s.charAt(0)=='\\')
                return s.substring(1);
        return s;
    }

    private JsonNode generateArrayNode(ArrayNode templateNode) {
        ArrayNode node=nodeFactory.arrayNode();
        for(Iterator<JsonNode> elements=templateNode.elements();elements.hasNext();) {
            JsonNode element=elements.next();
            JsonNode value=generateNode(element);
            if(value!=FIELD_DOES_NOT_EXIST)
                node.add(value);
        }
        return node;
    }


    /**
     * This is the function that generates random data
     *
     * @param name Name of the generation function
     * @param data Parameters to the generation function
     *
     * @return null if no such generation function exists, otherwise,
     * randomly generated data
     */
    private JsonNode generateValue(String name,JsonNode data) {
        Generator rnd=registry.get(name);
        if(rnd!=null) {
            return rnd.generate(nodeFactory,data,this);
        } else
            return null;
    }

    public static void main(String[] args) throws Exception {
        GeneratorRegistry registry=GeneratorRegistry.getDefaultInstance();
        if(args.length>0) {
            Monkey m=Monkey.getInstance(registry,args[0]);
            int n=1;
            if(args.length==2) 
                n=Integer.valueOf(args[1]);
            JsonNode node;
            if(n>1) {
                node=nodeFactory.arrayNode();
                for(int i=0;i<n;i++)
                    ((ArrayNode)node).add(m.generate());
            } else
                node=m.generate();
            System.out.println(Utils.prettyPrint(node));
        } else
            printHelp(registry);
    }

    private static void printHelp(GeneratorRegistry reg) {
        System.out.println("Generates data based on a template. Pass template file as arg, and an optional number of records. \n"+
                           "Template file is a JSON file. If a field value is one of the generation operators,\n"+
                           "contents of that field is generated by that operator. Use '\\' to escape an \n"+
                           "operator name.\n\n");
        for(String name:reg.getNames()) {
            System.out.println(reg.get(name).describe());
        }
    }

}
