package com.bdd.mer.frame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class LoadingScreen extends JWindow {

    int width = 428;
    int height = 428;

    public LoadingScreen() {
        // Configurar el tamaño y la posición de la ventana de carga
        setSize(width, height); // Tamaño de la ventana según la imagen generada
        setLocationRelativeTo(null);

        try {
            // Cargar la imagen en alta calidad (PNG o WEBP)
            BufferedImage originalImage = ImageIO.read(new File("src/com/bdd/mer/multimedia/Zylva.png")); // Cambia la ruta
            // Escalar la imagen con alta calidad si es necesario
            BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(originalImage, 0, 0, width, height, null); // Escalar a 1024x1024
            g2d.dispose();

            // Crear un JLabel con la imagen escalada
            JLabel loadingLabel = new JLabel(new ImageIcon(scaledImage), SwingConstants.CENTER);
            add(loadingLabel);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configurar el temporizador para cerrar la ventana después de 3 segundos
        Timer timer = new Timer(3000, _ -> SwingUtilities.invokeLater(this::dispose));
        timer.setRepeats(false);
        timer.start();

        // Hacer visible la ventana de carga
        setVisible(true);
    }
}

