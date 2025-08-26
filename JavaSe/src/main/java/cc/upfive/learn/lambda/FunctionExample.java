package cc.upfive.learn.lambda;

import java.util.function.Function;

public class FunctionExample {
    public static void main(String[] args) {
        // 字符串转整数
        Function<String, Integer> stringToInt = s -> Integer.parseInt(s);
        Integer number = stringToInt.apply("123");
        System.out.println("Number: " + number); // 输出: Number: 123

        // 方法引用 - 静态方法
        Function<String, Integer> stringToIntMR = Integer::parseInt;
        System.out.println(stringToIntMR.apply("456")); // 输出: 456

        // 函数组合：先求字符串长度，再将长度乘以2
        Function<String, Integer> lengthFunc = String::length;
        Function<Integer, Integer> doubleFunc = n -> n * 2;
        Function<String, Integer> composedFunc = lengthFunc.andThen(doubleFunc);
        System.out.println(composedFunc.apply("Hello")); // 输出: 10 (5 * 2)
    }
}