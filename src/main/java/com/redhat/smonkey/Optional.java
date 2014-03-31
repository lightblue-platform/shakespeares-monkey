package com.redhat.smonkey;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class Optional implements Generator {

    @Override
    public String describe() {
        return 
            " { \"optional\": { \"percent\": <decimal between 0 and 1>, \"value\": {object} } }\n"+
            "   Optionally generates a field value. The field exists with the value described in 'value'\n"+
            "   with probability 'percent'. By default, 'percent' is 0.5.";
    }

    @Override
    public String getName() {
        return "$optional";
    }

    @Override
     public JsonNode generate(JsonNodeFactory nodeFactory,JsonNode data,Monkey mon) {
        double percent=Utils.asDouble(data.get("percent"), .5)*100.0;
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
