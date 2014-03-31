package com.redhat.smonkey;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class RndInt implements Generator {

    @Override
    public String describe() {
        return 
            " { \"$int\": { \"min\": minValue, \"max\": maxValue } }\n"+
            "    Generate a random int between min/max";
    }

    @Override
    public String getName() {
        return "$int";
    }

    @Override
    public JsonNode generate(JsonNodeFactory nodeFactory,JsonNode data,Monkey mon) {
        int min=Utils.asInt(data.get("min"),Integer.MIN_VALUE);
        int max=Utils.asInt(data.get("max"),Integer.MAX_VALUE);
        long x=Utils.rnd.nextLong();
        long rng=(long)max-(long)min+1l;
        x%=rng;
        x+=(long)min;
        return nodeFactory.numberNode(x);
    }
 }
