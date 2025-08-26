package cc.upfive.learn.lambda;

import java.util.function.Consumer;

public class ConsumerExample {
    public static void main(String[] args) {
        // 使用 Lambda 打印字符串
        Consumer<String> printConsumer = s -> System.out.println(s);
        printConsumer.accept("Hello, Consumer!"); // 输出: Hello, Consumer!

        // 使用方法引用打印字符串 (更简洁)
        Consumer<String> printConsumerMR = System.out::println;
        printConsumerMR.accept("Hello, Method Reference!");

        // 组合消费：先打印，再打印长度
        Consumer<String> first = s -> System.out.print("String: " + s + ", Length: ");
        Consumer<String> second = s -> System.out.println(s.length());
        Consumer<String> combined = first.andThen(second);
        combined.accept("Java"); // 输出: String: Java, Length: 4
    }
}