/**
 * @(#)GUITool.java 0.9 2000/03/25 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

/**
 * Tools with a GUI implement this interface.
 * 
 * @version 0.9, 2000/03/25
 * @author  Andr&eacute; Platzer
 * @todo document, enhance design
 */
public interface GUITool {

    void init();

    void start();

    void stop();

    void destroy();
}

