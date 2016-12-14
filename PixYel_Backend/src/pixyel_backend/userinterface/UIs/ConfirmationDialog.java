/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Josua Frank
 */
public class ConfirmationDialog extends Window {

    public static void show(String confirmationText, Runnable onOk, Runnable onCancel) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(confirmationText, onOk, onCancel);
    }

    public ConfirmationDialog(String confirmationText, Runnable onOk, Runnable onCancel) {
        addCloseListener(ce -> onCancel.run());
        //try {
        //setIcon(Icons);
        //} catch (Ressources.RessourceNotFoundException ex) {
        //}
        setCaption("Confirmation");

        VerticalLayout root = new VerticalLayout();
        root.setSpacing(true);
        root.setMargin(true);

        Label text = new Label(confirmationText);
        text.setStyleName(ValoTheme.LABEL_HUGE);

        root.addComponent(text);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

        Label ignoreMe = new Label();

        Button ok = new Button("OK");
        ok.addClickListener(c -> {
            close();
            onOk.run();
        });
        ok.addStyleName(ValoTheme.BUTTON_DANGER);

        Button cancel = new Button("Cancel");
        cancel.addClickListener(c -> {
            close();
            onCancel.run();
        });
        cancel.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        footer.addComponents(ignoreMe, ok, cancel);
        footer.setExpandRatio(ignoreMe, 1);
        root.addComponent(footer);

        setContent(root);
        setModal(true);
        center();
        setDraggable(false);
        UI.getCurrent().addWindow(this);

    }
}
