package com.bdd.mer.interfaz.popup;

import com.bdd.mer.estatica.Component;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Jerarquia;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.estatica.atributo.Atributo;
import com.bdd.mer.estatica.coleccion.Dupla;
import com.bdd.mer.interfaz.PanelDibujo;
import com.bdd.mer.interfaz.anotacion.Nota;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class deleteMenuItem extends JMenuItem {

    deleteMenuItem(String name, PopupMenu popupMenu, PanelDibujo panelDibujo) {
        setText(name);

        addActionListener(_ -> {
            Component object = popupMenu.getObject();

            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro de que desea eliminar el objeto seleccionado?");
            if (confirmacion == JOptionPane.YES_OPTION) {
                List<Entidad> paraEliminarEntidad = new ArrayList<>();
                List<Relacion> paraEliminarRelacion = new ArrayList<>();
                List<Jerarquia> paraEliminarJerarquia = new ArrayList<>();

                if (object.getClass().toString().equals("class com.bdd.mer.estatica.Entidad")) {
                    paraEliminarRelacion.addAll(((Entidad) object).getRelaciones());
                    paraEliminarJerarquia.addAll(((Entidad) object).getHeriarchiesList());
                    paraEliminarEntidad.add((Entidad) object);
                }

                if (object.getClass().toString().equals("class com.bdd.mer.estatica.Relacion")) {
                    List<Entidad> participatingEntities = new ArrayList<>();

                    assert object instanceof Relacion;

                    for (Dupla<Component, Dupla<Character, Character>> d : ((Relacion) object).getEntidades()) {
                        participatingEntities.add((Entidad) d.getPrimero());
                    }

                    // Desvinculo a las entidades de la relación
                    for (Entidad e : participatingEntities) {
                        e.removeRelation((Relacion) object);
                    }

                    panelDibujo.eliminarRelacion((Relacion) object);
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

                if (object.getClass().toString().equals("class com.bdd.mer.estatica.atributo.Atributo")) {
                    assert object instanceof Atributo;
                    Component owner = ((Atributo) object).getOwner();

                    if (owner.getClass().toString().equals("class com.bdd.mer.estatica.Entidad")) {
                        ((Entidad) owner).removeAttribute((Atributo) object);
                    } else {
                        ((Relacion) owner).removeAttribute((Atributo) object);
                    }
                }

                // Elimina los elementos
                for (Relacion r : paraEliminarRelacion) {
                    panelDibujo.eliminarRelacion(r);
                }
                for (Jerarquia j : paraEliminarJerarquia) {
                    panelDibujo.deleteHierarchy(j);
                }
                for (Entidad ent : paraEliminarEntidad) {
                    panelDibujo.eliminarEntidad(ent);
                }
            }

            panelDibujo.repaint();
        });
    }
}
