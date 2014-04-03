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

public class Choose implements Generator {

    @Override
    public String describe() {
        return 
            " { \"$choose\": [ <values> ] } \n"+
            "   Randomly chooses a value from the array.";
    }

    @Override
    public String getName() {
        return "$choose";
    }

    @Override
    public JsonNode generate(JsonNodeFactory nodeFactory,JsonNode data,Monkey mon) {
        if(data instanceof ArrayNode) {
            JsonNode node=((ArrayNode)data).get( Utils.rnd.nextInt( ((ArrayNode)data).size() ) );
            return node.deepCopy();
        } else
            throw new RuntimeException("$choose needs an array");
    }

 }
