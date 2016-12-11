/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.userinterface.UIs.DesktopUI.apps;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import pixyel_backend.Log;
import pixyel_backend.userinterface.Translations;
import pixyel_backend.userinterface.ressources.Ressources;

/**
 *
 * @author Josua Frank
 */
public class OnlineMonitorWindow extends Window {

    private VerticalLayout layout;

    public static void show(Runnable onClose) {
        new UserManagementWindow(onClose);
    }

    public OnlineMonitorWindow(Runnable onClose) {
        addCloseListener((Window.CloseEvent ce) -> {
            onClose.run();
        });
        Page.getCurrent().addBrowserWindowResizeListener((s) -> onResized());

        layout = new VerticalLayout();
        HorizontalLayout scrollLayout = new HorizontalLayout(layout);

        setImmediate(true);
        setContent(scrollLayout);
        setCaption(" " + Translations.get(Translations.DESKTOP_ONLINE_MONITOR));

        try {
            setIcon(new FileResource(Ressources.getRessource("desktop_system_monitor_icon_small.png")));
        } catch (Ressources.RessourceNotFoundException ex) {

            Log.logWarning("Could not set the Console icon: " + ex, ConsoleWindow.class);
        }

        setDraggable(true);
        center();
        int WIDTH = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowWidth());
        int HIGHT = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowHeight());
        setWidth(WIDTH, Sizeable.Unit.PIXELS);
        setHeight(HIGHT, Sizeable.Unit.PIXELS);
        UI.getCurrent().addWindow(this);
    }

    public void onResized() {
        if (isAttached()) {
            int WIDTH = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowWidth());
            int HIGHT = (int) (0.5 * UI.getCurrent().getPage().getBrowserWindowHeight());
            setWidth(WIDTH, Sizeable.Unit.PIXELS);
            setHeight(HIGHT, Sizeable.Unit.PIXELS);
        }
    }

//    public VerticalLayout createContent() {
//        Statistics. 
//    }

//    public static int getJavaCpuUtilisation() {
//        JavaSysMon mon = new JavaSysMon();
//        mon.cpuFrequencyInHz();
//        System.out.println(mon.physical().getFreeBytes());
//        System.out.println(mon.physical().getTotalBytes());
//    }

    /**
     * [0] = total memory, [1] free memory
     *
     * @return
     */
//    public int[] getMemory() {
//
//    }
}
