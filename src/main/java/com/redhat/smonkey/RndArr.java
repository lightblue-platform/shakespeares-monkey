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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class RndArr implements Generator {

    @Override
    public String describe() {
        return 
            " { \"$array\": { \"minlength\": 1, \"maxlength\":32,\n"+
            "                 \"length\": len, \n"+
            "                 \"element\": {object}\n"+
            "   Generates an array with random size. If 'length' is given, generates\n"+
            "   an array with that fixed size. Then, for every element, evaluates the \n"+
            "   contents of 'element'.";
    }

    @Override
    public String getName() {
        return "$array";
    }

    @Override
     public JsonNode generate(JsonNodeFactory nodeFactory,JsonNode data,Monkey mon) {
        int minlength=1;
        int maxlength=32;
        int length=-1;

        minlength=Utils.asInt(data.get("minlength"),minlength);
        maxlength=Utils.asInt(data.get("maxlength"),maxlength);
        length=Utils.asInt(data.get("length"),length);
        JsonNode el=data.get("element");
        if(el==null)
            throw new RuntimeException("element is required");

        if(length<=0) {
            length=Utils.rndi(minlength,maxlength);
        }
        ArrayNode node=nodeFactory.arrayNode();
        for(int i=0;i<length;i++) {
            node.add(mon.generateNode(el));
        }
        return node;
    }

 }
