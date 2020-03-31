package com.ennie.community;

import java.util.concurrent.BlockingQueue;

public class BlockingQueueTest {

    public static void main(String[] args) {

    }
}

class Producer implements Runnable{

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue){
        this.queue = queue;
    }

    @Override
    public void run() {

    }
}