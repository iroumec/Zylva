package com.bdd.mer.frame.menuBar.exportation;

import com.bdd.mer.components.Component;
import com.bdd.mer.frame.DrawingPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ExportPNG {

    public static void exportToPng(DrawingPanel drawingPanel) {
        try {
            // Set the scale factor for higher resolution
            double scaleFactor = 2.0; // Adjust as needed for higher quality

            int width = (int) (drawingPanel.getWidth() * scaleFactor);
            int height = (int) (drawingPanel.getHeight() * scaleFactor);

            // Create a high-resolution image
            BufferedImage imagen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = imagen.createGraphics();

            // Set high-quality rendering hints
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            // Scale the graphics to the desired resolution
            g.scale(scaleFactor, scaleFactor);

            // Paint the panel onto the high-resolution image
            drawingPanel.paint(g);
            g.dispose();

            // Create a JFileChooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");

            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "png"));

            // Show the save file dialog
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // Write the high-resolution image to a file
                ImageIO.write(imagen, "PNG", new File(fileToSave.getAbsolutePath() + ".png"));

                JOptionPane.showMessageDialog(null, "The image was successfully saved to " + fileToSave.getAbsolutePath() + ".png.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * With the objective of the PNG not containing too much empty space, only the minimal area containing the
     * components is exported.
     *
     * @param drawingPanel The drawing panel to export.
     * @return The minimal rectangle enclosing the components in the drawing panel.
     */
    private Rectangle exportationArea(DrawingPanel drawingPanel) {

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        List<Component> components = new ArrayList<>(drawingPanel.getListComponents());

        for (Component component : components) {
            Rectangle bounds = component.getBounds();

            minX = Math.min(minX, (int) bounds.getMinX());
            minY = Math.min(minY, (int) bounds.getMinY());
            maxX = Math.max(maxX, (int) bounds.getMaxX());
            maxY = Math.max(maxY, (int) bounds.getMaxY());
        }

        if (minX == Integer.MAX_VALUE || minY == Integer.MAX_VALUE || maxX == Integer.MIN_VALUE || maxY == Integer.MIN_VALUE) {
            return new Rectangle(0, 0, 0, 0); // There are no components.
        }

        int margin = 5;

        int rectWidth = (maxX - minX) + 2 * margin;
        int rectHeight = (maxY - minY) + 2 * margin;

        return new Rectangle(minX - margin, minY - margin, rectWidth, rectHeight);
    }

    /*

    Sería idea que el área que se exporte sea, parecido al cálculo que se realiza con la agregación, únicamente el que
    incluya a las entidades.

     */

}
