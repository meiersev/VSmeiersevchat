package ch.ethz.inf.vs.a3.message;

import java.util.Comparator;
import ch.ethz.inf.vs.a3.message.Message;

/**
 * Message comparator class. Use with PriorityQueue.
 */
public class MessageComparator implements Comparator<Message> {

    @Override
    public int compare(Message lhs, Message rhs) {
        // Write your code here
        lhs.getTimestamp().happenedBefore(rhs.getTimestamp());
        return 0;
    }

}
