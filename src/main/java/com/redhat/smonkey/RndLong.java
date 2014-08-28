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

public class RndLong implements Generator {

    @Override
    public String describe() {
        return 
            " { \"$long\": { \"min\": minValue, \"max\": maxValue } }\n"+
            "    Generate a random long between min/max";
    }

    @Override
    public String getName() {
        return "$long";
    }

    @Override
    public JsonNode generate(JsonNodeFactory nodeFactory,JsonNode data,Monkey mon) {
        long min=Utils.asLong(data.get("min"),Long.MIN_VALUE);
        long max=Utils.asLong(data.get("max"),Long.MAX_VALUE);
        long x=Utils.rndl(min, max);
        long rng=max-min+1l;
        x%=rng;
        x+=(long)min;
        return nodeFactory.numberNode(x);
    }
 }
