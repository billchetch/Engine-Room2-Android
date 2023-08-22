package net.chetch.engineroom2.data;

import java.util.Observable;

public class AOData extends Observable {
    public String id;
    public boolean enabled;

    public AOData(String id){ this.id = id; }

    @Override
    public void notifyObservers(Object o){
        setChanged();
        super.notifyObservers(o);
    }
}
