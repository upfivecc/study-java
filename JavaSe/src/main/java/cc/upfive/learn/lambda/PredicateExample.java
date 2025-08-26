package cc.upfive.learn.lambda;

import java.util.function.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PredicateExample {
    public static void main(String[] args) {
        List<String> languages = Arrays.asList("Java", "Python", "C++", "JavaScript", "Go");

        // 判断字符串是否以 "J" 开头
        Predicate<String> startsWithJ = s -> s.startsWith("J");
        List<String> jLanguages = languages.stream()
                                        .filter(startsWithJ)
                                        .collect(Collectors.toList());
        System.out.println("Languages start with J: " + jLanguages); // 输出: [Java, JavaScript]

        // 组合断言：以 "J" 开头 并且 长度大于 4
        Predicate<String> lengthGreater4 = s -> s.length() > 4;
        Predicate<String> combinedPredicate = startsWithJ.and(lengthGreater4);
        List<String> filteredLanguages = languages.stream()
                                                .filter(combinedPredicate)
                                                .collect(Collectors.toList());
        System.out.println("Filtered languages: " + filteredLanguages); // 输出: [JavaScript]
    }
}