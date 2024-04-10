package com.bdd.mer.estatica;

import java.awt.*;

public interface Component {

    Rectangle getBounds();

    void setSeleccionada(boolean seleccionada);

    void setX(int x);

    void setY(int y);

    int getX();

    int getY();

    void setText(String newText);
}
