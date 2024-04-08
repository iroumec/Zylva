package com.bdd.mer.interfaz.anotacion;

import com.bdd.mer.estatica.Arrastrable;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.Serializable;

public class Nota implements Arrastrable, Serializable {
    private String text;

    int ancho = 100, alto = 50; // Ancho y alto del rectángulo
    int x, y; // Centro de la nota

    public Nota(String text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    boolean seleccionada = false;

    public void dibujar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

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
    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setText(String newText) {
        this.text = newText;
    }

}
