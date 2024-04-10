package com.bdd.mer.estatica.atributo;

import com.bdd.mer.estatica.Component;
import com.bdd.mer.estatica.Entidad;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Atributo implements Serializable, Component {

    private String text;
    private Component owner;
    private final boolean multivalued;
    private final boolean opcional;
    private TipoAtributo tipo;
    private final List<Atributo> components = new ArrayList<>();
    // The following attributes are saved for the method "getBounds()";
    private int x, y, i;

    public Atributo(Component owner, String text, boolean opcional, boolean multivalued, TipoAtributo tipo) {
        this.text = text;
        this.multivalued = multivalued;
        this.opcional = opcional;
        this.tipo = tipo;
        this.owner = owner;
    }

    public void dibujar(Graphics g, int x, int y, int i, boolean compound) {
        this.x = x;
        this.y = y;
        this.i = i;

        // Calcula la posición del atributo
        // La posición en X es la misma que el parámetro
        int atributoY = y + 30 + i * 16; // La posición Y del atributo

        // Cambia la fuente del texto (es necesario usar una que pueda mostrar los caracteres UNICODE)
        g.setFont(new Font("Arial Unicode MS", Font.BOLD, 10));

        String nombreAMostrar;
        if (tipo == TipoAtributo.MAIN) {
            nombreAMostrar = "● " + this.text;
        } else if (tipo == TipoAtributo.ALTERNATIVE) {
            nombreAMostrar = "◐ " + this.text;
        } else {
            nombreAMostrar = "○ " + this.text;
        }

        // Dibuja el nombre del atributo
        if (opcional) {
            if (multivalued) {
                g.drawString("- - - >" + nombreAMostrar, x, atributoY);
            } else {
                g.drawString("- - - -" + nombreAMostrar, x, atributoY);
            }
        } else {
            if (multivalued) {
                g.drawString("------>" + nombreAMostrar, x, atributoY);
            } else {
                g.drawString("-------" + nombreAMostrar, x, atributoY);
            }
        }

        // Dibujo las componentes si el atributo es compuesto
        for (int j = 0; j < components.size(); j++) {
            (components.get(j)).dibujar(g, this.x + 26, this.y + 15 + this.i * 16, j, true);
        }

        // Dibuja la línea de conexión
        if (compound) {
            g.drawLine(x, y+14, x, atributoY);
        } else {
            g.drawLine(x, y, x, atributoY);
        }

        // Restablezco la fuente
        g.setFont(new Font("Verdana", Font.BOLD, 10));
    }

    public boolean isCompound() {
        return (!components.isEmpty());
    }

    public int getNumberOfComponents() {
        return components.size();
    }

    public void setTipo(TipoAtributo tipo) {
        this.tipo = tipo;
    }

    public void addComponent(Atributo component) { this.components.add(component); }

    public boolean isMain() { return (this.tipo == TipoAtributo.MAIN); }

    public Component getOwner() {return this.owner; }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y + 30 + i * 16 - 16, x, 16);
    }

    @Override
    public void setSeleccionada(boolean seleccionada) {

    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

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
        this.text = newText;
    }
}
