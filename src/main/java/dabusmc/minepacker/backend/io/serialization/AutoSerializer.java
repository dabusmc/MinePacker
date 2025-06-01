package dabusmc.minepacker.backend.io.serialization;

import java.util.ArrayList;
import java.util.List;

// TODO: Implement interval auto saving with new thread
public class AutoSerializer {

    private List<AutoSaveable> m_BoundSaveableObjects;

    public AutoSerializer() {
        m_BoundSaveableObjects = new ArrayList<>();
    }

    public void register(AutoSaveable saveable) {
        m_BoundSaveableObjects.add(saveable);
    }

    public void start() {
        for(AutoSaveable saveable : m_BoundSaveableObjects) {
            Serializer.load(saveable);
        }
    }

    public void stop() {
        for(AutoSaveable saveable : m_BoundSaveableObjects) {
            Serializer.save(saveable);
        }
    }

}
