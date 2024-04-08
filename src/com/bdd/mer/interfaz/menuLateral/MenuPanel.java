package com.bdd.mer.interfaz.menuLateral;

import com.bdd.mer.interfaz.MarcoPrincipal;
import com.bdd.mer.interfaz.PanelDibujo;
import com.bdd.mer.interfaz.barraDeMenu.MenuBar;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {

    MenuPanel(Dimension dimension) {
        // Defino la distribución del panel
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // Defino el tamaño del panel
        setPreferredSize(dimension);
    }
}
