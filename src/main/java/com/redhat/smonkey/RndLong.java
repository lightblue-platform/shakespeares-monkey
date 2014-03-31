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
        long x=Utils.rnd.nextLong();
        long rng=max-min+1l;
        x%=rng;
        x+=(long)min;
        return nodeFactory.numberNode(x);
    }
 }
