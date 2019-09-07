package com.chen1144.agent;

import java.io.IOException;

public class IOThread extends Thread {
    public IOThread(IORunnable runnable){
        super(()->{
            try {
                runnable.run();
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        });
    }
}
