package com.bdd.mer.estatica;

import com.bdd.mer.estatica.atributo.Atributo;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entidad implements Component, Serializable {
    String nombre;
    int x, y; // Posición del centro del rectángulo
    int ancho = 100, alto = 50; // Ancho y alto del rectángulo
    boolean seleccionada = false;
    private final List<Atributo> atributos;
    private final List<Relacion> relaciones;
    private final List<Jerarquia> hierarchies;
    private final List<MacroEntity> macroEntities;
    private boolean entidadDebil = false;
    private boolean superTipo;
    private boolean subTipo;

    /* -------------------------------------------------------------------------------------------------------------- */

    public Entidad(String nombre, int x, int y) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;

        atributos = new ArrayList<>();
        relaciones = new ArrayList<>();
        hierarchies = new ArrayList<>();
        macroEntities = new ArrayList<>();
    }

    /* -------------------------------------------------------------------------------------------------------------- */

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

        // Aplica suavizado a las líneas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Cambia el color de dibujo basándote si la entidad está seleccionada o no
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

        // Variable para la colocación de atributos compuestos
        int espaciosExtras = 0;
        // Dibuja los atributos y las líneas de conexión
        for (int i = 0; i < atributos.size(); i++) {
            atributos.get(i).dibujar(g2, this.x, this.y, i + espaciosExtras, false);
            espaciosExtras += atributos.get(i).getNumberOfComponents();
        }

        // Dibuja el rectángulo
        g2.drawRect(rectX, rectY, rectAncho, rectAlto);

        // Dibuja un rectángulo exterior si la entidad es débil
        if (entidadDebil) {
            // Rellena el rectángulo
            g2.setColor(new Color(215, 239, 249));
            g2.fillRect(rectX - 5, rectY - 5, rectAncho + 10, rectAlto + 10);
            // Dibuja el rectángulo
            g2.setColor(Color.BLACK);
            g2.drawRect(rectX - 5, rectY - 5, rectAncho + 10, rectAlto + 10);
        }

        // Rellena el rectángulo
        g2.setColor(Color.WHITE);
        g2.fillRect(rectX, rectY, rectAncho, rectAlto);

        // Restablece el color de dibujo a negro
        g2.setColor(Color.BLACK);

        // Dibuja el nombre de la entidad
        g2.drawString(nombre, xTexto, yTexto);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public Rectangle getBounds() {
        return new Rectangle(x - ancho / 2, y - alto / 2, ancho, alto);
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void agregarAtributo(Atributo atributo) { this.atributos.add(atributo); }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void removeAttribute(Atributo attribute) {this.atributos.remove(attribute); }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Atributo> getAttributes() { return this.atributos; }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void agregarRelacion(Relacion relacion) { this.relaciones.add(relacion); }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void removeRelation(Relacion relation) { relaciones.remove(relation); }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Relacion> getRelaciones() { return this.relaciones; }

    /* -------------------------------------------------------------------------------------------------------------- */

    public List<Jerarquia> getHeriarchiesList() { return this.hierarchies; }

    /* -------------------------------------------------------------------------------------------------------------- */

    public int getX() {
        return x;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void setX(int x) {
        this.x = x;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public int getY() {
        return y;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    public void setText(String newText) {
        this.nombre = newText;
    }

    /* -------------------------------------------------------------------------------------------------------------- */

    public void setY(int y) {
        this.y = y;
    }

    public String getNombre() {
        return nombre;
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

    public void addHierarchy(Jerarquia hierarchy) {
        hierarchies.add(hierarchy);
    }

    public void removeHierarchy(Jerarquia hierarchy) {
        hierarchies.remove(hierarchy);
    }

    public Atributo hasMainAttribute() {
        for (Atributo a : atributos) {
            if (a.isMain()) {
                return a;
            }
        }

        return null;
    }
}
