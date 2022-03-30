package com.vicious.viciouslibkit.util;

import java.util.ArrayList;
import java.util.List;

public class VersionRange {
    public List<Integer> lowest;
    public List<Integer> highest;
    public VersionRange(List<Integer> lowest, List<Integer> highest){
        this.lowest=lowest;
        this.highest=highest;
    }
    public boolean isWithin(String ver){
        return isWithin(parseVer(ver));
    }
    public boolean isWithin(List<Integer> ver){
        for (int i = 0; i < ver.size(); i++) {
            int actual = ver.get(i);
            if(lowest != null){
                if(i < lowest.size()) {
                    Integer expected = lowest.get(i);
                    if(expected != null && actual < expected){
                        return false;
                    }
                }
            }
            if(highest != null){
                if(i < highest.size()) {
                    Integer expected = highest.get(i);
                    if(expected != null && actual > expected){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public static List<Integer> parseVer(String in){
        List<Integer> vrs = new ArrayList<>();
        StringBuilder val = new StringBuilder();
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if(c == '.'){
                if(val.toString().contains("*")){
                    vrs.add(null);
                }
                else vrs.add(Integer.parseInt(val.toString()));
                val = new StringBuilder();
            }
            else if(i == in.length()-1){
                val.append(c);
                if(val.toString().contains("*")){
                    vrs.add(null);
                }
                else vrs.add(Integer.parseInt(val.toString()));
            }
            else{
                val.append(c);
            }
        }
        return vrs;
    }
    public static VersionRange fromString(String in){
        int idx = in.indexOf('-');
        if(idx == -1){
            return new VersionRange(parseVer(in),null);
        }
        else{
            return new VersionRange(parseVer(in.substring(0,idx)),parseVer(in.substring(idx+1)));
        }
    }
}
