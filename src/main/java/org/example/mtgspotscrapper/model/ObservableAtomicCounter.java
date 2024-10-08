package org.example.mtgspotscrapper.model;

import java.util.function.Consumer;

public interface ObservableAtomicCounter {
    void increment();
    void decrement();
    @SuppressWarnings("unused")
    void addIncrementObserver(Consumer<Integer> counterObserver);
    void addDecrementObserver(Consumer<Integer> counterObserver);
}
