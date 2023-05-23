package com.john.doe.bus;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * Created by JOHN_DOE on 2023/5/23.
 */
public class ObserverRegistry {
    private ConcurrentMap<Class<?>, CopyOnWriteArraySet<ObserverAction>> registry = new ConcurrentHashMap<>();

    public void register(Object observer) {
        for (Map.Entry<Class<?>, Collection<ObserverAction>> entry : findAllObserverActions(observer).entrySet()) {
            Class<?> eventType = entry.getKey();
            Collection<ObserverAction> eventActions = entry.getValue();
            CopyOnWriteArraySet<ObserverAction> registeredEventActions = registry.get(eventType);

            if (registeredEventActions == null) {
                registry.putIfAbsent(eventType, new CopyOnWriteArraySet<>());
                registeredEventActions = registry.get(eventType);
            }
            registeredEventActions.addAll(eventActions);
        }
    }

    public List<ObserverAction> getMatchedObserverActions(Object event) {
        Class<?> postedEventType = event.getClass();

        return registry.keySet()
                .stream()
                .filter(postedEventType::isAssignableFrom)
                .map(k -> registry.get(k))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Map<Class<?>, Collection<ObserverAction>> findAllObserverActions(Object observer) {
        Map<Class<?>, Collection<ObserverAction>> observerActions = new HashMap<>();
        for (Method method : getAnnotationMethods(observer.getClass())) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> eventType = parameterTypes[0];

            observerActions.putIfAbsent(eventType, new ArrayList<>());
            observerActions.get(eventType).add(new ObserverAction(observer, method));
        }

        return observerActions;
    }

    private List<Method> getAnnotationMethods(Class<?> clazz) {
        return Arrays
                .stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Subscribe.class))
                .collect(Collectors.toList());
    }
}
