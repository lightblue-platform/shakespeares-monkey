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

import java.util.Date;
import java.util.Calendar;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class RndDate implements Generator {

    public static final String DEFAULT_FORMAT="yyyy/MM/dd";

    @Override
    public String describe() {
        return 
            " { \"$date\": { \"format\": SimpleDateFormat, \"min\": minDate, \"max\": maxDate, \n"+
            "                                              \"base\": startDate, \"fwd\": Forward, \"bck\": Backward }\n"+
            "    Generates random date. If format is not given, default is "+DEFAULT_FORMAT+"\n"+
            "    Either min/max, or fwd/bck should be given. If none is given, current date/time will be returned. \n"+
            "          min/max: A random date is returned between the given bounds\n"+
            "          fwd/bck: Starting from base (if not specified, it is now), select a date that goes forwad or\n"+
            "                   backwards from the given base date. The expression is of the form <number><unit> where \n"+
            "                   unit is sec, hr, min, d, m, y.";
    }

    @Override
    public String getName() {
        return "$date";
    }

    @Override
    public JsonNode generate(JsonNodeFactory nodeFactory,JsonNode data,Monkey mon) {
        String format=Utils.asString(data.get("format"),DEFAULT_FORMAT);
        SimpleDateFormat fmt=new SimpleDateFormat(format);

        try {
            String min=Utils.asString(data.get("min"),null);
            Date minDate=min==null?null:fmt.parse(min);
            String max=Utils.asString(data.get("max"),null);
            Date maxDate=max==null?null:fmt.parse(max);
            if(minDate==null&&maxDate==null) {
                String base=Utils.asString(data.get("base"),fmt.format(new Date()));
                Date baseDate=fmt.parse(base);
                
                String fwd=Utils.asString(data.get("fwd"),null);
                String bck=Utils.asString(data.get("bck"),null);
                if(fwd==null&&bck==null)
                    return nodeFactory.textNode(base);
                
                if(fwd!=null)
                    maxDate=applyDiff(baseDate,fwd,false);
                if(bck!=null)
                    minDate=applyDiff(baseDate,bck,true);
            }
            return nodeFactory.textNode(fmt.format(genDate(minDate,maxDate)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Date genDate(Date minDate,Date maxDate) {
        long min=minDate==null?System.currentTimeMillis():minDate.getTime();
        long max=maxDate==null?System.currentTimeMillis():maxDate.getTime();
        return new Date(Utils.rndl(min,max));
    }

    private Date applyDiff(Date base,String diff,boolean neg) {
        Calendar cal=Calendar.getInstance();
        cal.setTime(base);

        StringBuilder bld=new StringBuilder();
        int value=0;
        String unit;
        int state=0;
        boolean done=false;
        int calfield;
        for(int i=0;i<diff.length()&&!done;i++) {
            char c=diff.charAt(i);
            switch(state) {
            case 0:
                if(Character.isSpace(c))
                    ;
                else if(Character.isDigit(c)) {
                    bld.append(c);
                    state=1;
                } else
                    throw new RuntimeException("Bad expression:"+diff);
                break;

            case 1:
                if(Character.isDigit(c)) {
                    bld.append(c);
                } else if(Character.isSpace(c)) {
                    state=2;
                    value=Integer.valueOf(bld.toString());
                    bld=new StringBuilder();
                } else {
                    state=3;
                    value=Integer.valueOf(bld.toString());
                    bld=new StringBuilder();
                    bld.append(c);
                }
                break;

            case 2:
                if(!Character.isSpace(c)) {
                    state=3;
                    bld.append(c);
                }
                break;
            case 3:
                if(!Character.isSpace(c)) {
                    bld.append(c);
                } else
                    done=true;
                break;
            }
        }
        if(state==2||state==3) {
            unit=bld.toString();
            if("sec".equals(unit)) {
                calfield=Calendar.SECOND;
            } else if("min".equals(unit)) {
                calfield=Calendar.MINUTE;
            } else if("hr".equals(unit)) {
                calfield=Calendar.HOUR_OF_DAY;
            } else if("d".equals(unit)) {
                calfield=Calendar.DAY_OF_MONTH;
            } else if("m".equals(unit)) {
                calfield=Calendar.MONTH;
            } else if("y".equals(unit)) {
                calfield=Calendar.YEAR;
            } else
                throw new RuntimeException("Bad expression:"+diff);
        } else
            throw new RuntimeException("Bad expression:"+diff);
        cal.add(calfield,neg?-value:value);
        return cal.getTime(); 
    }

 }
