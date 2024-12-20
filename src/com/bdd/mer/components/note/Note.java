package com.bdd.mer.components.note;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.frame.PopupMenu;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;
import java.util.List;

public class Note extends Component implements Serializable {

    int ancho = 100, alto = 50; // Ancho y alto del rectángulo

    boolean seleccionada = false;

    public Note(String text, int x, int y) {
        super(text, x, y);
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {
        return null;
    }

    public void draw(Graphics2D g2) {

        // Cambia la fuente del texto
        g2.setFont(new Font("Verdana", Font.BOLD, 10));

        // Calcula el ancho del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(text);
        int altoTexto = fm.getHeight();

        // Calcula la posición del texto para centrarlo en el recuadro
        int xTexto = x - anchoTexto / 2;
        int yTexto = y + altoTexto / 2;

        // Cambia el grosor del recuadro
        g2.setStroke(new BasicStroke(1));

        // Dibuja el recuadro de la entidad
        int margen = 10; // Margen alrededor del texto

        // Aplica suavizado a las líneas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cambia el color de dibujo basándote en si la entidad está seleccionada o no
        if (seleccionada) {
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(3));
        } else {
            g2.setColor(Color.BLACK);
        }

        int rectX = x - anchoTexto / 2 - margen;
        int rectY = y - altoTexto / 2 - margen;
        int rectAncho = anchoTexto + 2 * margen;
        int rectAlto = altoTexto + 2 * margen;

        // Dibuja el rectángulo
        g2.drawRoundRect(rectX, rectY, rectAncho, rectAlto, 10, 10);

        // Rellena el rectángulo
        g2.setColor(new Color(253,253,150));
        g2.fill(new RoundRectangle2D.Double(rectX, rectY, rectAncho, rectAlto, 10, 10));
        //g2.fillRect(rectX, rectY, rectAncho, rectAlto);

        // Restablece el color de dibujo a negro
        g2.setColor(Color.BLACK);

        // Dibuja el nombre de la entidad
        g2.drawString(text, xTexto, yTexto);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - ancho / 2, y - alto / 2, ancho, alto);
    }

    @Override
    public List<Component> getComponentsForRemoval() {
        return List.of();
    }

    @Override
    public void cleanPresence() {

    }

    @Override
    public void changeReference(Entity oldEntity, Entity newEntity) {

    }
}
