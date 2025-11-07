package org.easywork.blockchain.block;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.easywork.blockchain.utils.Utils;

import java.security.PublicKey;
import java.util.*;

@Data
public class Blockchain {

    private final List<Transaction> transactionPool = new ArrayList<>();
    private List<Block> chain = new ArrayList<>();
    private String blockchainAddress;

    private int port;
    private List<String> neighbors = new ArrayList<>();

    private static final int MINING_DIFFICULTY = 3;
    private static final float MINING_REWARD = 1.0f;

    public Blockchain(String blockchainAddress, int port) {
        this.blockchainAddress = blockchainAddress;
        this.port = port;
        // 创世区块
        Block genesis = new Block(0, "0", new ArrayList<>());
        chain.add(genesis);
    }

    public Block lastBlock() {
        return chain.get(chain.size() - 1);
    }

    public synchronized void createBlock(int nonce, String previousHash) {
        Block block = new Block(nonce, previousHash, new ArrayList<>(transactionPool));
        chain.add(block);
        transactionPool.clear();
        System.out.println("Created block: " + block.hash());
    }

    public boolean createTransaction(Transaction tx, PublicKey senderPublicKey) {
        boolean added = this.addTransaction(tx, senderPublicKey);
        if (added) {
            for (String neighbor : this.neighbors) {
                String endpoint = String.format("http://%s/blockchain/createTransaction", neighbor);
                String res = HttpUtil.post(endpoint, JSONUtil.toJsonStr(tx));
                System.out.println("Transaction sync:" + neighbor + ", result: " + res);
            }
            return true;
        }
        return false;
    }

    public boolean addTransaction(Transaction tx, PublicKey senderPublicKey) {
        if (tx.getSender().equals("BLOCKCHAIN")) {
            transactionPool.add(tx);
            return true;
        }

        if (!tx.isValid(senderPublicKey)) {
            System.err.println("Invalid transaction signature");
            return false;
        }

        float senderBalance = this.calculateBalance(tx.getSender());
        if (senderBalance < tx.getValue()) {
            System.err.println("Insufficient balance");
            return false;
        }

        transactionPool.add(tx);
        System.out.println("Transaction accepted");
        return true;
    }

    public boolean validProof(int nonce, String previousHash, List<Transaction> transactions, int difficulty) {
        String zeros = Strings.repeat("0", difficulty);
        Block guess = new Block(nonce, previousHash, transactions);
        return guess.hash().startsWith(zeros);
    }

    public int proofOfWork() {
        List<Transaction> transactions = new ArrayList<>(transactionPool);
        String previousHash = lastBlock().hash();
        int nonce = 0;
        while (!validProof(nonce, previousHash, transactions, MINING_DIFFICULTY)) {
            nonce++;
        }
        return nonce;
    }

    public boolean isValidChain(List<Block> chain) {
        Block previousBlock = chain.get(0);
        for (int currentIndex = 1; currentIndex < chain.size(); currentIndex++) {
            Block block = chain.get(currentIndex);
            if (!previousBlock.hash().equals(block.getPreviousHash())) {
                return false;
            }

            if (!this.validProof(block.getNonce(), block.getPreviousHash(), block.getTransactions(), MINING_DIFFICULTY)) {
                return false;
            }

            previousBlock = block;
        }
        return true;
    }

    public synchronized void mine() {
//        if (this.transactionPool.isEmpty()) {
//            return;
//        }
        // 挖矿奖励
        this.addTransaction(new Transaction("BLOCKCHAIN", blockchainAddress, MINING_REWARD, null), null);
        int nonce = this.proofOfWork();
        createBlock(nonce, lastBlock().hash());
        System.out.println("Mining complete!");

        for (String neighbor : this.neighbors) {
            String endpoint = String.format("http://%s/blockchain/consensus", neighbor);
            String res = HttpUtil.get(endpoint);
            System.out.println("Consensus sync:" + neighbor + ", result: " + res);
        }
    }

    public float calculateBalance(String address) {
        float total = 0;
        for (Block b : chain) {
            for (Transaction t : b.getTransactions()) {
                if (address.equals(t.getRecipient())) total += t.getValue();
                if (address.equals(t.getSender())) total -= t.getValue();
            }
        }
        return total;
    }

    public void printChain() {
        for (int i = 0; i < chain.size(); i++) {
            System.out.println("=========== Block " + i + " ===========");
            chain.get(i).print();
        }
    }

    public void startSyncNeighbors() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                neighbors = Utils.findNeighbors(Utils.getHost(), port, 0, 1, 5000, 5003);
                System.out.println("Neighbors: " + neighbors);
            }
        }, 0, 20_000);
    }

    public void resolveConflicts() {
        int maxLength = this.chain.size();
        List<Block> longestChain = this.chain;
        boolean chainReplaced = false;
        
        for (String neighbor : this.neighbors) {
            String endpoint = String.format("http://%s/blockchain/getBlockchain", neighbor);
            String res = HttpUtil.get(endpoint);
            Blockchain blockchain = JSONUtil.toBean(res, Blockchain.class);
            int size = blockchain.chain.size();
            
            // 如果找到更长的有效链，则替换
            if (size > maxLength && this.isValidChain(blockchain.chain)) {
                maxLength = size;
                longestChain = blockchain.chain;
                chainReplaced = true;
            }
            // 如果找到相同长度但内容不同的有效链，根据区块链共识规则选择其中一个
            // 这里我们采用“第一个遇到的有效链”策略
            else if (size == maxLength && this.isValidChain(blockchain.chain)) {
                // 检查链内容是否不同
                if (!this.chain.equals(blockchain.chain) && !chainReplaced) {
                    // 在实际的区块链实现中，这里可能会有更复杂的决策逻辑
                    // 例如比较链的总工作量或其他指标
                    // 当前实现简单地选择第一个遇到的不同有效链
                    longestChain = blockchain.chain;
                    chainReplaced = true;
                    System.out.println("Found conflicting chain of same length, replacing with neighbor's chain");
                }
            }
        }
        
        if (longestChain != null && chainReplaced) {
            this.chain = longestChain;
            System.out.println("Resolve conflicts replaced");
        } else {
            System.out.println("No conflicts to resolve or chain not replaced");
        }
    }

    public void startMining() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mine();
            }
        }, 0, 60_000);
    }

    @PostConstruct
    public void run() {
        this.startSyncNeighbors();
        this.resolveConflicts();
        this.startMining();
    }

    @Data
    public static class TransactionRequest {
        private String senderBlockchainAddress;
        private String recipientBlockchainAddress;
        private String senderPublicKey;
        private String senderPrivateKey;
        private Float value;
        private String signature;

        public boolean validate() {
            return senderBlockchainAddress != null &&
                    recipientBlockchainAddress != null &&
                    senderPublicKey != null &&
                    senderPrivateKey != null &&
                    value != null &&
                    signature != null;
        }

    }

}