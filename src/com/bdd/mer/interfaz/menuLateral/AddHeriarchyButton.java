package com.bdd.mer.interfaz.menuLateral;

import com.bdd.mer.estatica.Arrastrable;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Jerarquia;
import com.bdd.mer.estatica.coleccion.Dupla;
import com.bdd.mer.interfaz.MarcoPrincipal;
import com.bdd.mer.interfaz.PanelDibujo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AddHeriarchyButton extends JButton {

    private PanelDibujo panelDibujo;

    public AddHeriarchyButton(String name, Dimension dimension, String fuente, MarcoPrincipal mainFrame, PanelDibujo panelDibujo) {
        setText(name);
        setMaximumSize(dimension);
        setBackground(Color.WHITE);
        setOpaque(Boolean.TRUE);
        setFont(new Font(fuente, Font.BOLD, 12));

        this.panelDibujo = panelDibujo;

        // Defino la función del botón
        addActionListener(_ -> {
            // Solo procede si se ha seleccionado al menos tres entidades
            if (panelDibujo.getComponentesSeleccionadas().size() >= 3 && panelDibujo.participaSoloEntidades()) {
                Entidad supertipo = seleccionarSupertipo();
                List<Entidad> subtipos = obtenerListaSubtipos(supertipo);
                if (rolesJerarquiaOcupados(supertipo, subtipos)) {
                    ocuparRoles(supertipo, subtipos);
                    Dupla<Boolean, Boolean> tipo = seleccionarTipoJerarquia();

                    Jerarquia newHierarchy = new Jerarquia("",tipo.getPrimero(), tipo.getSegundo(), supertipo, subtipos);

                    panelDibujo.agregarJerarquia(newHierarchy);

                    // Add the hierarchy to the entities
                    for (Arrastrable e : panelDibujo.getComponentesSeleccionadas()) {
                        ((Entidad) e).addHierarchy(newHierarchy);
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Una o más entidades seleccionadas ya ocupan el rol seleccionado en otra jerarquía.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione al menos tres entidades.");
            }

            // Desactiva el modo de selección
            panelDibujo.setSeleccionando(false);
            panelDibujo.limpiarEntidadesSeleccionadas();
            panelDibujo.repaint();
        });
    }

    public Entidad seleccionarSupertipo() {

        List<Arrastrable> entidadesSeleccionadas = panelDibujo.getComponentesSeleccionadas();
        Object[] opciones = new Object[entidadesSeleccionadas.size()];

        for (int i = 0; i < entidadesSeleccionadas.size(); i++) {
            opciones[i] = (((Entidad) entidadesSeleccionadas.get(i)).getNombre());
        }

        // Muestra el JOptionPane con los botones
        int seleccion = JOptionPane.showOptionDialog(null, "Seleccione a la entidad supertipo", "Selección",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        if (seleccion < entidadesSeleccionadas.size()) {
            return ((Entidad) entidadesSeleccionadas.get(seleccion));
        } else {
            // Arreglar
            return null;
        }
    }

    public List<Entidad> obtenerListaSubtipos(Entidad superTipo) {
        List<Arrastrable> entidadesSeleccionadas = panelDibujo.getComponentesSeleccionadas();
        List<Entidad> retorno = new ArrayList<>();

        for (Arrastrable a : entidadesSeleccionadas) {
            // Si tienen una referencia distinta al superTipo seleccionado
            if (a != superTipo) {
                retorno.add((Entidad) a);
            }
        }

        return retorno;
    }

    public Dupla<Boolean, Boolean> seleccionarTipoJerarquia() {

        // Crea los radio buttons
        JRadioButton opcionExclusiva = new JRadioButton("Exclusiva", true);
        JRadioButton opcionCompartida = new JRadioButton("Compartida");
        JRadioButton opcionTotal = new JRadioButton("Total", true);
        JRadioButton opcionParcial = new JRadioButton("Parcial");

        // Agrupa los radio buttons para que solo se pueda seleccionar uno a la vez
        ButtonGroup groupExclusivaCompartida = new ButtonGroup();
        groupExclusivaCompartida.add(opcionExclusiva);
        groupExclusivaCompartida.add(opcionCompartida);

        ButtonGroup groupTotalExclusiva = new ButtonGroup();
        groupTotalExclusiva.add(opcionTotal);
        groupTotalExclusiva.add(opcionParcial);

        // Crea un panel para contener los radio buttons
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Añade un BoxLayout al panel

        // Crea un panel para cada grupo de radio buttons
        JPanel panelEC = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEC.add(opcionExclusiva);
        panelEC.add(opcionCompartida);

        JPanel panelTP = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTP.add(opcionTotal);
        panelTP.add(opcionParcial);

        // Agrega los pares de opciones al panel
        panel.add(panelEC);
        panel.add(panelTP);

        // Muestra el panel en un JOptionPane
        JOptionPane.showOptionDialog(null, panel, "Elige una opción",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        return (new Dupla<>(opcionExclusiva.isSelected(), opcionTotal.isSelected()));
    }

    /*
    Un supertipo solo puede ser supertipo de una única jerarquía. Un subtipo solo puede
    ser subtipo de una única jerarquía.
     */
    public boolean rolesJerarquiaOcupados(Entidad supertipo, List<Entidad> subtipos) {
        if (supertipo.isSuperTipo()) {
            return false;
        }

        for (Entidad e : subtipos) {
            if (e.isSubTipo()) {
                return false;
            }
        }

        return true;
    }

    public void ocuparRoles(Entidad supertipo, List<Entidad> subtipos) {
        supertipo.setSuperTipo(Boolean.TRUE);

        for (Entidad e : subtipos) {
            e.setSubTipo(Boolean.TRUE);
        }
    }
}
