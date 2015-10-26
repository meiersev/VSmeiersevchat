package ch.ethz.inf.vs.a3.clock;

import com.sun.javafx.collections.MappingChange;

import java.lang.Integer;
import java.lang.Override;
import java.util.Map;
import java.util.Vector;

public class VectorClock implements  Clock{

    // Map of process id to its logical time
    private Map<Integer,Integer> vectorTime;

    int getTime(Integer pid){
    // returns the current clock for the given process id
        return vectorTime.get(pid);
    }

    void addProcess(Integer pid, int time){
    // adds a new process and its vector clock to the current clock
        vectorTime.put(pid, time);
        return;
    }

    @Override
    public void update(Clock other) {
    // update the clock with incoming clock
        VectorClock VectorOther = (VectorClock) other;
        for (Integer pidI : vectorTime.keySet()){
            Integer thisTimeI = getTime(pidI);
            Integer otherTimeI = VectorOther.getTime(pidI);
            if (thisTimeI < otherTimeI){
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

    // ToDo: not sure when to return true or use > instead
    @Override
    public boolean happenedBefore(Clock other) {
    // Check wheter a clock happened before another one
        VectorClock vectorOther = (VectorClock) other;
        boolean before = true;

        for (Integer pidI : vectorTime.keySet()){
            if (getTime(pidI) >= vectorOther.getTime(pidI)){
                before = false;
                break;
            }
        }

        return before;
    }

    // ToDo: implement
    @Override
    public void setClockFromString(String clock) {
    // setting clock from a string representation
        return;
    }

}