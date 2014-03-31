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
