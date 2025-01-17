package input;

import java.awt.*;
import java.awt.event.*;

public class Input implements KeyListener, MouseListener, MouseMotionListener {
    private final boolean[] Keys = new boolean[256];
    private final boolean[] KeyHandled = new boolean[256];

    private final boolean[] MouseButtons = new boolean[3];
    private final boolean[] MouseHandled = new boolean[3];

    private int MouseX, MouseY;
    private int LastMouseX = -1, LastMouseY = -1;
    private boolean QuitRequested = false;

    public void Initialize(Component Canvas) {
        Canvas.addKeyListener(this);
        Canvas.addMouseListener(this);
        Canvas.addMouseMotionListener(this);

        Canvas.setFocusable(true);
        Canvas.requestFocusInWindow();
    }

    public void Update() {
        if (Keys[KeyEvent.VK_ESCAPE]) QuitRequested = true;

        for (int i = 0; i < Keys.length; i++) {
            if (Keys[i] && !KeyHandled[i]) {
                if (i >= 0 && i <= 255) {
                    System.out.println("Key Pressed: " + KeyEvent.getKeyText(i));
                } else {
                    System.out.println("Unknown Key Pressed: Code " + i);
                }
                KeyHandled[i] = true;
            } else if (!Keys[i] && KeyHandled[i]) {
                System.out.println("Key Released: " + KeyEvent.getKeyText(i));
                KeyHandled[i] = false;
            }
        }

        for (int i = 0; i < MouseButtons.length; i++) {
            if (MouseButtons[i] && !MouseHandled[i]) {
                System.out.println("Mouse Button Pressed: " + (i + 1));
                MouseHandled[i] = true;
            } else if (!MouseButtons[i] && MouseHandled[i]) {
                System.out.println("Mouse Button Released: " + (i + 1));
                MouseHandled[i] = false;
            }
        }

        if (MouseX != LastMouseX || MouseY != LastMouseY) {
            System.out.println("Mouse Moved: (" + MouseX + ", " + MouseY + ")");
            LastMouseX = MouseX;
            LastMouseY = MouseY;
        }
    }

    public boolean IsKeyPressed(int keyCode) {
        return Keys[keyCode];
    }

    public boolean IsQuitRequested() {
        return QuitRequested;
    }

    public int GetMouseX() {
        return MouseX;
    }

    public int GetMouseY() {
        return MouseY;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() >= 0 && e.getKeyCode() < Keys.length) {
            Keys[e.getKeyCode()] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() >= 0 && e.getKeyCode() < Keys.length) {
            Keys[e.getKeyCode()] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() <= 3) {
            MouseButtons[e.getButton() - 1] = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() <= 3) {
            MouseButtons[e.getButton() - 1] = false;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        MouseX = e.getX();
        MouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
