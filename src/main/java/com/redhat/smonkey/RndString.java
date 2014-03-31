package com.redhat.smonkey;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class RndString implements Generator {

    public static final String DEFAULT_CHARSET=" abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-.,";

    @Override
    public String describe() {
        return 
            " { \"$str\": { \"minlength\": 1, \"maxlength\":32,\n"+
            "               \"length\": len, \"charset\":\"String of allowed chars\" } }\n"+
            "   Generates a random string using the given charset. The default charset includes all\n"+
            "   numeric and alphanumerics, space, and '-', '.', '.'.";
    }

    @Override
    public String getName() {
        return "$str";
    }

    @Override
    public JsonNode generate(JsonNodeFactory nodeFactory,JsonNode data,Monkey mon) {
        int minlength=1;
        int maxlength=32;
        String charset=DEFAULT_CHARSET;

        minlength=Utils.asInt(data.get("minlength"),minlength);
        maxlength=Utils.asInt(data.get("maxlength"),maxlength);
        int len=Utils.asInt(data.get("length"),-1);
        charset=Utils.asString(data.get("charset"),charset);
        
        if(len<=0) {
            len=Utils.rndi(minlength,maxlength);
        }
        StringBuilder bld=new StringBuilder();
        for(int i=0;i<len;i++)
            bld.append(charset.charAt(Utils.rnd.nextInt(charset.length())));
        return nodeFactory.textNode(bld.toString());
    }

 }
