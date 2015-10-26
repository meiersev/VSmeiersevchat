package ch.ethz.inf.vs.a3.clock;


import android.support.annotation.NonNull;

import java.lang.Integer;
import java.lang.Override;
import java.util.HashMap;
import java.util.Map;

public class VectorClock implements  Clock{

    // Map of process id to its logical time
    private Map<Integer,Integer> vectorTime = new HashMap<Integer,Integer>();

    int getTime(Integer pid){
    // returns the current clock for the given process id or -1 if pid isn't registered yet
        try {
            return vectorTime.get(pid);
        } catch (Exception exc){
            return -1;
        }
    }

    void addProcess(Integer pid, int time){
    // adds a new process and its vector clock to the current clock
        vectorTime.put(pid, time);
        return;
    }

    @Override
    public void update(Clock other) {
    // update the clock with incoming clock
        VectorClock vectorOther = (VectorClock) other;

        // loop over keys of other
        for (Integer pidI : vectorOther.vectorTime.keySet()){
            Integer thisTimeI = getTime(pidI);
            Integer otherTimeI = vectorOther.getTime(pidI);
            if (thisTimeI < otherTimeI){
                // contains case that thisTimeI == -1 or otherTimeI == -1
                vectorTime.put(pidI, otherTimeI);
            }
        }
        return;
    }

    @Override
    public void setClock(Clock other) {
    // set current clock to other clock
        VectorClock vectorOther = (VectorClock) other;
        // delete current values
        vectorTime.clear();
        // reset all values to the one of other
        for (Integer pidI : vectorOther.vectorTime.keySet()){
            vectorTime.put(pidI, vectorOther.getTime(pidI));
        }
        return;
    }

    @Override
    public void tick(Integer pid) {
    // tick the pid's clock
        Integer time = vectorTime.get(pid);
        vectorTime.put(pid, time + 1);
        return;
    }

    @Override
    public boolean happenedBefore(Clock other) {
    // Check wheter a clock happened before another one
        VectorClock vectorOther = (VectorClock) other;
        boolean before = true;

        for (Integer pidI : vectorTime.keySet()){
            if (getTime(pidI) > vectorOther.getTime(pidI)){
                before = false;
                break;
            }
        }
        return before;
    }

    @Override
    public String toString(){
    // get String representation of this
        String clockStr = "", pidStr, valStr;

        for (int pid : vectorTime.keySet()){
            // construct string repr of pid and val
            pidStr = "\"" + String.valueOf(pid) + "\"";
            valStr = String.valueOf(getTime(pid));

            clockStr = clockStr.concat(pidStr + ":" + valStr + ",");
        }
        // remove last colon
        if (clockStr != ""){
            clockStr = clockStr.substring(0,clockStr.length()-1);
        }
        // add brackets
        return "{" + clockStr + "}";
    }


    @Override
    public void setClockFromString(String clock) {
    // setting clock from a string representation
        Map<Integer,Integer> tempTime = new HashMap<Integer,Integer>();
        String[] mapArr, mapVal;
        int pid, val;

        try {
            // remove brackets
            clock = clock.replace("{","");
            clock = clock.replace("}","");
            // get individual maps of pid to values
            mapArr = clock.split(",");
        } catch (Exception ex){
            return;
        }
        for (String mapI : mapArr){
            try {
                mapVal = mapI.split(":");
                // break out of loop if mapI doesn't have a : in between
                if (mapVal.length < 2){
                    break;
                }
                mapVal[0] = mapVal[0].replace("\"","");
                pid = Integer.parseInt(mapVal[0]);
                val = Integer.parseInt(mapVal[1]);
                tempTime.put(pid,val);
            } catch (Exception ex){
                return;
            }
        }
        // only set time if correct String
        vectorTime = tempTime;
        return;
    }
}