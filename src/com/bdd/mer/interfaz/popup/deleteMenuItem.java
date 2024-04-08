package com.bdd.mer.interfaz.popup;

import com.bdd.mer.estatica.Arrastrable;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Jerarquia;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.interfaz.PanelDibujo;
import com.bdd.mer.interfaz.anotacion.Nota;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class deleteMenuItem extends JMenuItem {

    private PopupMenu popupMenu;

    deleteMenuItem(String name, PopupMenu popupMenu, PanelDibujo panelDibujo) {
        setText(name);
        this.popupMenu = popupMenu;

        addActionListener(_ -> {
            Arrastrable object = popupMenu.getObject();

            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro de que desea eliminar el objeto seleccionado?");
            if (confirmacion == JOptionPane.YES_OPTION) {
                List<Entidad> paraEliminarEntidad = new ArrayList<>();
                List<Relacion> paraEliminarRelacion = new ArrayList<>();
                List<Jerarquia> paraEliminarJerarquia = new ArrayList<>();
                List<Nota> paraEliminarNota = new ArrayList<>();

                if (object.getClass().toString().equals("class com.bdd.mer.estatica.Entidad")) {
                    paraEliminarRelacion.addAll(((Entidad) object).getRelaciones());
                    paraEliminarEntidad.add((Entidad) object);
                }

                if (object.getClass().toString().equals("class com.bdd.mer.interfaz.anotacion.Nota")) {
                    assert object instanceof Nota;
                    panelDibujo.deleteNote((Nota) object);
                }

                if (object.getClass().toString().equals("class com.bdd.mer.estatica.Jerarquia")) {
                    assert object instanceof Jerarquia;

                    List<Entidad> entitiesParticipating = new ArrayList<>(((Jerarquia) object).getEntitiesList());

                    // Desvinculo cada entidad con la jerarquía a eliminar
                    for (Entidad e : entitiesParticipating) {
                        e.removeHierarchy((Jerarquia) object);
                    }

                    panelDibujo.deleteHierarchy((Jerarquia) object);
                }

                // Elimina los elementos
                for (Relacion r : paraEliminarRelacion) {
                    panelDibujo.eliminarRelacion(r);
                }
                for (Entidad ent : paraEliminarEntidad) {
                    panelDibujo.eliminarEntidad(ent);
                }
            }

            panelDibujo.repaint();
        });
    }
}
