package dabusmc.minepacker.backend.util;

import java.util.ArrayList;
import java.util.List;

public class ListPair<T> {

    private List<T> m_First;
    private List<T> m_Second;

    public ListPair() {
        m_First = new ArrayList<>();
        m_Second = new ArrayList<>();
    }

    public List<T> getFirst() {
        return m_First;
    }

    public List<T> getSecond() {
        return m_Second;
    }

    public void setFirst(List<T> first) {
        m_First = first;
    }

    public void setSecond(List<T> second) {
        m_Second = second;
    }

    public void addToFirst(T val) {
        m_First.add(val);
    }

    public void addToSecond(T val) {
        m_First.add(val);
    }

}
