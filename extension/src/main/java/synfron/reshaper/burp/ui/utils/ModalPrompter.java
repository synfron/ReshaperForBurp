package synfron.reshaper.burp.ui.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import synfron.reshaper.burp.ui.components.shared.FormPrompt;

import javax.swing.*;
import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModalPrompter<T extends IPrompterModel<T>> {
    private static final Map<String, WeakReference<JDialog>> dialogMap = new ConcurrentHashMap<>();

    public static <T extends IPrompterModel<T>> void open(T model, FormPromptArgs<T> formPromptArgs) {
        FormPrompt<T> dialog = new FormPrompt<>(formPromptArgs.title, model, formPromptArgs.formComponent);

        dialog.setResizable(formPromptArgs.resizable);

        Point centerLocation = getCenterLocation(formPromptArgs);
        if (centerLocation != null) {
            dialog.setLocation(
                    centerLocation.x - dialog.getPreferredSize().width / 2,
                    centerLocation.y - dialog.getPreferredSize().height / 2
            );
        }

        if (formPromptArgs.size != null) {
            dialog.setSize(formPromptArgs.size);
        }

        if (formPromptArgs.dataBag != null) {
            dialog.putClientProperty("dataBag", formPromptArgs.dataBag);
        }

        if (formPromptArgs.registrationId != null) {
            dialogMap.put(formPromptArgs.registrationId, new WeakReference<>(dialog));
        }

        dialog.setVisible(true);
    }

    private static <T extends IPrompterModel<T>> Point getCenterLocation(FormPromptArgs<T> formPromptArgs) {
        Point centerLocation = null;
        if (formPromptArgs.locationRelativeTo != null) {
            Rectangle bounds = formPromptArgs.locationRelativeTo.getBounds();
            Point location = bounds.getLocation();
            centerLocation = new Point(
                    location.x + bounds.width / 2,
                    location.y + bounds.height / 2
            );
        } else {
            Point screenCenterLocation = getScreenCenterLocation();
            if (screenCenterLocation != null) {
                centerLocation = screenCenterLocation;
            }
        }
        return centerLocation;
    }

    private static Point getScreenCenterLocation() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point mouseLocation = pointerInfo.getLocation();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        for (GraphicsDevice screen : screens) {
            GraphicsConfiguration gc = screen.getDefaultConfiguration();
            Rectangle bounds = gc.getBounds();
            if (bounds.contains(mouseLocation)) {
                Point location = bounds.getLocation();
                return new Point(
                        location.x + bounds.width / 2,
                        location.y + bounds.height / 2
                );
            }
        }
        return null;
    }

    public static void dismiss(String id) {
        WeakReference<JDialog> dialogRef = dialogMap.get(id);
        if (dialogRef != null) {
            JDialog dialog = dialogRef.get();
            if (dialog != null) {
                dialog.dispose();
            }
            dialogMap.remove(id);
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor
    public static class FormPromptArgs<T extends IPrompterModel<T>> {
        private final String title;
        private final T model;
        private final Component formComponent;
        private boolean resizable;
        private Dimension size;
        private Component locationRelativeTo;
        private String registrationId;
        private Object dataBag;
    }
}
