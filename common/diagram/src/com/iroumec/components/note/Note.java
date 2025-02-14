package com.iroumec.components.note;

import com.iroumec.Diagram;
import com.iroumec.components.Component;
import com.iroumec.userPreferences.LanguageManager;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Note extends Component {

    /**
     * Constructor of the class.
     * @param text  The text that will appear in the note.
     * @param x The x coordinate in the panel.
     * @param y The y coordinate in the panel.
     */
    private Note(String text, int x, int y) {
        super(text, x, y);
        setDrawingPriority(8);
    }

    /**
     * The lines in the text are divided according to the max width.
     *
     * @param g2       The graphic context.
     * @param text     The complete text.
     * @return A list of strings adjusted according the width.
     */
    private List<String> wrapText(Graphics2D g2, String text) {
        List<String> lines = new ArrayList<>();
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine + (currentLine.isEmpty() ? "" : " ") + word;
            int maxWidth = 150;
            if (fm.stringWidth(testLine) > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            } else {
                currentLine.append(currentLine.isEmpty() ? "" : " ").append(word);
            }
        }
        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    public static void addNote(Diagram diagram) {

        String text = JOptionPane.showInputDialog(
                diagram,
                null,
                LanguageManager.getMessage("input.text"),
                JOptionPane.QUESTION_MESSAGE
        );

        if (text != null) {
            Component.addComponent(new Note(text, diagram.getMouseX(), diagram.getMouseY()), diagram);
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem actionItem = new JMenuItem("action.changeText");
        actionItem.addActionListener(_ -> this.rename());
        popupMenu.add(actionItem);

        actionItem = new JMenuItem("action.setForDelete");
        actionItem.addActionListener(_ -> this.deleteWithConfirmation());
        popupMenu.add(actionItem);

        return popupMenu;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void draw(Graphics2D g2) {

        FontMetrics fm = g2.getFontMetrics();
        List<String> lines = wrapText(g2, this.getText());
        int lineHeight = fm.getHeight();

        int margin = 10;
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, fm.stringWidth(line)); // The max width of the lines is calculated.
        }
        int rectAncho = maxWidth + 2 * margin;
        int rectAlto = lines.size() * lineHeight + 2 * margin;

        int rectX = this.getX() - rectAncho / 2;
        int rectY = this.getY() - rectAlto / 4; // Due to the baseline of the text.

        g2.setColor(new Color(244, 219, 131));
        g2.fillRoundRect(rectX, rectY, rectAncho, rectAlto, 2, 2);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));

        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        g2.drawRoundRect(rectX, rectY, rectAncho, rectAlto, 2, 2);
        this.setShape(new Rectangle(rectX, rectY, rectAncho, rectAlto));

        // The text is drawn centred.
        g2.setColor(Color.BLACK);
        int yTexto = rectY + margin + lineHeight;
        for (String line : lines) {
            int xTexto = rectX + (rectAncho - fm.stringWidth(line)) / 2;
            g2.drawString(line, xTexto, yTexto);
            yTexto += lineHeight;
        }
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    /*
    Maybe a distinction between independent and dependent components would be okay.
     */

    @Override
    protected void cleanReferencesTo(Component component) {

    }

    @Override
    protected void notifyRemovingOf(Component component) {

    }

    @Override
    protected boolean canBeDeleted() {
        return true;
    }
}
