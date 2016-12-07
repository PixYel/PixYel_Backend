/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import pixyel_backend.Log;
import pixyel_backend.userinterface.Translations;
import pixyel_backend.userinterface.ressources.Ressources;

/**
 *
 * @author Josua Frank
 */
public class ConsoleWindow extends Window {
    
    private int counter = 0;
    private VerticalLayout layout;
    
    public static ConsoleWindow show(Runnable onClose) {
        return new ConsoleWindow(onClose);
    }
    
    public ConsoleWindow(Runnable onClose) {
        Log.addConsoleWindow(this);
        addCloseListener((CloseEvent ce) -> {
            Log.removeConsoleWindow(this);
            onClose.run();
        });
        Page.getCurrent().addBrowserWindowResizeListener((s) -> onResized());
        
        layout = new VerticalLayout();
        
        setImmediate(true);
        setContent(layout);
        setCaption(" " + Translations.get(Translations.DESKTOP_CONSOLE));
        try {
            setIcon(new FileResource(Ressources.getRessource("desktop_console_icon_small.png")));
        } catch (Ressources.RessourceNotFoundException ex) {
            Log.logWarning("Could not set the Console icon: " + ex, ConsoleWindow.class);
        }
        //setPrimaryStyleName(ValoTheme.);
        setDraggable(true);
        center();
        int WIDTH = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowWidth());
        int HIGHT = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowHeight());
        setWidth(WIDTH, Unit.PIXELS);
        setHeight(HIGHT, Unit.PIXELS);
        UI.getCurrent().addWindow(this);
    }
    
    public void onResized() {
        if (isAttached()) {
            int WIDTH = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowWidth());
            int HIGHT = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowHeight());
            setWidth(WIDTH, Unit.PIXELS);
            setHeight(HIGHT, Unit.PIXELS);
        }
    }
    
    public void addError(String errorMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label errorLabel = getMessageLabel(errorMessage, "red");
        counter++;
        addToConsole(dateField, classNameLabel, errorLabel);
    }
    
    public void addInfo(String infoMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label infoLabel = getMessageLabel(infoMessage, "black");
        counter++;
        addToConsole(dateField, classNameLabel, infoLabel);
    }
    
    public void addDebug(String debugMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label debugLabel = getMessageLabel(debugMessage, "blue");
        counter++;
        addToConsole(dateField, classNameLabel, debugLabel);
    }
    
    public void addWarning(String warningMessage, String className) {
        DateField dateField = getDate();
        Label classNameLabel = getClassNameLabel(className);
        Label warningLabel = getMessageLabel(warningMessage, "yellow");
        counter++;
        addToConsole(dateField, classNameLabel, warningLabel);
    }
    
    ArrayBlockingQueue<HorizontalLayout> maxQueue = new ArrayBlockingQueue<>(250);
    
    private void addToConsole(DateField date, Label className, Label messageLabel) {
        HorizontalLayout row = new HorizontalLayout(date, className, messageLabel);
        row.setComponentAlignment(date, Alignment.MIDDLE_LEFT);
        row.setComponentAlignment(className, Alignment.MIDDLE_LEFT);
        row.setComponentAlignment(messageLabel, Alignment.MIDDLE_LEFT);
        row.setSpacing(true);
        if (maxQueue.size() >= 250) {
            layout.removeComponent(maxQueue.poll());
        }
        maxQueue.add(row);
        layout.addComponent(row);
        setScrollTop(999);
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
