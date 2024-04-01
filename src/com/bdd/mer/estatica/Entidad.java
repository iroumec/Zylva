package com.bdd.mer.estatica;

import com.bdd.mer.estatica.atributo.Atributo;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entidad implements Arrastrable, Serializable {
    String nombre;
    int x, y; // Posición del centro del rectángulo
    int ancho = 100, alto = 50; // Ancho y alto del rectángulo
    boolean seleccionada = false;
    private List<Atributo> atributos;
    private List<Relacion> relaciones;
    private boolean entidadDebil = false;
    private boolean tieneIDPrincipal = false;
    private boolean superTipo;
    private boolean subTipo;

    public Entidad(String nombre, int x, int y) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;

        atributos = new ArrayList<>();
        relaciones = new ArrayList<>();
    }

    public void dibujar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Cambia la fuente del texto
        g2.setFont(new Font("Verdana", Font.BOLD, 10));

        // Calcula el ancho del texto
        FontMetrics fm = g2.getFontMetrics();
        int anchoTexto = fm.stringWidth(nombre);
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

        // Dibuja los atributos y las líneas de conexión
        for (int i = 0; i < atributos.size(); i++) {
            atributos.get(i).dibujar(g2, this.x, this.y, i);
        }

        // Dibuja el rectángulo
        g2.drawRect(rectX, rectY, rectAncho, rectAlto);

        // Dibuja un rectángulo exterior si la entidad es débil
        if (entidadDebil) {
            g2.drawRect(rectX - 5, rectY - 5, rectAncho + 10, rectAlto + 10);
            // Rellena el rectángulo
            g2.setColor(Color.BLACK);
            g2.fillRect(rectX - 5, rectY - 5, rectAncho + 10, rectAlto + 10);
        }

        // Rellena el rectángulo
        g2.setColor(Color.WHITE);
        g2.fillRect(rectX, rectY, rectAncho, rectAlto);

        // Restablece el color de dibujo a negro
        g2.setColor(Color.BLACK);

        // Dibuja el nombre de la entidad
        g2.drawString(nombre, xTexto, yTexto);
    }

    public Rectangle getBounds() {
        return new Rectangle(x - ancho / 2, y - alto / 2, ancho, alto);
    }

    public void agregarAtributo(Atributo atributo) {
        this.atributos.add(atributo);
    }

    public void agregarRelacion(Relacion relacion) { this.relaciones.add(relacion); }

    public List<Relacion> getRelaciones() { return this.relaciones; }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    public int getAlto() {
        return alto;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    public void setSeleccionada(boolean seleccionada) {
        this.seleccionada = seleccionada;
    }

    public void setEntidadDebil(boolean entidadDebil) {
        this.entidadDebil = entidadDebil;
    }

    public boolean isDebil() {
        return entidadDebil;
    }

    public List<Atributo> getAtributos() {
        return atributos;
    }

    public void setTieneIDPrincipal(boolean tieneIDPrincipal) {
        this.tieneIDPrincipal = tieneIDPrincipal;
    }

    public boolean tieneIDPrincipal() {
        return tieneIDPrincipal;
    }

    public boolean isSuperTipo() {
        return superTipo;
    }

    public void setSuperTipo(boolean superTipo) {
        this.superTipo = superTipo;
    }

    public boolean isSubTipo() {
        return subTipo;
    }

    public void setSubTipo(boolean subTipo) {
        this.subTipo = subTipo;
    }
}
