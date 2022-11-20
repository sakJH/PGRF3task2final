package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

class CPanel extends JLabel {
    Image displayImage;
    //Původní buffImage
    BufferedImage buffImageOrigin;
    //Změněný buffImage
    BufferedImage buffImageChanged;
    //To, co se ve výsledku vykreslí
    BufferedImage buffImage;

    Graphics2D graphics2D;

    CPanel(){
        setBackground(Color.black);
        loadImage();
        setSize(displayImage.getWidth(this), displayImage.getWidth(this));
        createBufferedImages();
        buffImage = buffImageOrigin;
    }

    public void loadImageFromSource(String path){
        displayImage = Toolkit.getDefaultToolkit().getImage(path);
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(displayImage, 1);
        try {
            mt.waitForAll();
        } catch (Exception e) {
            System.out.println("Exception err.");
        }
        if (displayImage.getWidth(this) == - 1) {
            System.out.println("JPG err");
            System.exit(0);
        }
        else
        {
            setSize(displayImage.getWidth(this), displayImage.getWidth(this));
            createBufferedImages();
            buffImage = buffImageOrigin;
            reset();
        }
    }

    public void loadImage(){
        displayImage = Toolkit.getDefaultToolkit().getImage("src/view/Nepal.jpg");
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(displayImage, 1);
        try {
            mt.waitForAll();
        } catch (Exception e) {
            System.out.println("Exception error");
        }
        if (displayImage.getWidth(this) == - 1) {
            System.out.println("JPG error");
            System.exit(0);
        }
    }

    public void createBufferedImages() {
        buffImageOrigin = new BufferedImage(displayImage.getWidth(this), displayImage
                .getHeight(this), BufferedImage.TYPE_INT_RGB);

        graphics2D = buffImageOrigin.createGraphics();
        graphics2D.drawImage(displayImage, 0, 0, this);

        buffImageChanged = new BufferedImage(displayImage.getWidth(this), displayImage
                .getHeight(this), BufferedImage.TYPE_INT_RGB);
    }

    //Ukládání
    public void saveImage() throws IOException {
        File file = new File("src/EditedPicture.jpg");
        ImageIO.write(buffImage, "jpg", file);
        System.out.println("Obrázek uložen");
    }

    //-------------------------------
    // FUNKCE

    public int overflowControl(int value)
    {
        if (value < 0) {value = 0;}
        if (value > 255) {value = 255;}

        return  value;
    }

    public double overflowControlDouble(double value)
    {
        if (value < 0) {value = 0;}
        if (value > 255) {value = 255;}

        return  value;
    }

    public void functionJas(int urovenJasu) {
        for ( int y = 0; y < buffImageOrigin.getHeight(); y++) {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++) {

                Color pixelColor = new Color(buffImageOrigin.getRGB(x,y), true);

                int newRed = overflowControl( pixelColor.getRed() + urovenJasu );
                int newGreen = overflowControl( pixelColor.getGreen() + urovenJasu );
                int newBlue = overflowControl( pixelColor.getBlue() + urovenJasu );

                pixelColor = new Color(newRed, newGreen, newBlue);

                buffImageChanged.setRGB(x,y, pixelColor.getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }

    public void functionKontrast(int urovenKont) {
        int contrastFactor = ( 259 * (urovenKont + 255) / (255 * (259 - urovenKont)) );

        for ( int y = 0; y < buffImageOrigin.getHeight(); y++) {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++) {

                Color pixelColor = new Color(buffImageOrigin.getRGB(x,y), true);

                int newRed = overflowControl(contrastFactor * (pixelColor.getRed() - 128) + 128 );
                int newGreen = overflowControl(contrastFactor * (pixelColor.getGreen() - 128) + 128 );
                int newBlue = overflowControl(contrastFactor * (pixelColor.getBlue() - 128) + 128 );

                pixelColor = new Color(newRed, newGreen, newBlue);

                buffImageChanged.setRGB(x,y, pixelColor.getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }

    public void functionGamma(double gamma) {
        /*
        * Gama - zdroj - https://www.dfstudios.co.uk/articles/programming/image-programming-algorithms/image-processing-algorithms-part-6-gamma-correction/
        * */

        // Gamma min = 0,01
        // Gamma max = 3

        double gammaCorrection = 1 / gamma;

        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {
                Color pixelColor = new Color(buffImageOrigin.getRGB(x,y), true);

                double newRed = overflowControlDouble(
                        Math.pow( (255 * ((double)pixelColor.getRed() / 255)), gammaCorrection)
                );
                double newGreen = overflowControlDouble(
                        Math.pow( (255 * ((double)pixelColor.getGreen() / 255)), gammaCorrection)
                );
                double newBlue = overflowControlDouble(
                        Math.pow( (255 * ((double)pixelColor.getBlue() / 255)), gammaCorrection)
                );

                newRed = Math.round(newRed);
                newGreen = Math.round(newGreen);
                newBlue = Math.round(newBlue);

                pixelColor = new Color((int)newRed, (int)newGreen, (int)newBlue);

                buffImageChanged.setRGB(x,y, pixelColor.getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }

    public void functionRed(int elected){
        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {
                int pixel = buffImage.getRGB(x, y);

                Color color = new Color(pixel, true);

                int red = elected;
                int green = color.getGreen();
                int blue = color.getBlue();

                color = new Color(red, green, blue);
                buffImageChanged.setRGB(x, y, color.getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }

    public void functionGreen(int elected){
        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {
                int pixel = buffImage.getRGB(x, y);

                Color color = new Color(pixel, true);

                int red = color.getRed();
                int green = elected;
                int blue = color.getBlue();

                color = new Color(red, green, blue);
                buffImageChanged.setRGB(x, y, color.getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }

    public void functionBlue(int elected){
        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {
                int pixel = buffImage.getRGB(x, y);

                Color color = new Color(pixel, true);

                //v1
                int red = color.getRed();
                int green = color.getGreen();
                int blue = elected;

                color = new Color(red, green, blue);
                buffImageChanged.setRGB(x, y, color.getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }

    public void functionInvertColor(){
        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {
                int pixel = buffImage.getRGB(x, y);

                Color color = new Color(pixel, true);

                int red = 255 - color.getRed();
                int green = 255 - color.getGreen();
                int blue = 255 - color.getBlue();

                color = new Color(red, green, blue);
                buffImageChanged.setRGB(x, y, color.getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }

    public void functionGreyColor(){
        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {
                int pixel = buffImage.getRGB(x, y);

                Color color = new Color(pixel, true);

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int i = (red + green + blue) / 3;

                color = new Color(i, i, i);
                buffImageChanged.setRGB(x, y, color.getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }

    public void functionMedianFilter(){
        Color[] pixel = new Color[9];

        int[] RED = new int[9];
        int[] BLUE = new int[9];
        int[] GREEN = new int[9];

        for ( int y = 1; y < buffImageOrigin.getHeight() - 1; y++)
        {
            for (int x = 1; x < buffImageOrigin.getWidth() - 1; x++)
            {
                pixel[0] = new Color(buffImageOrigin.getRGB(x - 1,y - 1));
                pixel[1] = new Color(buffImageOrigin.getRGB(x - 1, y));
                pixel[2] = new Color(buffImageOrigin.getRGB(x - 1, y + 1));
                pixel[3] = new Color(buffImageOrigin.getRGB(x,y + 1));
                pixel[4] = new Color(buffImageOrigin.getRGB(x + 1,y + 1));
                pixel[5] = new Color(buffImageOrigin.getRGB(x + 1,y));
                pixel[6] = new Color(buffImageOrigin.getRGB(x + 1,y - 1));
                pixel[7] = new Color(buffImageOrigin.getRGB(x,y - 1));
                pixel[8] = new Color(buffImageOrigin.getRGB(x, y));

                for(int k = 0; k < 9; k++){
                    RED[k]=pixel[k].getRed();
                    GREEN[k]=pixel[k].getGreen();
                    BLUE[k]=pixel[k].getBlue();
                }

                Arrays.sort(RED);
                Arrays.sort(GREEN);
                Arrays.sort(BLUE);

                buffImageChanged.setRGB(x,y,new Color(RED[4],BLUE[4],GREEN[4]).getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }

    public void functionMyFilter(){
        Random randomNumber = new Random();
        int low = - 100;
        int high = 100;
        int randRed = randomNumber.nextInt(high-low) + low;
        int randGreen = randomNumber.nextInt(high-low) + low;
        int randBlue = randomNumber.nextInt(high-low) + low;

        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {
                int pixel = buffImageOrigin.getRGB(x, y);

                Color color = new Color(pixel, true);

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();


                int colR1 = 255 - red;
                int newRed = colR1 + randRed;

                int colG1 = 255 - green;
                int newGreen = colG1 + randGreen;

                int colB1 = 255 - blue;
                int newBlue = colB1 + randBlue;

                //

                if(newRed > 255)
                { newRed = newRed - 255 + 40;}
                if(newRed < 0)
                {newRed = 0;}

                if(newGreen > 255)
                { newGreen = newGreen - 255;}
                if(newGreen < 0)
                {newGreen = 0;}

                if(newBlue > 255)
                { newBlue = newBlue - 255;}
                if(newBlue < 0)
                {newBlue = 0;}

                color = new Color(newRed, newGreen, newBlue);

                buffImageChanged.setRGB(x, y, color.getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }


    public void functionSolariseColor(){
        int solariseRed, solariseGreen, solariseBlue;
        int threshold = 128;

        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {
                int pixel = buffImage.getRGB(x, y);

                Color color = new Color(pixel, true);

                if (color.getRed() < threshold)
                {
                    solariseRed = 255 - color.getRed();
                }
                else {solariseRed = color.getRed();}

                if (color.getGreen() < threshold)
                {
                    solariseGreen = 255 - color.getGreen();
                }
                else {solariseGreen = color.getGreen();}

                if (color.getBlue() < threshold)
                {
                    solariseBlue = 255 - color.getBlue();
                }
                else {solariseBlue = color.getBlue();}

                color = new Color(solariseRed, solariseGreen, solariseBlue);

                buffImageChanged.setRGB(x, y, color.getRGB());
            }
        }
        buffImage = buffImageChanged;
        repaint();
    }


    //Ještě rozbité algoritmy

    public void functionErrorDiffusion(){
        /*
        error = actualColour - nearestColour
        --PutPixelColour(x+1, y  ) = Truncate(GetPixelColour(x+1, y  ) + 7/16 * error)
        PutPixelColour(x-1, y+1) = Truncate(GetPixelColour(x-1, y+1) + 3/16 * error)
        PutPixelColour(x  , y+1) = Truncate(GetPixelColour(x  , y+1) + 5/16 * error)
        PutPixelColour(x+1, y+1) = Truncate(GetPixelColour(x+1, y+1) + 1/16 * error)*/

        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {

            }
        }
        buffImage = buffImageChanged;
        repaint();
    }

    public void functionFindingNeadersColour(){
    /*
        minimumDistance = 255*255 + 255*255 + 255*255 + 1
        For paletteColour = 0 To lastPaletteColour
            rDiff = Red(actualColour) - Red(paletteColour)
            gDiff = Green(actualColour) - Green(paletteColour)
            bDiff = Blue(actualColour) - Blue(paletteColour)
            distance = rDiff*rDiff + gDiff*gDiff + bDiff*bDiff
            If distance < minimumDistance
                minimumDistance = distance
                nearestColour = paletteColour
            EndIf
         Next
*/
        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {
                int minimumDistance = 255*255 + 255*255 + 255*255 + 1;
                Color[] polePalet = new Color[8];
                polePalet[0] = Color.BLACK;
                polePalet[1] = Color.RED;
                polePalet[2] = Color.GREEN;
                polePalet[3] = Color.YELLOW;
                polePalet[4] = Color.BLUE;
                polePalet[5] = Color.MAGENTA;
                polePalet[6] = Color.CYAN;
                polePalet[7] = Color.WHITE;

                for (Color value : polePalet) {
                    int pixel = buffImage.getRGB(x, y);

                    Color color = new Color(pixel, true);

                    int rDiff = color.getRed() - value.getRed();
                    int gDiff = color.getGreen() - value.getGreen();
                    int bDiff = color.getBlue() - value.getBlue();

                    int distance = rDiff * rDiff + gDiff * gDiff + bDiff * bDiff;

                    if (distance < minimumDistance) {
                        minimumDistance = distance;
                        //nearestColour = paletteColour;
                    }
                }
            }
        }
        buffImage = buffImageChanged;
        repaint();

    }

    // FUNKCE
    //-------------------------------

    public void setOriginRGB(){
        for ( int y = 0; y < buffImageOrigin.getHeight(); y++)
        {
            for (int x = 0; x < buffImageOrigin.getWidth(); x++)
            {
                int pixel = buffImageOrigin.getRGB(x, y);

                Color color = new Color(pixel, true);

                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                color = new Color(red, green, blue);
                buffImageChanged.setRGB(x, y, color.getRGB());
            }
        }
        buffImage = buffImageChanged;
    }

    public void reset() {
        graphics2D.setColor(Color.black);
        graphics2D.clearRect(0, 0, buffImage.getWidth(this), buffImage.getHeight(this));
        graphics2D.drawImage(displayImage, 0, 0, this);
        setOriginRGB();
        buffImage = buffImageOrigin;
    }


    public void update(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        paintComponent(g);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;

        g2D.drawImage(buffImage, 0, 0, this);
    }
}