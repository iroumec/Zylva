package com.bdd.mer.components.note;

import com.bdd.mer.components.Component;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.actions.Action;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Note extends Component {

    public Note(String text, int x, int y, DrawingPanel drawingPanel) {
        super(text, x, y, drawingPanel);
    }

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.getActionManager().getPopupMenu(
                this,
                Action.CHANGE_TEXT,
                Action.DELETE
        );

    }

    public void draw(Graphics2D g2) {

        // Calcula el ancho del texto y divide en líneas si es necesario
        FontMetrics fm = g2.getFontMetrics();
        List<String> lines = wrapText(g2, this.getText()); // Ajusta el ancho máximo según lo que necesites
        int lineHeight = fm.getHeight();

        // Calcula el tamaño del rectángulo según el contenido
        int margen = 10; // Margen alrededor del texto
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, fm.stringWidth(line)); // Calcula el ancho máximo de las líneas
        }
        int rectAncho = maxWidth + 2 * margen;
        int rectAlto = lines.size() * lineHeight + 2 * margen;

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

        // Dibuja las líneas del texto centradas
        g2.setColor(Color.BLACK);
        int yTexto = rectY + margen + lineHeight;
        for (String line : lines) {
            int xTexto = rectX + (rectAncho - fm.stringWidth(line)) / 2;
            g2.drawString(line, xTexto, yTexto);
            yTexto += lineHeight;
        }
    }

    @Override
    public void cleanPresence() {

    }

    @Override
    public void changeReference(Component oldComponent, Component newComponent) {

    }

    /**
     * Divide el texto en líneas según el ancho máximo disponible.
     *
     * @param g2       El contexto gráfico.
     * @param text     El texto completo.
     * @return Una lista de líneas ajustadas al ancho.
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
}
