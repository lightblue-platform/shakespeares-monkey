package com.redhat.smonkey;

import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;

public final class Utils {

    public static final Random rnd=new Random();

    public static Integer asInt(JsonNode value,Integer def) {
        return value==null?def:value.asInt();
    }

    public static Long asLong(JsonNode value,Long def) {
        return value==null?def:value.asLong();
    }

    public static String asString(JsonNode value,String def) {
        return value==null?def:value.asText();
    }

    public static double asDouble(JsonNode value,Double def) {
        return value==null?def:value.asDouble();
    }

    public static int rndi(int min,int max) {
        int n=max-min+1;
        int x=rnd.nextInt(n);
        return x+min;
    }
        
    public static long rndl(long min,long max) {
        long x=rnd.nextLong();
        if(x<0)
            x=-x;
        long n=max-min+1;
        return (x%n)+min;
    }
        

    /**
     * Returns true with 'percent' percent probability
     */
    public static boolean rndBool(int percent) {
        return rnd.nextInt(100)<percent;
    }

    private Utils() {}
}
