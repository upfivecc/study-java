
JDK 8 引入的函数式编程特性，让 Java 代码更简洁、灵活和易读。下面通过一些核心概念和代码示例来帮助你理解。


函数式编程的核心是函数式接口（Functional Interface），指只有一个抽象方法的接口（但可以有默认方法或静态方法）。JDK提供了java.util.function包，包含了许多内置的函数式接口。

以下是几个最常用的函数式接口：


✍️ 怎么写 Lambda 表达式
Lambda 表达式的基本语法是 (参数列表) -> { 方法体 }，但为了简洁，有多种简化写法：

```java
// 1. 标准写法
Function<String, Integer> func1 = (String s) -> { return s.length(); };

// 2. 参数类型可省略（类型推断）
Function<String, Integer> func2 = (s) -> { return s.length(); };

// 3. 只有一个参数，可省略括号
Function<String, Integer> func3 = s -> { return s.length(); };

// 4. 方法体只有一条语句，可省略大括号和return（如果有返回值）
Function<String, Integer> func4 = s -> s.length();

```

