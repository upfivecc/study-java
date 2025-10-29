package org.easywork.blockchain;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

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
        boolean isAdded = addTransaction(tx, senderPublicKey);
        if (isAdded) {
            for (String neighbor : this.neighbors) {
                String endpoint = String.format("http://%s/blockchain/addTransaction", neighbor);
                TransactionRequest txRequest = new TransactionRequest();
                txRequest.setSenderBlockchainAddress(tx.getSender());
                txRequest.setRecipientBlockchainAddress(tx.getRecipient());
                txRequest.setValue(tx.getValue());
                if (null != senderPublicKey) {
                    txRequest.setSenderPublicKey(Base64.getEncoder().encodeToString(senderPublicKey.getEncoded()));
                }
                String res = HttpUtil.post(endpoint, JSONUtil.toJsonStr(txRequest));
                System.out.println("Transaction sync:" + neighbor + ", result: " + res);
            }
        }
        return true;
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

        float senderBalance = calculateBalance(tx.getSender());
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
        createTransaction(new Transaction("BLOCKCHAIN", blockchainAddress, MINING_REWARD), null);
        int nonce = proofOfWork();
        createBlock(nonce, lastBlock().hash());
        System.out.println("⛏️ Mining complete!");

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
        for (String neighbor : this.neighbors) {
            String endpoint = String.format("http://%s/chain", neighbor);
            String res = HttpUtil.get(endpoint);
            Blockchain blockchain = JSONUtil.toBean(res, Blockchain.class);
            int size = blockchain.chain.size();
            if (size > maxLength && this.isValidChain(blockchain.chain)) {
                maxLength = size;
                this.chain = blockchain.chain;
            }
        }
    }

    public void startMining() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mine();
            }
        }, 0, 20_000);
    }

    @PostConstruct
    public void run() {
        this.startSyncNeighbors();
        this.resolveConflicts();
        this.startMining();
    }

}