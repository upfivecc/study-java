package cc.upfive.learn.lambda;

import java.util.function.Supplier;
import java.util.Random;

public class SupplierExample {
    public static void main(String[] args) {
        // 供给一个随机整数
        Supplier<Integer> randomSupplier = () -> new Random().nextInt(100);
        System.out.println("Random number: " + randomSupplier.get());

        // 供给一个新的空字符串
        Supplier<String> stringSupplier = String::new;
        String emptyString = stringSupplier.get();
        System.out.println("Empty string: '" + emptyString + "'");
    }
}