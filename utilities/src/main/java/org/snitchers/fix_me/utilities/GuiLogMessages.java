package org.snitchers.fix_me.utilities;

import javax.swing.*;

public class GuiLogMessages extends JTextArea {

    public GuiLogMessages() {
        super(1, 1);
        setEditable(false);
        setLineWrap(true);
    }

    @Override
    public void append(String message) {
        super.append(message + "\n\n    ");
        setRows( getRows() + 1 );
    }
}
