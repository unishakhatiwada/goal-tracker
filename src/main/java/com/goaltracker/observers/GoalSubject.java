package com.goaltracker.observers;

import com.goaltracker.models.Goal;
import com.goaltracker.repositories.GoalRepository;

import java.util.ArrayList;
import java.util.List;

public class GoalSubject {

    private final List<GoalObserver> observers = new ArrayList<>();
    private List<Goal> goals;
    private GoalRepository goalRepository;

    public void addObserver(GoalObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GoalObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Goal goal) {
        for (GoalObserver observer : observers) {
            observer.update(goal);
        }
    }

    public void checkGoals() {
        goals = goalRepository.fetchGoalsNearDeadline();

        for (Goal goal : goals) {
            notifyObservers(goal);
        }
    }

}
