package org.easywork.blockchain;

public class BlockChainServer {
    public static void main(String[] args) {
        // 创建钱包
        Wallet alice = new Wallet();
        Wallet bob = new Wallet();
        alice.print();
        bob.print();

        Blockchain blockchain = new Blockchain(alice.getBlockchainAddress());

        // Alice -> Bob 转账
        Transaction tx1 = new Transaction(alice.getBlockchainAddress(), bob.getBlockchainAddress(), 2.5f);
        tx1.signTransaction(alice);

        // 验证签名并加入交易池
        blockchain.addTransaction(tx1, alice.getPublicKey());

        // 挖矿
        blockchain.mine();
        blockchain.printChain();

        System.out.println("Alice balance: " + blockchain.calculateBalance(alice.getBlockchainAddress()));
        System.out.println("Bob balance: " + blockchain.calculateBalance(bob.getBlockchainAddress()));
    }
}