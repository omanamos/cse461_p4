import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class DummyReceiver {
    final Lock yeahLock = new ReentrantLock();
    final Condition yeahArrived = yeahLock.newCondition();
    
    // needs to be global for multiple threads
    private Set<PendingYeah> receivedYeahs;
    
    public void receiveSays() {
    }
    
    // Called by sender object
    public void receiveYeah(PendingYeah pending) throws InterruptedException {
        yeahLock.lock();
        try {
            while(!receivedYeahs.contains(pending)) {
                yeahArrived.await();
            }
            // We received what we were looking for!
            receivedYeahs.remove(pending);
        } finally {
            yeahLock.unlock();
        }
    }
}
