package cc.upfive.learn.lambda;

@FunctionalInterface
interface StringProcessor {
    String process(String input); // 单个抽象方法

    // 默认方法
    default void describe() {
        System.out.println("This is a string processor.");
    }
}

public class CustomFunctionalInterfaceExample {
    public static void main(String[] args) {
        // 使用 Lambda 实现字符串大写转换
        StringProcessor toUpperCaseProcessor = s -> s.toUpperCase();
        System.out.println(toUpperCaseProcessor.process("hello")); // 输出: HELLO

        // 使用 Lambda 实现字符串反转
        StringProcessor reverseProcessor = s -> new StringBuilder(s).reverse().toString();
        System.out.println(reverseProcessor.process("Java")); // 输出: avaJ

        toUpperCaseProcessor.describe(); // 输出: This is a string processor.
    }
}