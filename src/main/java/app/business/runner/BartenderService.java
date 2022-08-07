package app.business.runner;

import app.business.model.Priority;
import app.business.service.OrderActionsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import java.util.PriorityQueue;


@Component
@Slf4j
public class BartenderService implements CommandLineRunner, Ordered {

    private final OrderActionsImpl orderActions;
    int counterOrders = 0;

    public BartenderService(OrderActionsImpl orderActions) {
        this.orderActions = orderActions;
    }

    @Override
    public void run(String... args) throws Exception {
        PriorityQueue<Priority> priorityQueue = orderActions.getPriorityQueue();
        PriorityQueue<Priority> ordersOutOfCapacity = orderActions.getPriorityQueueOutOfCapacity();
        PriorityQueue<Priority> happyOurQueue = orderActions.getHappyOurQueue();
        while (true){
            log.info("priorityQueue size is {}", priorityQueue.size());
            log.info("ordersOutOfCapacity size is {}", ordersOutOfCapacity.size());
            log.info("happyOurQueue size is {}", happyOurQueue.size());
            if(priorityQueue.isEmpty()){
                log.info("What are want to drink, please?");
                synchronized (priorityQueue){
                    log.info("waiting for new orders...");
                    priorityQueue.wait();
                }
            }
            if(!happyOurQueue.isEmpty()){
                while (!happyOurQueue.isEmpty()){
                    priorityQueue.add(happyOurQueue.poll());
                    log.info("added happy hour !!!");
                }
            }
            if(!priorityQueue.isEmpty()){
                Thread.sleep(5000);
                Object priority = priorityQueue.poll();
                log.info("order with priority {} was executed, please take a drink is done {}", priority, counterOrders++);
                synchronized (priorityQueue){
                    priorityQueue.notify();
                }
                if(priorityQueue.size() < 50 &&
                        !ordersOutOfCapacity.isEmpty()){
                    priorityQueue.add(ordersOutOfCapacity.poll());
                }
            }
           log.info("priorityQueue state is {}", priorityQueue);
        }
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
