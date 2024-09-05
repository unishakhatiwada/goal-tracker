package com.goaltracker.observers;

import com.goaltracker.models.Goal;

public interface GoalObserver {
    void update(Goal goal);
}
