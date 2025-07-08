package dabusmc.minepacker.backend.io.serialization;

import dabusmc.minepacker.frontend.popups.ProgressBarPopup;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ThreadedFunctionWithProgressBar<T> {

    private ProgressBarPopup m_Bar;
    private Task<T> m_Task;
    private List<Consumer<T>> m_OnFinish;

    public ThreadedFunctionWithProgressBar(Task<T> task) {
        m_OnFinish = new ArrayList<>();

        m_Bar = new ProgressBarPopup();
        m_Bar.initComponents();
        m_Bar.setComplete(false);

        m_Task = task;
        m_Task.setOnFailed(wse -> wse.getSource().getException().printStackTrace());
        m_Task.setOnSucceeded(wse -> {
            m_Bar.getBar().progressProperty().unbind();
            m_Bar.getLabel().textProperty().unbind();
            m_Bar.setComplete(true);
            m_Bar.reload();
            m_Bar.getLabel().setText("Complete!");
            m_Bar.getBar().setProgress(100.0);

            if(m_OnFinish.size() != 0) {
                for(Consumer<T> func : m_OnFinish) {
                    func.accept((T) wse.getSource().getValue());
                }
            }
        });

        m_Bar.getBar().progressProperty().bind(m_Task.progressProperty());
        m_Bar.getLabel().textProperty().bind(m_Task.messageProperty());
    }

    public void addOnFinish(Consumer<T> event) {
        m_OnFinish.add(event);
    }

    public void begin() {
        new Thread(m_Task).start();
        m_Bar.display();
    }

    public void finish() {
        m_Bar.finish();
    }

}
