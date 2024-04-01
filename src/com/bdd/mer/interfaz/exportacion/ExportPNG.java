package com.bdd.mer.interfaz.exportacion;

import com.bdd.mer.interfaz.PanelDibujo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public final class ExportPNG {

    public static void exportToPng(PanelDibujo panelDibujo) {
        try {
            // Crea una imagen del mismo tamaño que el panel
            BufferedImage imagen = new BufferedImage(panelDibujo.getWidth(), panelDibujo.getHeight(), BufferedImage.TYPE_INT_ARGB);

            // Dibuja el panel en la imagen
            Graphics g = imagen.createGraphics();
            panelDibujo.paint(g);
            g.dispose();

            // Escribe la imagen en un archivo
            ImageIO.write(imagen, "PNG", new File(LocalDate.now().toString() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
