package ch.ethz.inf.vs.a3.clock;

import java.lang.Integer;
import java.lang.Override;

public class LamportClock implements  Clock{

    // Field of current Time
    private Integer lamportTime = 0;

    int getTime(){
    // returns the current clock
        return lamportTime;
    }

    void setTime(int time){
    // sets the current time to argument(time)
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

    @Override
    public boolean happenedBefore(Clock other) {
        // Check whether a clock happened before another one
        LamportClock vectorOther = (LamportClock) other;
        boolean before = true;

        if (lamportTime >= vectorOther.getTime()){
            before = false;
        }
        return before;
    }

    @Override
    public String toString(){
    // get String representation of this
        return String.valueOf(this.getTime());
    }

    @Override
    public void setClockFromString(String clock) {
        // setting clock from a string representation (if a valid one)
        try{
            lamportTime = Integer.parseInt(clock);
        } catch (Exception exp){
        }
        return;
    }
}