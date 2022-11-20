import lwjglutils.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

public class FileLoader {

    public static OGLTexture2D loadIMG(){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("./res/img"));
        chooser.setFileFilter(new FileNameExtensionFilter("Texture", "jpg","png"));
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showOpenDialog(null);

        if(result== JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath().replace("\\", "/");
            String[] name = path.split("/");
            try {
                return  new OGLTexture2D("img/"+name[name.length-1]);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        else if(result == JFileChooser.CANCEL_OPTION) {System.out.println("IMG error");}
        return  null;
    }
}
