/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI;

import com.vaadin.server.FileResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Collection;
import java.util.Date;
import pixyel_backend.Log;
import pixyel_backend.userinterface.Translations;
import pixyel_backend.userinterface.Userinterface;
import pixyel_backend.userinterface.ressources.Ressources;

/**
 *
 * @author Josua Frank
 */
public class ConsoleWindow extends Window {

    private Table table = new Table();
    private int counter = 0;

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
            setIcon(new FileResource(Ressources.getRessource("desktop_console_icon_small.png")));
        } catch (Ressources.RessourceNotFoundException ex) {
            Log.logWarning("Could not set the Console icon: " + ex, ConsoleWindow.class);
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

    public static void logError(String errorMessage, String className) {
        Userinterface.sessions.stream().map((Userinterface ui) -> ui.getWindows()).forEach((Collection<Window> windows) -> {
            windows.forEach((Window window) -> {
                if (window instanceof ConsoleWindow) {
                    ((ConsoleWindow) window).addError(errorMessage, className);
                }
            });
        });
    }

    public void addError(String errorMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label errorLabel = getMessageLabel(errorMessage, "red");
        counter++;
        table.addItem(new Object[]{dateField, classNameLabel, errorLabel}, counter);
        table.setCurrentPageFirstItemId(counter);
    }

    public static void logInfo(String infoMessage, String className) {
        Userinterface.sessions.stream().map((Userinterface ui) -> ui.getWindows()).forEach((Collection<Window> windows) -> {
            windows.forEach((Window window) -> {
                if (window instanceof ConsoleWindow) {
                    ((ConsoleWindow) window).addInfo(infoMessage, className);
                }
            });
        });
    }

    public void addInfo(String infoMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label infoLabel = getMessageLabel(infoMessage, "black");
        counter++;
        table.addItem(new Object[]{dateField, classNameLabel, infoLabel}, counter);
        table.setCurrentPageFirstItemId(counter);
    }

    public static void logDebug(String debugMessage, String className) {
        Userinterface.sessions.stream().map((Userinterface ui) -> ui.getWindows()).forEach((Collection<Window> windows) -> {
            windows.forEach((Window window) -> {
                if (window instanceof ConsoleWindow) {
                    ((ConsoleWindow) window).addDebug(debugMessage, className);
                }
            });
        });
    }

    public void addDebug(String debugMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label debugLabel = getMessageLabel(debugMessage, "blue");
        counter++;
        table.addItem(new Object[]{dateField, classNameLabel, debugLabel}, counter);
        table.setCurrentPageFirstItemId(counter);
    }

    public static void logWarning(String warningMessage, String className) {
        Userinterface.sessions.stream().map((Userinterface ui) -> ui.getWindows()).forEach((Collection<Window> windows) -> {
            windows.forEach((Window window) -> {
                if (window instanceof ConsoleWindow) {
                    ((ConsoleWindow) window).addWarning(warningMessage, className);
                }
            });
        });
    }

    public void addWarning(String warningMessage, String className) {
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
