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

import java.io.FileInputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class PropertyFile implements Generator {

    private static final Map<String,Properties> propertyFiles=new HashMap<>();

    @Override
    public String describe() {
        return 
            " { \"$propfile\": { \"file\":\"fileName\", \"choose\":\"property\", \"property\":\"property\" } }\n"+
            "   Reads a property file. Then, if 'choose' is specified, reads the property given by \n"+
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
        if(s==null)
            throw new RuntimeException("Property file expected");
        Properties p=propertyFiles.get(s);
        if(p==null) {
            try {
                p=new Properties();
                p.load(new FileInputStream(s));
                propertyFiles.put(s,p);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        s=Utils.asString(data.get("choose"),null);
        if(s!=null) {
            String value=p.getProperty(s);
            if(value!=null) {
                StringTokenizer tok=new StringTokenizer(value,", ");
                int x=Utils.rnd.nextInt(tok.countTokens());
                value=null;
                for(int i=0;i<x;i++)
                    value=tok.nextToken();
                return nodeFactory.textNode(value);
            } else
                throw new RuntimeException("No property:"+s);
        } else {
            s=Utils.asString(data.get("property"),null);
            if(s==null)
                throw new RuntimeException("choose or property is required for $propfile");
            String value=p.getProperty(s);
            if(value==null)
                throw new RuntimeException("No property:"+s);
            return nodeFactory.textNode(value);
        }
    }

 }
