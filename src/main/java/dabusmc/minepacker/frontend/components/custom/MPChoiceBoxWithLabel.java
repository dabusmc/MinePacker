package dabusmc.minepacker.frontend.components.custom;

import dabusmc.minepacker.frontend.components.MPChoiceBox;
import dabusmc.minepacker.frontend.components.MPHBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public class MPChoiceBoxWithLabel extends MPHBox {

    private MPChoiceBox<String> m_Box;
    private Label m_Label;
    private ObservableList<String> m_Choices;

    private Consumer<String> m_OnChanged;

    public MPChoiceBoxWithLabel(double width, String labelText, ObservableList<String> list) {
        super(15.0);

        m_OnChanged = null;

        m_Choices = list;

        setBoxWidth(width);
        setAlignment(Pos.TOP_LEFT);
        setPadding(new Insets(15.0f, 7.5f, 7.5f, 7.5f));

        m_Label = new Label(labelText);
        m_Box = new MPChoiceBox<>(list);
        m_Box.setValue(list.getFirst());
        m_Box.setBoxWidth(width * 0.5);

        m_Box.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if(m_OnChanged != null) {
                    m_OnChanged.accept(newValue);
                }
            }
        });

        getChildren().addAll(m_Label, m_Box);
    }

    public void setOnChanged(Consumer<String> onChanged) {
        m_OnChanged = onChanged;
    }

    public void setSelection(int index) {
        m_Box.setValue(m_Choices.get(index));
    }

}
