package com.johncorby.gravityguild;

import com.johncorby.gravityguild.util.Common;
import com.johncorby.gravityguild.util.Identifiable;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Run regular java tests and such with this
 */
public class Tester extends Application {
    public static void print(Object... msgs) {
        System.out.println(String.join(" ", Common.toStr(msgs)));
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        print("Testing Identifiable");
        Identifiable<String> i1 = new Identifiable<>("i1");
        Identifiable<String> i2 = new Identifiable<>("i2");
        Identifiable.dispose("i1");
        Identifiable.dispose("i1");
        i2.dispose();
        i1.dispose();
    }
}
