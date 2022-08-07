package app.business.service;

import app.business.model.Priority;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@Getter
@Slf4j
public class OrderActionsImpl implements OrderActions{

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private Comparator<Priority> priorityComparator = (p1, p2) -> {
        int first = p1.getDrink().getDrinkIdAndPriority();
        int second = p2.getDrink().getDrinkIdAndPriority();
        if(first < 1 || first > 3){
            return -1;
        }
        if(second < 1 || second > 3) {
            return  1;
        }
        if(first == second){
            boolean result = p1.getLocalDateTime().isBefore(p2.getLocalDateTime());
            return result ? -1 : 1;
        }
        return first-second;

    };
    private PriorityQueue<Priority> priorityQueue = new PriorityQueue(50, priorityComparator);
    private PriorityQueue<Priority> priorityQueueOutOfCapacity = new PriorityQueue(priorityComparator);
    private PriorityQueue<Priority> happyOurQueue = new PriorityQueue<>(priorityComparator);

    public OrderActionsImpl(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    @Override
    public void orderDrink(int drinkId, Priority priority) {
        log.info("orderDrink start with drinkId {}, priority {}", drinkId, priority);
        synchronized (priorityQueue){
            if(priorityQueue.size() < 50){
                priorityQueue.add(priority);
                log.info("orderDrink priorityBlockingQueue size less from 50, priority {} was add to queue", priority);
                priorityQueue.notify();// if queue less capacity -> notify and continue handle main queue
            }else {
                priorityQueueOutOfCapacity.add(priority);// if capacity is 50 -> add orders to another queue for waiting
                log.info("Sorry, we need wait a little time for new orders, priority {} was add to priorityQueueOutOfCapacity for waiting...", priority);
            }
        }
    }

    @Override
    public void orderRecurringHappyHourDrink(int drinkId, Priority priority, int interval) throws InterruptedException {
        synchronized (priorityQueue) {
            if (!priorityQueue.isEmpty()) {
                if(priorityQueue.peek().getDrink().getDrinkIdAndPriority() == 3){
                    log.info("wait for high priority will be finished....");
                    happyOurQueue.add(priority);
                    priorityQueue.wait();// if in main queue exist order with high priority -> wait for it will be finish
                }
            }
            threadPoolTaskScheduler.scheduleWithFixedDelay(() -> {
                log.info("orderRecurringHappyHourDrink start Happy Hour with interval {} !!!", interval);
                    synchronized (priorityQueue){
                        priorityQueue.add(priority);//
                        priorityQueue.notify();
                    }
            }, interval);
        }
    }
    @Override
    public int getNextDrink() {
        return !priorityQueue.isEmpty() ? priorityQueue.poll().getDrink().getDrinkIdAndPriority() : -1;
    }
}
