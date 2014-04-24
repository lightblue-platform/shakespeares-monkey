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

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;

import java.io.FileInputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class PropertyFile implements Generator {

    private static final Map<String,ParsedProperties> propertyFiles=new HashMap<>();
    private static final ParsedProperties defaultProperties;

    static {
        try {
            Properties p=new Properties();
            p.load(PropertyFile.class.getResourceAsStream("/default.properties"));
            defaultProperties=new ParsedProperties(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class ParsedProperties {
        private final Properties properties;
        private final Map<String,String[]> propertyMap=new HashMap<>();;

        public ParsedProperties(Properties p) {
            properties=p;
        }

        public String getValue(String key) {
            return properties.getProperty(key);
        }

        public String[] getValues(String key) {
            String[] value=propertyMap.get(key);
            if(value==null) {
                String valueStr=properties.getProperty(key);
                if(valueStr!=null) {
                    StringTokenizer tok=new StringTokenizer(valueStr,", ");
                    List<String> tokens=new ArrayList<>();
                    while(tok.hasMoreTokens())
                        tokens.add(tok.nextToken());
                    value=tokens.toArray(new String[tokens.size()]);
                    propertyMap.put(key,value);
                }
            }
            return value;
        }
    }

    @Override
    public String describe() {
        return 
            " { \"$propfile\": { \"file\":\"fileName\", \"choose\":\"property\", \"property\":\"property\" } }\n"+
            "   Reads a property file. If no 'file' is given, uses the internal default.properties.\n"+
            "   Then, if 'choose' is specified, reads the property given by \n"+
            "   'choose', and selects one of the values from that property, separated by comma \n"+
            "   and/or space. If instead 'property' is given, returns a node with that property value.";
    }

    @Override
    public String getName() {
        return "$propfile";
    }

    @Override
    public JsonNode generate(JsonNodeFactory nodeFactory,JsonNode data,Monkey mon) {
        String s=Utils.asString(data.get("file"),null);
        ParsedProperties p;
        if(s==null)
            p=defaultProperties;
        else {
            p=propertyFiles.get(s);
            if(p==null) {
                try {
                    Properties pr=new Properties();
                    pr.load(new FileInputStream(s));
                    propertyFiles.put(s,p=new ParsedProperties(pr));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        s=Utils.asString(data.get("choose"),null);
        if(s!=null) {
            String[] values=p.getValues(s);
            if(values!=null) {
                int x=Utils.rnd.nextInt(values.length);
                return nodeFactory.textNode(values[x]);
            } else
                throw new RuntimeException("No property:"+s);
        } else {
            s=Utils.asString(data.get("property"),null);
            if(s==null)
                throw new RuntimeException("choose or property is required for $propfile");
            String value=p.getValue(s);
            if(value==null)
                throw new RuntimeException("No property:"+s);
            return nodeFactory.textNode(value);
        }
    }

 }
