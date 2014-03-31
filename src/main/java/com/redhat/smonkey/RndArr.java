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
