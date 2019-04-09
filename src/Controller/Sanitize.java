/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author J
 */
public class Sanitize {

    String output;
    Boolean validated;
    String apos = "'";
    String slash = "/";
    String quote = "\"";
    String ampersand = "&";

    public String sanitize(String input) {
        input = input.contains("&") ? input.replaceAll("&", "&amp") : input;
        input = input.contains("<") ? input.replaceAll("<", "&lt") : input;
        input = input.contains(">") ? input.replaceAll(">", "&gt") : input;
        input = input.contains("\\") ? input.replaceAll("\\\\", "&back") : input;
        input = input.contains(";") ? input.replaceAll(";", "&semi") : input;
        System.out.println("input sanitized word = " + input);
        return input;
    }

    public String deSanitize(String input) {
        input = input.contains("&lt") ? input.replaceAll("&lt", "<") : input;
        input = input.contains("&gt") ? input.replaceAll("&gt", ">") : input;
        input = input.contains("&semi") ? input.replaceAll("&semi", ";") : input;
        input = input.contains("&amp") ? input.replaceAll("&amp", "&") : input;
        System.out.println("input desanitized = " + input);
        return input;
    }

    public boolean validateStringSize(String input) {

        return (input.length() < 32) && input.length() > 0;
    }

    public boolean validateIntSize(String input) {
        try {
            return (Integer.parseInt(input) < 2_000_000_000 && input.length() < 32 && input.length() > 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateDoubleSize(String input) {
        if (input.contains(".")) {
            int integerPlaces = input.indexOf('.');
            int decimalPlaces = input.length() - integerPlaces - 1;
            try {
                return (Double.parseDouble(input) < 2_000_000_000.00 && input.length() < 32 && input.length() > 0 && decimalPlaces <= 2);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            try {
                return (Double.parseDouble(input) < 2_000_000_000.00 && input.length() < 32 && input.length() > 0);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
