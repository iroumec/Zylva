package com.bdd.mer.interfaz.popup;

import com.bdd.mer.estatica.Component;
import com.bdd.mer.estatica.Entidad;
import com.bdd.mer.estatica.Relacion;
import com.bdd.mer.estatica.atributo.Atributo;
import com.bdd.mer.estatica.atributo.TipoAtributo;
import com.bdd.mer.interfaz.PanelDibujo;

import javax.swing.*;
import java.awt.*;

public class addAtributeMenuItem extends JMenuItem {

    private PopupMenu popupMenu;

    addAtributeMenuItem(String name, PopupMenu popupMenu, PanelDibujo panelDibujo) {
        setText(name);
        this.popupMenu = popupMenu;

        addActionListener(_ -> {
            Component object = popupMenu.getObject();

            // Crea los componentes del panel
            JTextField fieldNombre = new JTextField(10);
            JCheckBox boxOpcional = new JCheckBox("Opcional");
            JCheckBox boxMultivaluado = new JCheckBox("Multivaluado");

            TipoAtributo attributeType = selectAtributeType();

            // Crea un array de los componentes
            Object[] message = {
                    "Ingrese el nombre del atributo:", fieldNombre,
                    "Seleccione las opciones:", boxOpcional, boxMultivaluado
            };

            // Muestra el JOptionPane
            int option = JOptionPane.showConfirmDialog(null, message, "Ingrese la información del atributo", JOptionPane.OK_CANCEL_OPTION);

            // Muestra una ventana emergente para ingresar el nombre del atributo y seleccionar las opciones
            if (option == JOptionPane.OK_OPTION) {
                String nombre = fieldNombre.getText();

                if (nombre != null) {
                    if (object.getClass().toString().equals("class com.bdd.mer.estatica.Entidad")) {

                        // En caso de seleccionarse un atributo principal, ya teniendo uno, el anterior
                        // principal pasa a ser alternativo.
                        if (attributeType == TipoAtributo.MAIN) {
                            Atributo mainAttribute = ((Entidad) object).hasMainAttribute();

                            if (mainAttribute != null) {
                                mainAttribute.setTipo(TipoAtributo.ALTERNATIVE);
                            }
                        }

                        ((Entidad) object).agregarAtributo(new Atributo((Entidad) object, nombre,
                                boxOpcional.isSelected(),
                                boxMultivaluado.isSelected(),
                                attributeType));
                    } else if (object.getClass().toString().equals("class com.bdd.mer.estatica.Relacion")) {
                            ((Relacion) object).agregarAtributo(new Atributo((Relacion) object, nombre,
                                    boxOpcional.isSelected(),
                                    boxMultivaluado.isSelected(),
                                    attributeType));
                    }
                }

                panelDibujo.repaint();

            }
        });
    }

    private TipoAtributo selectAtributeType() {

        // Crea los radio buttons
        JRadioButton commonAttributeOption = new JRadioButton("Común", true);
        JRadioButton alternativeAttributeOption = new JRadioButton("Alternativo");
        JRadioButton mainAttributeOption = new JRadioButton("Principal");

        // Agrupa los radio buttons para que solo se pueda seleccionar uno a la vez
        ButtonGroup group = new ButtonGroup();
        group.add(commonAttributeOption);
        group.add(alternativeAttributeOption);
        group.add(mainAttributeOption);

        // Crea un panel para contener los radio buttons
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Añade un BoxLayout al panel

        // Crea un panel para el grupo de radio buttons
        JPanel panelAttribute = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAttribute.add(commonAttributeOption);
        panelAttribute.add(alternativeAttributeOption);
        panelAttribute.add(mainAttributeOption);

        // Agrega los pares de opciones al panel
        panel.add(panelAttribute);

        // Muestra el panel en un JOptionPane
        JOptionPane.showOptionDialog(null, panel, "Tipo de atributo: ",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if (commonAttributeOption.isSelected()) {
            return TipoAtributo.COMMON;
        } else if (alternativeAttributeOption.isSelected()) {
            return TipoAtributo.ALTERNATIVE;
        } else {
            return TipoAtributo.MAIN;
        }
    }
}
