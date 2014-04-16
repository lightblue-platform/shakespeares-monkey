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

public class Optional implements Generator {

    @Override
    public String describe() {
        return 
            " { \"optional\": { \"percent\": <decimal between 0 and 100>, \"value\": {object} } }\n"+
            "   Optionally generates a field value. The field exists with the value described in 'value'\n"+
            "   with probability 'percent'. By default, 'percent' is 0.5.";
    }

    @Override
    public String getName() {
        return "$optional";
    }

    @Override
     public JsonNode generate(JsonNodeFactory nodeFactory,JsonNode data,Monkey mon) {
        double percent=Utils.asDouble(data.get("percent"), 50.0);
        if(Utils.rndBool((int)percent)) {
            JsonNode value=data.get("value");
            if(value==null)
                throw new RuntimeException("value expected");
            return mon.generateNode(value);
        } else {
            return Monkey.FIELD_DOES_NOT_EXIST;
        }
    }

 }
