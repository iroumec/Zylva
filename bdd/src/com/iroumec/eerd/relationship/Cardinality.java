package com.iroumec.eerd.relationship;

import com.iroumec.components.basicComponents.Line;
import com.iroumec.eerd.relationship.cardinalities.CardinalityMenu;
import com.iroumec.userPreferences.LanguageManager;
import com.iroumec.components.basicComponents.Guard;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public final class Cardinality extends Guard {

    private final Line line;
    private final CardinalityMenu menu;
    private String minimumValue, maximumValue;

    public Cardinality(String minimumValue, String maximumValue, Line line, CardinalityMenu menu) {
        super(giveFormat(minimumValue, maximumValue), line);
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.line = line;
        this.menu = menu;
    }

    public static String giveFormat(String firstValue, String secondValue) {
        return "(" + firstValue + ", " + secondValue + ")";
    }

    public String getMinimumCardinality() { return this.minimumValue; }
    public String getMaximumCardinality() { return this.maximumValue; }

    public Line getLine() { return this.line; }

    /* -------------------------------------------------------------------------------------------------------------- */
    /*                                               Overridden Methods                                               */
    /* -------------------------------------------------------------------------------------------------------------- */

    @Override
    protected JPopupMenu getPopupMenu() {

        return this.menu.getPopupMenu(this);
    }

    /**
     * Given a cardinality, changes its values.
     */
    public void changeCardinality() {

        JTextField cardinalidadMinimaCampo = new JTextField(3);
        JTextField cardinalidadMaximaCampo = new JTextField(3);

        JPanel miPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        miPanel.add(new JLabel(LanguageManager.getMessage("cardinality.minimum")));
        miPanel.add(cardinalidadMinimaCampo);
        miPanel.add(new JLabel(LanguageManager.getMessage("cardinality.maximum")));
        miPanel.add(cardinalidadMaximaCampo);

        this.diagram.setFocus(cardinalidadMinimaCampo);

        int resultado = JOptionPane.showConfirmDialog(null, miPanel, LanguageManager.getMessage("cardinality.twoValues"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String minText = cardinalidadMinimaCampo.getText().trim();
            String maxText = cardinalidadMaximaCampo.getText().trim();

            Optional<Integer> minValue = parseInteger(minText);

            // Validates if the fields are not empty.
            if (minText.isEmpty() || maxText.isEmpty()) {
                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("cardinality.warning.emptyFields"), LanguageManager.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validates if the minimum cardinality is a valid number.
            if (minValue.isEmpty() || minValue.get() < 0) {
                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("cardinality.warning.invalidMinimum"), LanguageManager.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validates if the maximum cardinality is a valid number or a letter.
            if (!isIntegerOrLetter(maxText) || (isInteger(maxText) && Integer.parseInt(maxText) < 1)) {
                JOptionPane.showMessageDialog(null, LanguageManager.getMessage("cardinality.warning.invalidMaximum"), LanguageManager.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If the maximum cardinality is a number, it must be greater than the minimum cardinality.
            if (isInteger(maxText)) {
                int maxValue = Integer.parseInt(maxText);
                if (minValue.get() > maxValue) {
                    JOptionPane.showMessageDialog(null, LanguageManager.getMessage("cardinality.warning.invalidRange"), LanguageManager.getMessage("error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // If everything is valid, the cardinality is updated.
            this.setText(Cardinality.giveFormat(minText, maxText));
            this.minimumValue = minText;
            this.maximumValue = maxText;

            // Here only the area of the cardinality could be repainted, but, if the cardinality now has a considerable
            // greater number, it'll lead to visual noise until all the panel is repainted.
            this.diagram.repaint();
        }
    }

    /**
     * It parses a text to <Code>Integer</Code> if it's possible.
     *
     * @param text Text to be parsed.
     * @return {@code OptionalPresence<Integer>} containing the parsed text if it was possible.
     */
    private Optional<Integer> parseInteger(String text) {
        try {
            return Optional.of(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Validates if a text is an integer xor a letter.
     *
     * @param text Text to be checked.
     * @return <Code>TRUE</Code> if the text is an integer xor a letter. It returns <Code>FALSE</Code> in any other
     * case.
     */
    private boolean isIntegerOrLetter(String text) {
        return text.matches("\\d+") || text.matches("[a-zA-Z]");
    }

    /**
     * Validates if a text is strictly a number.
     *
     * @param text Text to be checked.
     * @return <Code>TRUE</Code> if the text is an integer. It returns <Code>FALSE</Code> in any other case.
     */
    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
