package org.easywork.blockchain;

import java.util.Timer;
import java.util.TimerTask;

public class Miner {
    private final Blockchain blockchain;

    public Miner(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public void startMining() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                blockchain.mine();
            }
        }, 0, 20_000); // 每 20 秒挖矿一次
    }
}