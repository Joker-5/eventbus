package com.john.doe.bus;

import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executor;

/**
 * Created by JOHN_DOE on 2023/5/23.
 */
public class EventBus {
    private Executor executor;
    private ObserverRegistry registry;

    public EventBus() {
        this(MoreExecutors.directExecutor());
    }

    protected EventBus(Executor executor) {
        this.executor = executor;
        this.registry = new ObserverRegistry();
    }

    public void register(Object object) {
        registry.register(object);
    }

    public void post(Object event) {
        for (ObserverAction observerAction : registry.getMatchedObserverActions(event)) {
            executor.execute(() -> observerAction.execute(event));
        }
    }
}
