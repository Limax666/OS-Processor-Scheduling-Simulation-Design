package kernel;

import java.util.LinkedList;
import java.util.Queue;

public class DJFK {
    Queue<Process>[] queues = new Queue[3];
    public DJFK(){
        for (int i = 0; i < 3; i++) {
            queues[i] = new LinkedList<>();
        }
    }
}
