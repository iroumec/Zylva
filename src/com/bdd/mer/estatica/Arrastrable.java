package com.bdd.mer.estatica;

import com.bdd.mer.estatica.atributo.Atributo;

import java.awt.*;

public interface Arrastrable {

    public Rectangle getBounds();

    public void setSeleccionada(boolean seleccionada);

    public void setX(int x);

    public void setY(int y);

    public void agregarAtributo(Atributo atributo);

    public int getX();

    public int getY();
}
