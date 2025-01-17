package core;

import input.Input;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferStrategy;

public class Engine {
    private JFrame Window;
    private Canvas Canvas;
    private BufferStrategy Buffer;
    private boolean Running;

    private int[][] PixelBuffer;
    private boolean BufferDirty = false;
    private int ResizeFactor = Global.BUFFER_RESIZE;

    private int Frames = 0;
    private long Timer = System.currentTimeMillis();

    private boolean Focused = true;

    private boolean DrawCircle = false;
    private boolean DrawRectangle = false;
    private boolean DrawLine = false;

    public void Initialize() {
        Window = new JFrame(Global.WINDOW_TITLE);
        Canvas = new Canvas();
        Canvas.setPreferredSize(new Dimension(Global.WINDOW_WIDTH, Global.WINDOW_HEIGHT));
        Canvas.setFocusable(false);

        Window.add(Canvas);
        Window.pack();
        Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Window.setResizable(false);
        Window.setLocationRelativeTo(null);
        Window.setVisible(true);

        Canvas.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                Focused = true;
                System.out.println("Window focused.");
            }

            @Override
            public void focusLost(FocusEvent e) {
                Focused = false;
                System.out.println("Window lost focus.");
            }
        });

        PixelBuffer = new int[Global.WINDOW_WIDTH / ResizeFactor][Global.WINDOW_HEIGHT / ResizeFactor];
        Canvas.createBufferStrategy(2);
        Buffer = Canvas.getBufferStrategy();

        Running = true;

        Window.setTitle(Global.WINDOW_TITLE + " | FPS: 0");
    }

    public void Start(Input InputHandler) {
        final double nsPerFrame = 1_000_000_000.0 / Global.FPS;

        while (Running) {
            long CurrentTime = System.nanoTime();

            if (Focused) {
                InputHandler.Update();
                HandleInput(InputHandler);
            }

            Render();
            Frames++;

            if (System.currentTimeMillis() - Timer > 1000) {
                Window.setTitle(Global.WINDOW_TITLE + " | FPS: " + Frames);
                Frames = 0;
                Timer += 1000;
            }

            if (Global.VSYNC_ENABLED) {
                long ElapsedTime = System.nanoTime() - CurrentTime;
                if (ElapsedTime < nsPerFrame) {
                    try {
                        Thread.sleep((long) ((nsPerFrame - ElapsedTime) / 1_000_000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Cleanup();
    }

    private void HandleInput(Input InputHandler) {
        DrawCircle = InputHandler.IsKeyPressed(KeyEvent.VK_C);
        DrawRectangle = InputHandler.IsKeyPressed(KeyEvent.VK_R);
        DrawLine = InputHandler.IsKeyPressed(KeyEvent.VK_L);

        if (InputHandler.IsQuitRequested()) {
            Running = false;
        }
    }

    public void Render() {
        ClearBuffer();

        if (DrawCircle) {
            DrawCircle(PixelBuffer.length / 2, PixelBuffer[0].length / 2, 10, 0xFF0000); // Red Circle
        }

        if (DrawRectangle) {
            DrawRectangle(5, 5, 20, 10, 0x00FF00); // Green Rectangle
        }

        if (DrawLine) {
            DrawLine(0, 0, PixelBuffer.length - 1, PixelBuffer[0].length - 1, 0x0000FF); // Blue Line
        }

        Graphics G = Buffer.getDrawGraphics();
        G.setColor(Color.BLACK);
        G.fillRect(0, 0, Global.WINDOW_WIDTH, Global.WINDOW_HEIGHT);

        RenderBuffer(G);

        G.dispose();
        Buffer.show();
    }

    private void ClearBuffer() {
        if (!BufferDirty) return;

        for (int x = 0; x < PixelBuffer.length; x++) {
            for (int y = 0; y < PixelBuffer[0].length; y++) {
                PixelBuffer[x][y] = 0;
            }
        }

        BufferDirty = false;
    }

    private void RenderBuffer(Graphics G) {
        for (int x = 0; x < PixelBuffer.length; x++) {
            for (int y = 0; y < PixelBuffer[0].length; y++) {
                int Color = PixelBuffer[x][y];
                if (Color != 0) {
                    G.setColor(new Color(Color));
                    G.fillRect(x * ResizeFactor, y * ResizeFactor, ResizeFactor, ResizeFactor);
                }
            }
        }
    }

    public void SetPixel(int x, int y, int Color) {
        if (x >= 0 && x < PixelBuffer.length && y >= 0 && y < PixelBuffer[0].length) {
            PixelBuffer[x][y] = Color;
            BufferDirty = true;
        }
    }

    public void DrawCircle(int cx, int cy, int radius, int color) {
        int x = radius, y = 0, radiusError = 1 - x;

        while (x >= y) {
            SetPixel(cx + x, cy + y, color);
            SetPixel(cx - x, cy + y, color);
            SetPixel(cx + x, cy - y, color);
            SetPixel(cx - x, cy - y, color);
            SetPixel(cx + y, cy + x, color);
            SetPixel(cx - y, cy + x, color);
            SetPixel(cx + y, cy - x, color);
            SetPixel(cx - y, cy - x, color);

            y++;
            if (radiusError < 0) {
                radiusError += 2 * y + 1;
            } else {
                x--;
                radiusError += 2 * (y - x) + 1;
            }
        }
    }

    public void DrawRectangle(int x, int y, int width, int height, int color) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                SetPixel(x + i, y + j, color);
            }
        }
    }

    public void DrawLine(int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy, e2;

        while (true) {
            SetPixel(x0, y0, color);
            if (x0 == x1 && y0 == y1) break;
            e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x0 += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    public void Cleanup() {
        Window.dispose();
    }

    public Canvas GetCanvas() {
        return Canvas;
    }
}
