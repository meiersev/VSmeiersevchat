package ch.ethz.inf.vs.a3.clock;

import com.sun.javafx.collections.MappingChange;

import java.lang.Integer;
import java.lang.Override;
import java.util.Map;
import java.util.Vector;

public class LamportClock implements  Clock{

    // Map of process id to its logical time
    private Integer lamportTime;

    // ToDo: check correct syntax
    int getTime(){
    // returns the current clock for the given process id
        return lamportTime;
    }

    void setTime(int time){
    // sets the current time to argument
        lamportTime = time;
        return;
    }

    @Override
    public void update(Clock other) {
    // update the clock with incoming clock
        LamportClock lamportOther = (LamportClock) other;
        if (lamportTime < lamportOther.getTime()){
            lamportTime = lamportOther.getTime();
        }
        return;
    }

    @Override
    public void setClock(Clock other) {
    // set current clock to other clock
        LamportClock lamportOther = (LamportClock) other;

        // reset lamportTime to the one of other
        lamportTime = lamportOther.getTime();
        return;
    }

    @Override
    public void tick(Integer pid) {
        // tick the pid's clock
        lamportTime++;
        return;
    }

    // ToDo: not sure when to return true or use > instead
    @Override
    public boolean happenedBefore(Clock other) {
        // Check wheter a clock happened before another one
        LamportClock vectorOther = (LamportClock) other;
        boolean before = true;

        if (lamportTime >= vectorOther.getTime()){
            before = false;
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