package com.bdd.mer.interfaz;

import javax.swing.*;
import java.awt.*;

public class LoadingScreen extends JWindow {

    public LoadingScreen() {
        // Configurar el tamaño y la posición de la ventana de carga
        setSize(224, 224);
        setLocationRelativeTo(null);

        // Cargar la imagen original
        ImageIcon originalIcon = new ImageIcon("src/com/bdd/mer/interfaz/images/Zilva.jpeg");
        Image originalImage = originalIcon.getImage();
        // Escalar la imagen al tamaño deseado
        Image scaledImage = originalImage.getScaledInstance(224, 224, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel loadingLabel = new JLabel(scaledIcon, SwingConstants.CENTER);
        add(loadingLabel);

        // Configurar el temporizador para cerrar la ventana después de 3 segundos
        // Cerrar la ventana en el EDT
        Timer timer = new Timer(3000, _ -> {
            SwingUtilities.invokeLater(this::dispose); // Cerrar la ventana en el EDT
        });
        timer.setRepeats(false);
        timer.start();

        // Hacer visible la ventana de carga
        setVisible(true);
    }
}
