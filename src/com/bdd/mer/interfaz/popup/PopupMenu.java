package com.bdd.mer.interfaz.popup;

import com.bdd.mer.estatica.Arrastrable;
import com.bdd.mer.interfaz.PanelDibujo;

import javax.swing.*;

public class PopupMenu extends JPopupMenu {

    private Arrastrable objeto;
    private PanelDibujo panelDibujo;

    public PopupMenu(PanelDibujo panelDibujo) {
        this(panelDibujo, false);
    }

    public PopupMenu(PanelDibujo panelDibujo, boolean attribute) {
        this(panelDibujo, attribute, false);
    }

    public PopupMenu(PanelDibujo panelDibujo, boolean attribute, boolean rename) {
        this(panelDibujo, attribute, rename, false);
    }

    public PopupMenu(PanelDibujo panelDibujo, boolean attribute, boolean rename, boolean delete) {
        this.panelDibujo = panelDibujo;
        if (attribute) {
            add(new addAtributeMenuItem("Agregar atributo", this, panelDibujo));
        }
        if (rename) {
            add(new renameMenuItem("Renombrar", this, panelDibujo));
        }
        if (delete) {
            add(new deleteMenuItem("Eliminar", this, panelDibujo));
        }
    }

    public void setObject(Arrastrable object) {
        this.objeto = object;
    }

    Arrastrable getObject() {
        return objeto;
    }
}
