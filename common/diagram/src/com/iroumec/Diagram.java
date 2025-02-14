package com.iroumec;

import com.iroumec.components.Component;
import com.iroumec.executables.GUIExecutable;
import com.iroumec.executables.Item;
import com.iroumec.userPreferences.LanguageManager;
import com.iroumec.userPreferences.Preference;
import com.iroumec.userPreferences.UserPreferences;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Diagram extends JPanel {

    private List<com.iroumec.components.Component> components = new ArrayList<>();
    private com.iroumec.components.Component draggedComponent = null;
    private Set<com.iroumec.components.Component> selectedComponents = new HashSet<>();
    private final Rectangle selectionArea;
    private int selectionAreaStartX, selectionAreaStartY;
    private boolean selectingArea;
    private JPopupMenu backgroundPopupMenu;
    private boolean antialiasing = true;

    /**
     * Mouse x and y coordinates. This is useful for when a combination of keys are pressed.
     */
    private int mouseX, mouseY;

    /**
     * Useful to save the difference between the component position and the mouse position.
     */
    private int offsetX, offsetY;

    /**
     * Constructs a {@code Diagram}.
     */
    public Diagram() {

        this.setOpaque(Boolean.TRUE);
        this.setBackground(Color.WHITE);

        selectionArea = new Rectangle(0, 0, 0, 0);

        this.backgroundPopupMenu = getBackgroundPopupMenu();

        this.setAntialiasing(UserPreferences.loadBooleanPreference(Preference.ANTIALIASING, true));

        this.initializeMouseListeners();
    }

    public abstract JPopupMenu getBackgroundPopupMenu();

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        // It draws the selection area.
        Graphics2D g2d = (Graphics2D) g;

        if (antialiasing) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        g2d.draw(selectionArea);

        for (com.iroumec.components.Component component : this.components) {
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            component.draw(g2d);
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public boolean noComponenteThere(int x, int y) {

        for (com.iroumec.components.Component component : this.components) {
            if (component.getBounds().contains((new Point(x, y)))) {
                return false;
            }
        }
        return true;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void selectComponents() {

        for (com.iroumec.components.Component component : this.components) {

            if (component.canBeSelectedBySelectionArea() && selectionArea.getBounds().contains(new Point(component.getX(), component.getY()))) {

                // If the component is inside the selection area...
                selectedComponents.add(component);
                component.setSelected(Boolean.TRUE);
            }
        }

    }

    /* -------------------------------------------------------------------------------------------------------------- */

    protected void addComponent(@NotNull com.iroumec.components.Component component) {
        int index = Collections.binarySearch(
                this.components,
                component,
                Comparator.comparing(com.iroumec.components.Component::getDrawingPriority) // Cambiado aquí
        );
        if (index < 0) {
            index = -index - 1;
        }
        this.components.add(index, component);

        this.repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    final void removeComponent(@NotNull com.iroumec.components.Component componentToRemove) {

        this.components.remove(componentToRemove);
        this.selectedComponents.clear();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /**
     * The drawing priority of the components may change dynamically. In that case, this method must be invoked to
     * sort the components.
     */
    public void sortComponents() {

        // The algorithm used is a Timsort, a combination of a Merge Sort and an Insertion Sort.
        this.components.sort(Comparator.comparing(com.iroumec.components.Component::getDrawingPriority));
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<com.iroumec.components.Component> getSelectedComponents() {
        return (new ArrayList<>(selectedComponents));
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void cleanSelectedComponents() {

        for (com.iroumec.components.Component selectedComponent : selectedComponents) {
           selectedComponent.setSelected(Boolean.FALSE);
        }

        selectedComponents.clear();

        this.repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void reset() {
        this.components = new ArrayList<>();
        this.draggedComponent = null;
        this.selectedComponents = new HashSet<>();
        this.repaint();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public int getMouseX() {
        return this.mouseX;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public int getMouseY() {
        return this.mouseY;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<com.iroumec.components.Component> getListComponents() { return new ArrayList<>(this.components); }

    public boolean existsComponent(String componentName) {

        for (com.iroumec.components.Component component : this.components) {
            if (!component.getText().isEmpty() && component.getText().equals(componentName)) {
                return true;
            }
        }

        return false;
    }

    public boolean existsComponent(com.iroumec.components.Component component) {

        return this.components.contains(component);
    }

    public void resetLanguage() {

        this.backgroundPopupMenu = this.getBackgroundPopupMenu();

        for (com.iroumec.components.Component component : this.components) {
            component.resetLanguage();
        }
    }

    public Point getCenterOfComponents(List<com.iroumec.components.Component> components) {

        if (components.isEmpty()) {
            return new Point(this.getMouseX(), this.getMouseY());
        }

        double sumX = 0, sumY = 0;

        for (com.iroumec.components.Component component : components) {

            sumX += component.getX();
            sumY += component.getY();
        }

        // Calculate the average of the X and Y coordinates
        double centerX = sumX / components.size();
        double centerY = sumY / components.size();

        // Return the center as a Point object
        return new Point((int) centerX, (int) centerY);
    }

    public void setAntialiasing(boolean antialiasing) {
        this.antialiasing = antialiasing;
        UserPreferences.savePreference(Preference.ANTIALIASING, antialiasing);
        this.repaint();
    }

    public boolean isAntialiasingActive() {
        return this.antialiasing;
    }

    /**
     * Sets focus on the JComponent.
     *
     * @param component {@code JComponent} to be focused.
     */
    public void setFocus(JComponent component) {

        component.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                component.requestFocusInWindow();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                           Mouse Interactions Methods                                           */
    /* -------------------------------------------------------------------------------------------------------------- */

    private void initializeMouseListeners() {

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMoved(e);
            }
        });
    }

    private void handleMousePressed(MouseEvent e) {

        if (noComponenteThere(e.getX(), e.getY()) && !e.isControlDown()) {
            startSelectionArea(e);
        } else {
            selectingArea = false;

            // Cuando se hace clic en el mouse, verifica si se ha seleccionado una entidad
            if (e.isControlDown()) {
                this.handleControlClick();
            } else {
                List<com.iroumec.components.Component> components = getListComponents().reversed();
                for (com.iroumec.components.Component component : components) {
                    if (component.getBounds().contains(e.getPoint())) {
                        draggedComponent = component;
                        offsetX = e.getX() - draggedComponent.getX();
                        offsetY = e.getY() - draggedComponent.getY();
                        draggedComponent.setSelected(Boolean.TRUE);
                        break;
                    }
                }
            }
            mostrarMenu(e);
        }
    }

    private void handleControlClick() {
        for (com.iroumec.components.Component component : components.reversed()) {
            if (component.getBounds().contains(new Point(Diagram.this.mouseX, Diagram.this.mouseY))) {
                selectedComponents.add(component);
                component.setSelected(Boolean.TRUE);
                break;
            }
        }
    }

    private void mostrarMenu(MouseEvent e) {

        // If right click is pressed...
        if (e.isPopupTrigger()) {
            boolean componentClicked = false;

            for (com.iroumec.components.Component component : getListComponents().reversed()) {
                if (component.getBounds().contains(e.getPoint())) {
                    cleanSelectedComponents();
                    selectedComponents.add(component);
                    component.showPopupMenu(e.getComponent(), e.getX(), e.getY());
                    componentClicked = Boolean.TRUE;
                    break;
                }
            }

            if (!componentClicked) {
                backgroundPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

    }

    private void handleMouseReleased(MouseEvent e) {
        // When the mouse is released, the selection finishes.
        if (selectionArea.width != 0 || selectionArea.height != 0) {
            selectComponents();
            selectionArea.setBounds(0, 0, 0, 0);
            repaint();
        } else {
            if (!e.isPopupTrigger()) {
                if (!e.isControlDown()) {
                    // If the mouse is released and the selection area is nonexistent.
                    cleanSelectedComponents();
                }
            }
        }

        // When the mouse is released, the component dragged is unselected and the dragging stops.
        if (draggedComponent != null) {
            draggedComponent.setSelected(Boolean.FALSE);
        }
        draggedComponent = null;
        repaint();

        mostrarMenu(e);

    }

    private void handleMouseDragged(MouseEvent e) {

        if (selectingArea) {
            updateSelectionArea(e);
        } else if (!e.isPopupTrigger() && draggedComponent != null) {
            dragComponent(e);
        }

        repaint();
    }

    private void handleMouseMoved(MouseEvent e) {
        if (Diagram.this.getBounds().contains(e.getX(), e.getY())) {
            Diagram.this.mouseX = e.getX();
            Diagram.this.mouseY = e.getY();
        }
    }

    private void startSelectionArea(MouseEvent e) {
        selectionAreaStartX = e.getX();
        selectionAreaStartY = e.getY();
        selectionArea.setBounds(selectionAreaStartX, selectionAreaStartY, 0, 0);
        selectingArea = true;
        repaint(selectionArea.getBounds());
    }

    private void updateSelectionArea(MouseEvent e) {
        selectionArea.setBounds(
                Math.min(e.getX(), selectionAreaStartX),
                Math.min(e.getY(), selectionAreaStartY),
                Math.abs(e.getX() - selectionAreaStartX),
                Math.abs(e.getY() - selectionAreaStartY)
        );
    }

    private void dragComponent(MouseEvent e) {
        // Offset is subtracted with the goal of making the animation smooth.
        draggedComponent.setX(e.getX() - offsetX);
        draggedComponent.setY(e.getY() - offsetY);
    }

    public void exportToPng() {

        // The minimal area of exportation is calculated.
        Rectangle exportArea = getMinimalExportationArea();
        if (exportArea.width <= 0 || exportArea.height <= 0) {
            JOptionPane.showMessageDialog(this, LanguageManager.getMessage("warning.noComponentsToExport"));
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
            this.paint(g);
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
            Logger logger = Logger.getLogger(String.valueOf(Calendar.DATE));
            logger.log(Level.WARNING, LanguageManager.getMessage("error.PNGExport"), e);
        }
    }

    public void saveDiagram() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(LanguageManager.getMessage("fileManager.saveDiagram.dialog"));

        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Diagram Files", "mer"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            File fileToSave = fileChooser.getSelectedFile();

            // Verificar si el archivo tiene la extensión .mer, si no, agregarla
            if (!fileToSave.getName().endsWith(".mer")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".mer");
            }

            try {

                FileOutputStream fileOut = new FileOutputStream(fileToSave);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);

                out.writeObject(this.components);

                out.close();
                fileOut.close();

                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("fileSavedSuccessfully"));

            } catch (IOException i) {

                JOptionPane.showMessageDialog(null,LanguageManager.getMessage("fileManager.saveDiagram.exception"));
            }
        }
    }

    public void loadDiagram() {

        JFileChooser fileChooser = new JFileChooser();

        // Filtro de archivo para solo mostrar archivos .mer
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Diagram Files", "mer"));

        fileChooser.setDialogTitle("Specify the file you want to load.");

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {

            File fileToLoad = fileChooser.getSelectedFile();
            FileInputStream fileIn = null;
            ObjectInputStream in = null;

            try {

                fileIn = new FileInputStream(fileToLoad);
                in = new ObjectInputStream(fileIn);

                @SuppressWarnings("unchecked")
                List<com.iroumec.components.Component> components = (List<com.iroumec.components.Component>) in.readObject();

                this.reset();

                for (com.iroumec.components.Component component : components.reversed()) {

                    this.addComponent(component);
                    component.setDrawingPanel(this);

                    // If the PopupMenu is not reset, the actions menus are not shown.
                    component.resetPopupMenu();
                }

                in.close();
                fileIn.close();

            } catch (IOException i) {

                try {

                    if (in != null) {
                        in.close();
                    }

                    if (fileIn != null) {
                        fileIn.close();
                    }
                } catch (IOException io) {

                    // Unexpected.
                }

                JOptionPane.showMessageDialog(null,"The program didn't find the specified file.");

            } catch (ClassNotFoundException c) {

                // The classes were not found.
                JOptionPane.showMessageDialog(null,"An unexpected error occurred while saving the file.");

            }
        }
    }

    /**
     * With the objective of the PNG not containing too much empty space, only the minimal area containing the
     * components is exported.
     *
     * @return The minimal rectangle enclosing the components in the drawing panel.
     */
    @SuppressWarnings("Duplicates")
    private Rectangle getMinimalExportationArea() {

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Component component : this.components) {
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

    public List<Item> getFileMenuItems() {

        List<Item> out = new ArrayList<>();

        Item exportToPNG = new Item("fileMenu.exportToPNG", false);
        exportToPNG.addActionListener(_ -> this.exportToPng());
        out.add(exportToPNG);

        Item saveDiagram = new Item("fileMenu.saveDiagram", false);
        saveDiagram.addActionListener(_ -> this.saveDiagram());
        out.add(saveDiagram);

        Item loadDiagram = new Item("fileMenu.loadDiagram", false);
        loadDiagram.addActionListener(_ -> this.loadDiagram());
        out.add(loadDiagram);

        Item changeLanguage = new Item("fileMenu.changeLanguage", true);
        changeLanguage.addActionListener(_ -> LanguageManager.changeLanguage());
        out.add(changeLanguage);

        // TODO: que cambie el texto dependiendo de si está activado o no.
        Item swapAntialiasing = new Item("fileMenu.swapAntialiasing", false);
        swapAntialiasing.addActionListener(_ -> this.setAntialiasing(!this.isAntialiasingActive()));
        out.add(swapAntialiasing);

        return out;
    }
}