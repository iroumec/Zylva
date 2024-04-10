package com.bdd.mer.estatica;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Jerarquia implements Component, Serializable {
    int x, y, radio; // Centro del óvalo
    private final boolean exclusiva;
    private final boolean total;
    private final Entidad superTipo;
    private final List<Entidad> subTipos;
    private boolean seleccionada = false;

    public Jerarquia(boolean exclusiva, boolean total, Entidad superTipo, List<Entidad> subTipos) {
        this.exclusiva = exclusiva;
        this.total = total;
        this.superTipo = superTipo;
        this.subTipos = subTipos;
        this.x = superTipo.getX();
        this.y = superTipo.getY() + 50;
    }

    public void dibujar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Cambia la fuente del texto
        g2.setFont(new Font("Verdana", Font.BOLD, 10));

        // Selecciono la letra a utilizar
        String letra;
        if (this.isExclusiva()) {
            letra = "d";
        } else {
            letra = "o";
        }

        // Obtengo la fuente del texto y calculo su tamaño
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(letra);
        int textHeight = fm.getHeight();

        // Calcula el diámetro del círculo basado en el tamaño del texto
        int diameter = Math.max(textWidth, textHeight) + 10;
        this.radio = diameter/2;

        // Aplica suavizado a las líneas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Si la jerarquía es seleccionada, se pinta de CYAN
        if (seleccionada) {
            g2.setColor(Color.CYAN);
        }

        // Dibuja la línea al supertipo
        g2.drawLine(this.x, this.y, superTipo.getX(), superTipo.getY());
        // Si es total, dibuja dos líneas
        if (this.isTotal()) {
            g2.drawLine(this.x - 5, this.y - 5, superTipo.getX() - 5, superTipo.getY() - 5);
        }

        // Dibuja las líneas a los subtipos
        for (Entidad e : subTipos) {
            g2.drawLine(this.x, this.y, e.getX(), e.getY());
        }

        // Dibuja el círculo adaptado al tamaño del texto
        g2.drawOval(this.x - radio, this.y - radio, diameter, diameter);

        // Rellena el círculo
        g2.setColor(Color.WHITE);
        g2.fillOval(this.x - radio, this.y - radio, diameter, diameter);

        // Dibuja el texto dentro del círculo
        g2.setColor(Color.BLACK);
        g2.drawString(letra, this.x - 4, this.y + 4);
    }

    /*
    En una jerarquía total, toda instancia del supertipo debe ser instancia también de alguno
    de los subtipos.

    Una jerarquía exclusiva se nota con una doble línea del supertipo al ícono de jerarquía.
    Por otro lado, si la jerarquía es parcial, se utiliza una única línea.
     */
    public boolean isTotal() {
        return this.total;
    }

    /*
    En una jerarquía exclusiva, los ejemplares de los subtipos son conjuntos disjuntos (solo pueden
    pertenecer a un subtipo a la vez).

    Una jerarquía exclusiva se nota con la letra "d" (Disjunt), mientras que una jerarquía compartida
    se nota con la letra "o" (Overlapping).
     */
    public boolean isExclusiva() {
        return this.exclusiva;
    }

    public List<Entidad> getEntitiesList() {
        List<Entidad> l = new ArrayList<>(subTipos);
        l.add(superTipo);
        return l;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x - radio, y - radio, radio * 2, radio * 2);
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
        /* Nothing */
    }
}
