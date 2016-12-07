/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI;

import com.vaadin.server.FileResource;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.userinterface.Translations;
import pixyel_backend.userinterface.ressources.Ressources;

/**
 *
 * @author Josua Frank
 */
public class ConsoleWindow extends Window {

    static Table table = new Table();
    static int counter = 0;

    public ConsoleWindow() {

        table.setSizeFull();
        table.setSelectable(true);
        table.setImmediate(true);
        table.setColumnCollapsingAllowed(true);

        table.addContainerProperty(Translations.get(Translations.CONSOLE_DATE), DateField.class, null);
        table.addContainerProperty(Translations.get(Translations.CONSOLE_CLASS), Label.class, null);
        String id = Translations.get(Translations.CONSOLE_MESSAGE);
        table.addContainerProperty(id, Label.class, null);
        table.setColumnExpandRatio(id, 1);

        setContent(table);
        setCaption(" " + Translations.get(Translations.DESKTOP_CONSOLE));
        try {
            setIcon(new FileResource(Ressources.getRessource("desktop_console_icon.png")));
        } catch (Ressources.RessourceNotFoundException ex) {
            Log.logWarning("Could not set the Console icon: "+ex, ConsoleWindow.class);
        }
        //setPrimaryStyleName(ValoTheme.);
        setDraggable(true);
        center();
        setSizeFull();

        Log.logInfo("Testinfo", ConsoleWindow.class);
        Log.logWarning("Testwarning", ConsoleWindow.class);
        Log.logError("Testerror", ConsoleWindow.class);
        Log.logDebug("Testdebug", ConsoleWindow.class);
    }

    public static void addError(String errorMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label errorLabel = getMessageLabel(errorMessage, "red");
        counter++;
        table.addItem(new Object[]{dateField, classNameLabel, errorLabel}, counter);
        table.setCurrentPageFirstItemId(counter);
    }

    public static void addInfo(String infoMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label infoLabel = getMessageLabel(infoMessage, "black");
        counter++;
        table.addItem(new Object[]{dateField, classNameLabel, infoLabel}, counter);
        table.setCurrentPageFirstItemId(counter);
    }

    public static void addDebug(String debugMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label debugLabel = getMessageLabel(debugMessage, "blue");
        counter++;
        table.addItem(new Object[]{dateField, classNameLabel, debugLabel}, counter);
        table.setCurrentPageFirstItemId(counter);
    }

    public static void addWarning(String warningMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label warningLabel = getMessageLabel(warningMessage, "yellow");
        counter++;
        table.addItem(new Object[]{dateField, classNameLabel, warningLabel}, counter);
        table.setCurrentPageFirstItemId(counter);
    }

    private static DateField getDate() {
        DateField dateField = new DateField();
        dateField.setValue(new Date());
        dateField.setResolution(Resolution.SECOND);
        dateField.setDateFormat("dd.MM.yyyy HH:mm:ss");
        dateField.setReadOnly(true);
        dateField.setStyleName(ValoTheme.DATEFIELD_BORDERLESS);
        return dateField;
    }

    private static Label getClassNameLabel(String className) {
        Label classNameLabel = new Label(className);
        classNameLabel.setStyleName(ValoTheme.LABEL_BOLD);
        return classNameLabel;
    }

    private static Label getMessageLabel(String content, String color) {
        Label message = new Label(content);
        switch (color) {
            case "red":
                message.setStyleName("label-red");
                message.addStyleName(ValoTheme.LABEL_FAILURE);
                break;
            case "green":
                message.setStyleName("label-green");
                break;
            case "blue":
                message.setStyleName(ValoTheme.LABEL_LIGHT);
                message.addStyleName("label-blue");
                break;
            case "yellow":
                message.setStyleName("label-yellow");
                message.addStyleName(ValoTheme.LABEL_BOLD);
                break;
            case "black":
                //Default
                break;
        }
        return message;
    }
}
