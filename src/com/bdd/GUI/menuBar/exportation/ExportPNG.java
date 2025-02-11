package com.bdd.GUI.menuBar.exportation;

import com.bdd.GUI.Component;
import com.bdd.GUI.Diagram;
import com.bdd.GUI.userPreferences.LanguageManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExportPNG {

    private static final Logger logger = Logger.getLogger(ExportPNG.class.getName());

    private ExportPNG() {
        throw new AssertionError(); // Security oriented.
    }

    public static void exportToPng(Diagram diagram) {

        // The minimal area of exportation is calculated.
        Rectangle exportArea = getMinimalExportationArea(diagram);
        if (exportArea.width <= 0 || exportArea.height <= 0) {
            JOptionPane.showMessageDialog(diagram, LanguageManager.getMessage("warning.noComponentsToExport"));
            return;
        }

        // The scale factor is set for higher resolution.
        double scaleFactor = 2.0;

        int width = (int) (exportArea.width * scaleFactor);
        int height = (int) (exportArea.height * scaleFactor);

        try {

            // Create a high-resolution image.
            BufferedImage imagen = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = imagen.createGraphics();

            // Set high-quality rendering hints.
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            // Scale the graphics to the desired resolution.
            g.scale(scaleFactor, scaleFactor);

            // A translation is applied to centre the content in the image.
            g.translate(-exportArea.x, -exportArea.y);

            // The panel is painted onto the high-resolution image.
            diagram.paint(g);
            g.dispose();

            // A JFileChooser is created.
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(LanguageManager.getMessage("input.PNGExport"));

            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "png"));

            // The save file dialog is shown.
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                // The high-resolution image is written to a file.
                ImageIO.write(imagen, "PNG", new File(fileToSave.getAbsolutePath() + ".png"));

                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("warning.imageSaved") + " " + fileToSave.getAbsolutePath() + ".png.");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, LanguageManager.getMessage("error.PNGExport"), e);
        }
    }

    /**
     * With the objective of the PNG not containing too much empty space, only the minimal area containing the
     * components is exported.
     *
     * @param diagram The drawing panel to export.
     * @return The minimal rectangle enclosing the components in the drawing panel.
     */
    @SuppressWarnings("Duplicates")
    private static Rectangle getMinimalExportationArea(Diagram diagram) {

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        List<Component> components = new ArrayList<>(diagram.getListComponents());

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

}
