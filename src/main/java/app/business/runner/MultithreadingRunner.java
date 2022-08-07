package app.business.runner;

import app.business.model.Drink;
import app.business.model.Priority;
import app.business.service.OrderActions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
@Slf4j
public class MultithreadingRunner implements CommandLineRunner, Ordered {

    private final OrderActions orderActions;
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private Random random = new Random();

    public MultithreadingRunner(OrderActions orderActions, ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.orderActions = orderActions;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
    }

    @Override
    public void run(String... args) throws Exception {
        for(int i = 0; i < 10; i++){
            Thread.sleep(1000);
            new Thread(() -> {
                int drinkId = 1 + random.nextInt(3);
                try {
//                    orderActions.orderDrink(drinkId, new Priority(new Drink(5)));
                    orderActions.orderDrink(drinkId, new Priority(new Drink(3)));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    @Override
    public int getOrder() {
        return 1;
    }
}
