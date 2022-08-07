package app.business.controller;

import app.business.model.Drink;
import app.business.model.HappyHour;
import app.business.model.Priority;
import app.business.service.OrderActions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Random;

@RestController
public class OrderController {

    private final OrderActions orderActions;
    private Random random = new Random();

    public OrderController(OrderActions orderActions) {
        this.orderActions = orderActions;
    }

    @PostMapping("/send-order")
    public void sendOrder(@RequestBody Priority priority) throws InterruptedException {
        orderActions.orderDrink(priority.getDrink().getDrinkIdAndPriority(), priority);
    }

    @GetMapping("/send-hundred-orders")
    public void sendHundredOrders() throws InterruptedException {
        for (int i = 0; i < 100; i++){
            int drinkId = 1 + random.nextInt(3);
            orderActions.orderDrink(drinkId, new Priority(new Drink(3)));
        }
    }

    @PostMapping("/happy-hour")
    public void happyHour(@RequestBody HappyHour happyHour) throws InterruptedException {
        orderActions.orderRecurringHappyHourDrink(happyHour.getDrinkId(), new Priority(new Drink(happyHour.getDrinkId())), happyHour.getInterval());

    }
}
