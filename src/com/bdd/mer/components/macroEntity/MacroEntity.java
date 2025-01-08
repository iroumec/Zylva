package com.bdd.mer.components.macroEntity;

import com.bdd.mer.components.Component;
import com.bdd.mer.components.entity.Entity;
import com.bdd.mer.components.relationship.Relationship;
import com.bdd.mer.frame.DrawingPanel;
import com.bdd.mer.frame.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MacroEntity extends Component {

    // List of components forming the MacroEntity.
    private final Relationship relationship;

    public MacroEntity(Relationship relationship) {
        this.relationship = relationship;
    }

    @Override
    protected PopupMenu getGenericPopupMenu() {

        DrawingPanel drawingPanel = this.getPanelDibujo();
        PopupMenu macroEntityPopupMenu = new PopupMenu(drawingPanel);

        JMenuItem deleteMacroEntity = new JMenuItem("Delete");
        deleteMacroEntity.addActionListener(_ -> drawingPanel.getActioner().deleteSelectedComponents());

        macroEntityPopupMenu.addOption(deleteMacroEntity);

        return macroEntityPopupMenu;

    }

    @Override
    public void draw(Graphics2D g2) {

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        // Crea una copia de la lista para no modificar la original
        List<Component> components = new ArrayList<>(this.relationship.getRelatedComponents());
        components.add(relationship);

        // Calcula los límites del rectángulo que engloba todos los componentes
        for (Component component : components) {
            Rectangle bounds = component.getBounds();

            minX = Math.min(minX, (int) bounds.getMinX());
            minY = Math.min(minY, (int) bounds.getMinY());
            maxX = Math.max(maxX, (int) bounds.getMaxX());
            maxY = Math.max(maxY, (int) bounds.getMaxY());
        }

        int margen = 5;

        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.BLACK);

        // Cambia el color y grosor del borde si está seleccionado
        if (this.isSelected()) {
            this.setSelectionOptions(g2);
        }

        // Calcula el ancho y alto del rectángulo correctamente
        int rectWidth = (maxX - minX) + 2 * margen;
        int rectHeight = (maxY - minY) + 2 * margen;

        // Dibuja el rectángulo
        g2.drawRect(minX - margen, minY - margen, rectWidth, rectHeight);

        // Actualiza la forma de la entidad
        this.setShape(new Rectangle(minX - margen, minY - margen, rectWidth, rectHeight));

        // Restablece el grosor del borde y el color
        g2.setStroke(new BasicStroke(1));
        g2.setColor(Color.BLACK);
    }


    @Override
    public void cleanPresence() {

    }

    @Override
    public void changeReference(Entity oldEntity, Entity newEntity) {

    }

}
