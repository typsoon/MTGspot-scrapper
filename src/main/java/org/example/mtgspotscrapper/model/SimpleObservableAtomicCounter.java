package org.example.mtgspotscrapper.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SimpleObservableAtomicCounter implements ObservableAtomicCounter {
    private final AtomicInteger counter = new AtomicInteger(0);
    Collection<Consumer<Integer>> incrementObservers = new ArrayList<>();
    Collection<Consumer<Integer>> decrementObservers = new ArrayList<>();

    @Override
    public void increment() {
        Integer counterState = counter.incrementAndGet();
        incrementObservers.forEach(o -> o.accept(counterState));
    }

    @Override
    public void decrement() {
        Integer counterState = counter.decrementAndGet();
        decrementObservers.forEach(o -> o.accept(counterState));
    }

    @SuppressWarnings("unused")
    @Override
    public void addIncrementObserver(Consumer<Integer> counterObserver) {
        incrementObservers.add(counterObserver);
    }

    @Override
    public void addDecrementObserver(Consumer<Integer> counterObserver) {
        decrementObservers.add(counterObserver);
    }
}
