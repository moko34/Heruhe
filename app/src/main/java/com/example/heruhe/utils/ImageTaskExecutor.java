package com.example.heruhe.utils;

import java.util.concurrent.Executor;

public class ImageTaskExecutor implements Executor {
    @Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
