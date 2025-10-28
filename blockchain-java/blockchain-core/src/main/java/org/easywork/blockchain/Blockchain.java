package org.easywork.blockchain;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
@Data
public class Blockchain {

    private final List<Transaction> transactionPool = new ArrayList<>();
    private final List<Block> chain = new ArrayList<>();
    private final String blockchainAddress;

    private static final int MINING_DIFFICULTY = 3;
    private static final float MINING_REWARD = 1.0f;

    public Blockchain(String blockchainAddress) {
        this.blockchainAddress = blockchainAddress;
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
            System.err.println("💣 Insufficient balance");
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

    public void mine() {
        // 挖矿奖励
        addTransaction(new Transaction("BLOCKCHAIN", blockchainAddress, MINING_REWARD),null);
        int nonce = proofOfWork();
        createBlock(nonce, lastBlock().hash());
        System.out.println("⛏️ Mining complete!");
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

}