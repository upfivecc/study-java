package cc.upfive.learn.lambda;

public class TestBuilderDemo {

    public <O> void test(TestBuilder<O> builder) {
        System.out.println(builder.build());
    }


    public static void main(String[] args) {
        TestBuilderDemo demo = new TestBuilderDemo();
        demo.test((() -> "hello world"));




    }
}
