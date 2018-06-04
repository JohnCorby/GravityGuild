package com.johncorby.gravityguild;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Run regular java tests and such with this
 */
public class Tester extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ITest test = new Test();
        test.test();
    }
}

interface ITest {
    public void test();
}

class Test implements ITest {
    @Override
    public void test() {
        System.out.println("Hello World!");
    }
}
