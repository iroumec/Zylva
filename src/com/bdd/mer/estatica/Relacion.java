package com.bdd.mer.estatica;

import com.bdd.mer.estatica.atributo.Atributo;
import com.bdd.mer.estatica.coleccion.Dupla;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Relacion implements Arrastrable, Serializable {
    private String nombre;
    private final List<Dupla<Arrastrable, Dupla<Character, Character>>> entidades;
    private int x, y, diagonalHorizontal, diagonalVertical; // Posición del centro del rombo
    private final Polygon forma;
    private boolean seleccionada = false;
    private final List<Atributo> atributos;
    private boolean formaDefinida = false;
    private boolean diagonalDefinida = false;

    public Relacion(String nombre, List<Dupla<Arrastrable, Dupla<Character, Character>>> entidades, int x, int y) {

        this.nombre = nombre;
        this.entidades = entidades;

        for (Dupla<Arrastrable, Dupla<Character, Character>> dupla : entidades) {
            ((Entidad) dupla.getPrimero()).agregarRelacion(this);
        }

        this.x = x;
        this.y = y;

        // Crea un rombo
        forma = new Polygon();

        atributos = new ArrayList<>();
    }

    public void dibujar(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        // Cambia la fuente del texto
        g2.setFont(new Font("Verdana", Font.BOLD, 12));

        // Calcula el ancho del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(nombre);
        int altoTexto = fm.getHeight();

        // Calcula la posición del texto para centrarlo en el recuadro
        int xTexto = x - anchoTexto / 2;
        int yTexto = y + altoTexto / 2;

        // Cambia el grosor del recuadro
        g2.setStroke(new BasicStroke(2));

        // Dibuja el recuadro de la entidad
        int margen = 20; // Margen alrededor del texto

        // Dibuja una línea desde el centro de la relación hasta el centro de cada entidad
        for (Dupla<Arrastrable, Dupla<Character, Character>> dupla : entidades) {
            g2.drawLine(x, y, dupla.getPrimero().getX(), dupla.getPrimero().getY());
            // Si es débil, dibuja dos líneas.
            if (((Entidad) dupla.getPrimero()).isDebil()) {
                g2.drawLine(x - 10, y - 10, dupla.getPrimero().getX() - 10, dupla.getPrimero().getY() - 10);
            }

            g2.drawString("(" + dupla.getSegundo().getPrimero() + ", " + dupla.getSegundo().getSegundo() + ")",
                    (dupla.getPrimero().getX() + this.x)/2, (dupla.getPrimero().getY() + this.y)/2);
        }

        // Le doy forma al polígono
        // La diagonal solo necesito calcularla una única vez
        if (!diagonalDefinida) {
            diagonalHorizontal = anchoTexto + 2 * margen; // Diagonal horizontal basada en el ancho del texto
            diagonalVertical = altoTexto + 2 * margen; // Diagonal vertical basada en el alto del texto
            diagonalDefinida = true;
        }

        // Define la forma del polígono y me aseguro de hacerlo una sola vez
        // Si no hago esto, el polígono titila al mover las entidades
        if (!formaDefinida) {
            forma.addPoint(x, y - diagonalVertical / 2 + 8); // Punto superior
            forma.addPoint(x + diagonalHorizontal / 2, y); // Punto derecho
            forma.addPoint(x, y + diagonalVertical / 2 - 8); // Punto inferior
            forma.addPoint(x - diagonalHorizontal / 2, y); // Punto izquierdo
            formaDefinida = true;
        }

        // Cambia el color de dibujo basándote en si la entidad está seleccionada o no
        if (seleccionada) {
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(3));
        } else {
            g2.setColor(Color.BLACK);
        }

        // Dibuja los atributos y las líneas de conexión
        for (int i = 0; i < atributos.size(); i++) {
            atributos.get(i).dibujar(g2, this.x, this.y, i);
        }

        // Dibuja el rombo
        g2.drawPolygon(forma);

        // Relleno el polígono
        g2.setColor(Color.WHITE);
        g2.fillPolygon(forma);

        g2.setColor(Color.BLACK);
        g2.drawString(nombre, xTexto, yTexto); // Dibuja el nombre de la relación
    }

    public Rectangle getBounds() {
        // Calcula las coordenadas del punto superior izquierdo del rectángulo delimitador
        int rectX = this.x - diagonalHorizontal / 2;
        int rectY = this.y - diagonalVertical / 2;

        // Crea y devuelve el rectángulo delimitador
        return new Rectangle(rectX, rectY, diagonalHorizontal, diagonalVertical);
    }

    @Override
    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }

    private void actualizarForma() {
        forma.reset();
        forma.addPoint(x, y - diagonalVertical / 2 + 10); // Punto superior
        forma.addPoint(x + diagonalHorizontal / 2, y); // Punto derecho
        forma.addPoint(x, y + diagonalVertical / 2 - 10); // Punto inferior
        forma.addPoint(x - diagonalHorizontal / 2, y); // Punto izquierdo
    }

    public void agregarAtributo(Atributo atributo) {
        this.atributos.add(atributo);
    }

    public List<Dupla<Arrastrable, Dupla<Character, Character>>> getEntidades() { return this.entidades; }

    @Override
    public void setX(int x) {
        this.x = x;
        actualizarForma();
    }

    @Override
    public void setY(int y) {
        this.y = y;
        actualizarForma();
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setText(String newText) {
        this.nombre = newText;
    }
}