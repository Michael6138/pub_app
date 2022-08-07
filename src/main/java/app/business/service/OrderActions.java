package app.business.service;


import app.business.model.Priority;

public interface OrderActions {

    void orderDrink(int drinkId, Priority priority) throws InterruptedException;
    void orderRecurringHappyHourDrink(int drinkId, Priority priority, int interval) throws InterruptedException;
    int getNextDrink();
}
