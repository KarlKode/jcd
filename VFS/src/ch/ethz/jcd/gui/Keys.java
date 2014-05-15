package ch.ethz.jcd.gui;

/**
 * Created by leo on 13/05/14.
 */
public class Keys {

    private static boolean controlPressed = false;
    private static boolean altPressed;

    public static boolean isCtrlPressed(){
        return controlPressed;
    }

    public static void setCtrlPressed(boolean pressed){
        controlPressed = pressed;
    }

    public static void setAltPressed(boolean altPressed) {
        Keys.altPressed = altPressed;
    }
}
