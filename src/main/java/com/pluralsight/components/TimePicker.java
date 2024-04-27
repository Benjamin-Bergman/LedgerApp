// Copyright (c) Benjamin Bergman 2024.

package com.pluralsight.components;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.*;

import java.time.*;
import java.time.temporal.*;

/**
 * Represents a picker that lets users select a time.
 */
public final class TimePicker extends Panel {
    private final IntPicker hourPicker, minutePicker;
    private final AMPMPicker ampmPicker;

    /**
     * Creates a new instance whose default value is {@code LocalTime.now()}.
     */
    public TimePicker() {
        this(LocalTime.now());
    }

    /**
     * Creates a new instance with the given default time.
     *
     * @param defaultTime The initial value of this picker.
     */
    public TimePicker(LocalTime defaultTime) {
        super(new LinearLayout(Direction.HORIZONTAL));

        hourPicker = new IntPicker(defaultTime.getHour(), 1, 12);
        minutePicker = new IntPicker(defaultTime.getMinute(), 0, 59);
        ampmPicker = new AMPMPicker(defaultTime.get(ChronoField.AMPM_OF_DAY) == 0);
        addComponent(hourPicker);
        addComponent(new Label(":"));
        addComponent(minutePicker);
        addComponent(ampmPicker);
    }

    /**
     * @return {@code true} if the currently selected time is the default.
     */
    public boolean isDefault() {
        return hourPicker.isDefault() && minutePicker.isDefault() && ampmPicker.isDefaultValue();
    }

    /**
     * @return The current value selected by the picker.
     */
    public LocalTime timeValue() {
        //noinspection MagicNumber
        return LocalTime.of((ampmPicker.isAM() ? 0 : 12) + hourPicker.getSelectedValue(), minutePicker.getSelectedValue());
    }

    private static final class AMPMPicker extends AbstractInteractableComponent<AMPMPicker> {
        private final boolean defaultValue;
        @SuppressWarnings("FieldNamingConvention")
        private boolean isAM;

        AMPMPicker(boolean isAM) {
            this.isAM = isAM;
            defaultValue = isAM;
        }

        boolean isDefaultValue() {
            return defaultValue == isAM;
        }

        @SuppressWarnings("SuspiciousGetterSetter")
        boolean isAM() {
            return isAM;
        }

        @Override
        protected InteractableRenderer<AMPMPicker> createDefaultRenderer() {
            return new AMPMPickerRenderer();
        }

        @Override
        protected Result handleKeyStroke(KeyStroke keyStroke) {
            if (isActivationStroke(keyStroke)) {
                isAM = !isAM;
                invalidate();
                return Result.HANDLED;
            }
            if (keyStroke.getKeyType() == KeyType.Character)
                //noinspection SwitchStatementWithoutDefaultBranch
                switch (keyStroke.getCharacter()) {
                    case 'a', 'A' -> {
                        isAM = true;
                        invalidate();
                        return Result.HANDLED;
                    }
                    case 'p', 'P' -> {
                        isAM = false;
                        invalidate();
                        return Result.HANDLED;
                    }
                }

            return super.handleKeyStroke(keyStroke);
        }

        @SuppressWarnings("InnerClassTooDeeplyNested")
        private final class AMPMPickerRenderer implements InteractableRenderer<AMPMPicker> {
            @Override
            public TerminalPosition getCursorLocation(AMPMPicker ampmPicker) {
                return TerminalPosition.TOP_LEFT_CORNER;
            }

            @Override
            public TerminalSize getPreferredSize(AMPMPicker ampmPicker) {
                return new TerminalSize(2, 1);
            }

            @Override
            public void drawComponent(TextGUIGraphics textGUIGraphics, AMPMPicker ampmPicker) {
                var definition = getTheme().getDefinition(AMPMPicker.class);
                if (isFocused()) textGUIGraphics.applyThemeStyle(definition.getActive());
                else textGUIGraphics.applyThemeStyle(definition.getNormal());
                textGUIGraphics.putString(0, 0, isAM ? "AM" : "PM");
            }
        }
    }
}
