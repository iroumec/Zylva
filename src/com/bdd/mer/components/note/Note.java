package com.bdd.mer.components.note;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.PopupMenu;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Note extends Component {

    public Note(String text, int x, int y) {
        super(text, x, y);
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {
        return null;
    }

    public void draw(Graphics2D g2) {

        // Calcula el ancho del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(this.getText());
        int altoTexto = fm.getHeight();

        // Calcula la posición del texto para centrarlo en el recuadro
        int xTexto = this.getX() - anchoTexto / 2;
        int yTexto = this.getY() + altoTexto / 2;

        // Cambia el grosor del recuadro
        g2.setStroke(new BasicStroke(1));

        // Dibuja el recuadro de la entidad
        int margen = 10; // Margen alrededor del texto

        // Aplica suavizado a las líneas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cambia el color de dibujo basándote en si la entidad está seleccionada o no
        if (this.isSelected()) {
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(3));
        } else {
            g2.setColor(Color.BLACK);
        }

        int rectX = this.getX() - anchoTexto / 2 - margen;
        int rectY = this.getY() - altoTexto / 2 - margen;
        int rectAncho = anchoTexto + 2 * margen;
        int rectAlto = altoTexto + 2 * margen;

        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(rectX + 4, rectY + 4, rectAncho, rectAlto, 2, 2);

        GradientPaint gp = new GradientPaint(
                rectX, rectY, new Color(253,253,150),
                rectX, rectY + rectAlto, new Color(253, 207, 62)
        );
        g2.setPaint(gp);
        g2.fillRoundRect(rectX, rectY, rectAncho, rectAlto, 2, 2);

        g2.drawRoundRect(rectX, rectY, rectAncho, rectAlto, 2, 2);
        this.setShape(new Rectangle(rectX, rectY, rectAncho, rectAlto));

        // Name of the component.
        g2.setColor(Color.BLACK);
        g2.drawString(super.getText(), xTexto, yTexto);
    }

    @Override
    public void cleanPresence() {

    }

    @Override
    public void changeReference(Entity oldEntity, Entity newEntity) {

    }
}
