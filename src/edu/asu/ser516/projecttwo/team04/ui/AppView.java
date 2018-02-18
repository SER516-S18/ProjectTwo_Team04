package edu.asu.ser516.projecttwo.team04.ui;

import edu.asu.ser516.projecttwo.team04.ClientModel;
import edu.asu.ser516.projecttwo.team04.ServerModel;
import edu.asu.ser516.projecttwo.team04.listeners.ClientListener;
import edu.asu.ser516.projecttwo.team04.listeners.ServerListener;
import edu.asu.ser516.projecttwo.team04.util.Log;
import edu.asu.ser516.projecttwo.team04.util.UIStandards;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AppView, the main app singleton frame that contains either the client or server UI
 */
public class AppView extends JFrame {
    public static final int TYPE_CLIENT = 0;
    public static final int TYPE_SERVER = 1;

    private static AppView _instance = null;

    public static AppView get() {
        if(_instance == null)
            _instance = new AppView();

        return _instance;
    }

    private int _type = TYPE_CLIENT;
    private AppToolbar viewToolbar;
    private JPanel viewMenu;
    private ConsoleView viewConsole;

    private AppView() {}

    /**
     * Set whether the app is a client or server
     * @param type
     */
    public void setType(int type) {
        if(type != TYPE_CLIENT && type != TYPE_SERVER)
            throw new IllegalArgumentException("Type must be TYPE_CLIENT or TYPE_SERVER");
        else {
            _type = type;
            this.updateType();
        }
    }

    /**
     * Initialize the AppView frame UI
     */
    public void init() {
        this.setMinimumSize(new Dimension(800, 600));
        this.setBackground(UIStandards.BACKGROUND_BLUE);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("SER516 Project Two: Team 4");
        this.setLayout(new BorderLayout());

        // Add top
        viewToolbar = new AppToolbar();
        this.add(viewToolbar, BorderLayout.PAGE_START);

        // Add center
        this.updateType();

        // Add footer (console)
        viewConsole = new ConsoleView();
        this.add(viewConsole, BorderLayout.PAGE_END);

        // Package, set visible, move to center of screen
        this.setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }

    /**
     * Update the AppView with the correct UI (either client or server)
     */
    private void updateType() {
        if(viewMenu != null) {
            this.remove(viewMenu);
            viewMenu = null;
        }

        if(_type == AppView.TYPE_CLIENT) {
            Log.i("Application initializing as a Client", AppView.class);
            viewMenu = new ClientView(this);
            this.setTitle("Client - SER516 Project Two: Team 4");
        } else if(_type == AppView.TYPE_SERVER) {
            Log.i("Application initializing as a Server", AppView.class);
            viewMenu = new ServerView(this);
            this.setTitle("Server - SER516 Project Two: Team 4");
        }

        this.add(viewMenu, BorderLayout.CENTER);
        this.repaint();
        this.revalidate();
    }

    /**
     * AppToolbar, the toolbar with the start/stop button
     */
    private class AppToolbar extends JToolBar {
        private JButton buttonToggle;

        public AppToolbar() {
            this.setBackground(UIStandards.BACKGROUND_BLUE);
            this.setBorder(new EmptyBorder(8, 8, 8, 8));
            this.setFloatable(false);
            this.add(Box.createHorizontalGlue());

            ServerModel.get().addListener(new ServerListener() {
                @Override
                public void started() {
                    if(AppView.this.isServer())
                        buttonToggle.setText("Stop");
                }

                @Override
                public void shutdown() {
                    if(AppView.this.isServer())
                        buttonToggle.setText("Start");
                }
            });

            ClientModel.get().addListener(new ClientListener() {
                @Override
                public void inputChanged(Integer newest, Integer min, Integer max, Integer avg) {}

                @Override
                public void started() {
                    if(AppView.this.isClient())
                        buttonToggle.setText("Stop");
                }

                @Override
                public void shutdown() {
                    if(AppView.this.isClient())
                        buttonToggle.setText("Start");
                }
            });

            buttonToggle = new JButton("Start");
            buttonToggle.setFont(UIStandards.DEFAULT_FONT);
            buttonToggle.setBackground(UIStandards.BACKGROUND_PINK);
            buttonToggle.setFocusPainted(false);
            buttonToggle.addActionListener(e -> {
                if(AppView.this.isServer()) {
                    if (ServerModel.get().isRunning())
                        ServerModel.get().shutdown();
                    else
                        ServerModel.get().start();
                } else {
                    if(ClientModel.get().isRunning())
                        ClientModel.get().shutdown();
                    else
                        ClientModel.get().start();
                }
            });
            this.add(buttonToggle);
        }
    }

    public boolean isClient() {
        return _type == TYPE_CLIENT;
    }

    public boolean isServer() {
        return  _type == TYPE_SERVER;
    }
}