package view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame{
    CPanel displayPanel;
    JSlider redSlider;

    public MainFrame() throws IOException {
        setTitle("Color Editor - Jan Sakač");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() throws IOException {

        Container container = getContentPane();
        displayPanel = new CPanel();
        container.add(displayPanel);

        //Vytvoření prostředí
        JToolBar slat = new JToolBar();
        slat.setLayout(new BoxLayout(slat, BoxLayout.X_AXIS));
        slat.setFloatable(false);

        JPanel centerRight = new JPanel();
        centerRight.setLayout(new BoxLayout(centerRight, BoxLayout.Y_AXIS));

        //Vytvoření komponent
        //Pro slat
        JButton openButton = new JButton("Otevřít");
        JButton saveButton = new JButton("Uložit");

        //Pro centerRight - komponenty na změnu funkcí
        Label bright = new Label("Jas");
        Label brightValue = new Label("Původní");
        JSlider sliderBright = new JSlider();
        sliderBright.setValue(0);
        sliderBright.setMinimum(-128);
        sliderBright.setMaximum(128);
        sliderBright.addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        int sliderBrightValue = sliderBright.getValue();
                        displayPanel.functionJas(sliderBrightValue);
                        brightValue.setText(String.valueOf(sliderBrightValue));
                    }
                }
        );

        Label contrast = new Label("Kontrast");
        Label contrastValue = new Label("Původní");
        JSlider sliderContrast = new JSlider();
        sliderContrast.setValue(0);
        sliderContrast.setMaximum(255);
        sliderContrast.setMinimum(0);
        sliderContrast.addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        int sliderContrastValue = sliderContrast.getValue();
                        displayPanel.functionKontrast(sliderContrastValue);
                        contrastValue.setText(String.valueOf(sliderContrastValue));
                        //repaint();
                    }
                }
        );

        //
        Label gamma = new Label("Gama (od 0,1 - 3)");

        JFormattedTextField gammaTextInput = new JFormattedTextField();
        gammaTextInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Runnable format = new Runnable() {
                    @Override
                    public void run() {
                        String text = gammaTextInput.getText();
                        if(!text.matches("([a-d0-7]{1})*(,\\d{0,2})?")){
                            gammaTextInput.setText(text.substring(0,text.length()-1));
                        }

                        if (Double.parseDouble(text.replace(",", ".")) >= 3)
                        {
                            gammaTextInput.setText(text.substring(0,text.length()-1));
                        }
                    }
                };
                SwingUtilities.invokeLater(format);
            }
            @Override public void removeUpdate(DocumentEvent e) {}
            @Override public void changedUpdate(DocumentEvent e) {}
        });

        gammaTextInput.setPreferredSize(new Dimension(25,10));

        JButton gammaButton = new JButton();
        gammaButton.setText("Změnit gamu");

        gammaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String gamaComma = gammaTextInput.getText();
                String gammaVal = gamaComma.replace(",", ".");
                double levelgamma = Double.parseDouble(gammaVal);
                System.out.println("Gama " + levelgamma);
                displayPanel.functionGamma(levelgamma);
            }
        });

        Label rgb = new Label("RGB");
        Label redLabel = new Label("Red");
        Label redLabelValue = new Label("Původní");
        redSlider = new JSlider();
        redSlider.setMinimum(1);
        redSlider.setMaximum(255);
        redSlider.setValue(128);

        Label greenLabel = new Label("Green");
        Label greenLabelValue = new Label("Původní");
        JSlider greenSlider = new JSlider();
        greenSlider.setMinimum(1);
        greenSlider.setMaximum(255);
        greenSlider.setValue(128);

        Label blueLabel = new Label("Blue");
        Label blueLabelValue = new Label("Původní");
        JSlider blueSlider = new JSlider();
        blueSlider.setMinimum(1);
        blueSlider.setMaximum(255);
        blueSlider.setValue(128);

        redSlider.addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        int levelRed = redSlider.getValue();
                        displayPanel.functionRed(levelRed);
                        redLabelValue.setText(String.valueOf(levelRed));
                    }
                }
        );
        greenSlider.addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        int levelGreen = greenSlider.getValue();
                        displayPanel.functionGreen(levelGreen);
                        greenLabelValue.setText(String.valueOf(levelGreen));
                    }
                }
        );
        blueSlider.addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        int levelBlue = blueSlider.getValue();
                        displayPanel.functionBlue(levelBlue);
                        blueLabelValue.setText(String.valueOf(levelBlue));
                    }
                }
        );


        JButton invertColorButtom = new JButton();
        invertColorButtom.setText("Invertovat barvy");
        invertColorButtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPanel.functionInvertColor();
            }
        });


        JButton greyColorButtom = new JButton();
        greyColorButtom.setText("Černobílý filtr");
        greyColorButtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPanel.functionGreyColor();
            }
        });

        JButton solariseColorButtom = new JButton();
        solariseColorButtom.setText("Solarizace");
        solariseColorButtom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPanel.functionSolariseColor();
            }
        });

        JButton medianButton = new JButton();
        medianButton.setText("Medián");
        medianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPanel.functionMedianFilter();
            }
        });

        JButton myFilterButton = new JButton();
        myFilterButton.setText("Náhodný filtr");
        myFilterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPanel.functionMyFilter();
            }
        });

        JButton restartButton = new JButton("Restartovat nastaveni");

        //--------------------------------------------
        //--------------------------------------------

        //Přidání komponent do prostředí
        //Cen. Left
        slat.add(openButton);
        slat.add(saveButton);
        //Cen. Right
        centerRight.add(bright);
        centerRight.add(brightValue);
        centerRight.add(sliderBright);
        centerRight.add(contrast);
        centerRight.add(contrastValue);
        centerRight.add(sliderContrast);
        centerRight.add(gamma);
        centerRight.add(gammaTextInput);
        centerRight.add(gammaButton);

        centerRight.add(rgb);
        centerRight.add(redLabel);
        centerRight.add(redLabelValue);
        centerRight.add(redSlider);
        centerRight.add(greenLabel);
        centerRight.add(greenLabelValue);
        centerRight.add(greenSlider);
        centerRight.add(blueLabel);
        centerRight.add(blueLabelValue);
        centerRight.add(blueSlider);
        centerRight.add(invertColorButtom);
        centerRight.add(solariseColorButtom);
        centerRight.add(greyColorButtom);
        centerRight.add(medianButton);
        centerRight.add(myFilterButton);

        centerRight.add(restartButton);

        //ZOBRAZENÍ do kontejneru
        add(slat,BorderLayout.NORTH);
        //add(centerLeft,BorderLayout.CENTER);
        //add(container, BorderLayout.CENTER);
        add(centerRight,BorderLayout.EAST);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setSize(displayPanel.getWidth(), displayPanel.getHeight() + 10);

        //--------------------------------------------
        //Načtení nového IMG přes tlačítko
        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //Najdi si požadovaný obrázek
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.setFileFilter(new FileNameExtensionFilter("JPG", "jpg"));

                int result = fileChooser.showOpenDialog(MainFrame.this);

                if (result == JFileChooser.APPROVE_OPTION)
                {
                    File file = fileChooser.getSelectedFile();
                    String path = file.getPath();
                    displayPanel.loadImageFromSource(path);
                    System.out.println("Obrázek načten");
                }
                else
                {
                    System.out.println("Chyba načítání");
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    displayPanel.saveImage();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        restartButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sliderBright.setValue(0);
                        sliderContrast.setValue(128);
                        //restart RGB
                        redSlider.setValue(128);
                        greenSlider.setValue(128);
                        blueSlider.setValue(128);
                        displayPanel.reset(); //reset i pro RGB
                        displayPanel.repaint();
                    }
                }
        );
    }

}



























