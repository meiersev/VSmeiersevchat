package ch.ethz.inf.vs.a3.message;

import java.util.Comparator;

import ch.ethz.inf.vs.a3.clock.VectorClock;
import ch.ethz.inf.vs.a3.message.Message;

/**
 * Message comparator class. Use with PriorityQueue.
 */
public class MessageComparator implements Comparator<Message> {

    @Override
    public int compare(Message lhs, Message rhs) {
        VectorClock lhsClock = lhs.getTimestamp();
        VectorClock rhsClock = rhs.getTimestamp();

        if (lhsClock.happenedBefore(rhsClock)){
            // return int <0 if lhs smaller than rhs
            return -1;
        } else if (rhsClock.happenedBefore(lhsClock)){
            // return int >0 if rhs smaller than rhs
            return 1;
        }
        // return int 0 if objects are equal
        return 0;
    }

}
