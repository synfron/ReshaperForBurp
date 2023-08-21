package synfron.reshaper.burp.ui.utils;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DocumentListenerFinder {

    private final DocumentActionListener actionListener;
    private final Component component;
    private final FinderComponentAdapter componentListener;

    public DocumentListenerFinder(Component component, DocumentActionListener actionListener) {
        this.component = component;
        this.actionListener = actionListener;
        this.componentListener = new FinderComponentAdapter();
        if (!tryAddDocumentListener()) {
            component.addComponentListener(componentListener);
        }
    }
    
    private boolean tryAddDocumentListener() {
        boolean added = false;
        try {
            if (component.getWidth() > 0) {
                Component lastComponent = component;
                while (true) {
                    Component currentComponent = lastComponent.getComponentAt(
                            lastComponent.getWidth() / 2,
                            lastComponent.getHeight() / 2
                    );
                    if (currentComponent == null) {
                        break;
                    }
                    if (currentComponent instanceof JTextComponent textComponent) {
                        textComponent.getDocument().addDocumentListener(actionListener);
                        added = true;
                        break;
                    }
                    if (currentComponent == lastComponent) {
                        break;
                    }
                    lastComponent = currentComponent;
                }
            }
        } catch (Exception ignored) {
        }
        if (added) {
            component.removeComponentListener(componentListener);
        }
        return added;
    }
    
    public class FinderComponentAdapter extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            tryAddDocumentListener();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            tryAddDocumentListener();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            tryAddDocumentListener();
        }
    }
    
}
