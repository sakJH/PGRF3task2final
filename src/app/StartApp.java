package app;

import view.MainFrame;

import javax.swing.*;
import java.io.IOException;

public class StartApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new MainFrame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}
