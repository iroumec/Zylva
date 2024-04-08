package com.bdd.mer.estatica.atributo;

import java.awt.*;
import java.io.Serializable;

public class Atributo implements Serializable {

    private String nombre;
    private boolean multivaluado;
    private boolean opcional;
    private TipoAtributo tipo;

    public Atributo(String nombre, boolean opcional, boolean multivaluado, TipoAtributo tipo) {
        this.nombre = nombre;
        this.multivaluado = multivaluado;
        this.opcional = opcional;
        this.tipo = tipo;
    }

    public void dibujar(Graphics g, int x, int y, int i) {
        // Calcula la posición del atributo
        int atributoX = x; // La posición X del atributo
        int atributoY = y + 30 + i * 16; // La posición Y del atributo


        String nombreAMostrar;
        if (tipo == TipoAtributo.MAIN) {
            nombreAMostrar = "P - " + this.nombre;
        } else if (tipo == TipoAtributo.ALTERNATIVE) {
            nombreAMostrar = "A - " + this.nombre;
        } else {
            nombreAMostrar = "C - " + this.nombre;
        }


        // Dibuja el nombre del atributo
        if (opcional) {
            if (multivaluado) {
                g.drawString("- - - ->" + nombreAMostrar, atributoX, atributoY);
            } else {
                g.drawString("- - - -" + nombreAMostrar, atributoX, atributoY);
            }
        } else {
            if (multivaluado) {
                g.drawString("------->" + nombreAMostrar, atributoX, atributoY);
            } else {
                g.drawString("-------" + nombreAMostrar, atributoX, atributoY);
            }
        }

        // Dibuja la línea de conexión
        g.drawLine(x, y, atributoX, atributoY);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(TipoAtributo tipo) {
        this.tipo = tipo;
    }

    public TipoAtributo getType() { return this.tipo; }

    public boolean isMain() { return (this.tipo == TipoAtributo.MAIN); }
}
