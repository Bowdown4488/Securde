/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import java.util.Collections;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 *
 * @author luisenricolopez
 */
public class PasswordField {
    private JTextField textField;       
    private String password;
    private boolean echoLock;
    
    public PasswordField(javax.swing.JTextField textField) {
        this.textField = textField;
        this.password = "";
        echoLock = false;
        
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    insert(e.getDocument().getText(e.getOffset(), e.getLength()), e.getOffset());
                } catch (BadLocationException ex) {}
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                delete(e.getOffset(), e.getLength());
            }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });
    }

    public void insert(String text, int offset) {
        if (echoLock) { return; }
        password = password.substring(0, offset) + text + password.substring(offset, password.length());
        SwingUtilities.invokeLater(() -> {
            echoLock = true;
            textField.setText(String.join("", Collections.nCopies(length(), "*")));
            textField.setCaretPosition(offset+1);
            echoLock = false;
        });
    }

    public void delete(int offset, int length) {
        if (echoLock) { return; }
        password = password.substring(0, offset) + password.substring(offset+length, password.length());
        SwingUtilities.invokeLater(() -> {
            echoLock = true;
            textField.setText(String.join("", Collections.nCopies(length(), "*")));
            textField.setCaretPosition(offset);
            echoLock = false;
        });
    }
    
    public void clear() {
        if (echoLock) { return; }
        password = "";
        
        SwingUtilities.invokeLater(() -> {
            echoLock = true;
            textField.setText("");
            echoLock = false;
        });
    }

    public int length() {
        return password.length();
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return password;
    }

}
