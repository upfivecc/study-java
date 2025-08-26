package cc.upfive.learn.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 找出所有偶数，乘以2，然后收集到新列表
        List<Integer> result = numbers.stream() // 创建流
                .filter(n -> n % 2 == 0) // 中间操作：过滤 (Predicate)
                .map(n -> n * 2) // 中间操作：映射 (Function)
                .collect(Collectors.toList()); // 终止操作：收集结果

        System.out.println("Processed numbers: " + result); // 输出: [4, 8, 12, 16, 20]

        // 并行流处理提高大数据集性能
        long evenCount = numbers.parallelStream()
                                .filter(n -> n % 2 == 0)
                                .count();
        System.out.println("Count of even numbers: " + evenCount); // 输出: 5
    }
}