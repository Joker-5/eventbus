package com.john.doe.bus;

import java.util.concurrent.Executor;

/**
 * Created by JOHN_DOE on 2023/5/23.
 */
public class AsyncEventBus extends EventBus {
    public AsyncEventBus(Executor executor) {
        super(executor);
    }
}
