package cc.upfive.learn.lambda;

/**
 * @author: fiveupup
 * @version: 1.0.0
 * @date: 2025/8/27 21:54
 */
public final class MyBuilder {
    private String name;

    public MyBuilder name(String name) {
        return MyBuilder.this;
    }
}
