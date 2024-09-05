package com.goaltracker.observers;

import com.goaltracker.models.Goal;

public class NotificationObserver implements GoalObserver {

    @Override
    public void update(Goal goal) {
        sendNotification(goal);
    }

    private void sendNotification(Goal goal) {
    }

}
