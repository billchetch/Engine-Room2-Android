package net.chetch.cmengineroom.data;

import java.util.Observable;

public class AOData extends Observable {
    public enum Event{
        ENABLED,
        DISABLED,
    }

    public String id;
    public boolean enabled;

    public AOData(String id){ this.id = id; }

    @Override
    public void notifyObservers(Object o){
        setChanged();
        super.notifyObservers(o);
    }

    public void enable(boolean enabled){
        boolean oldValue = this.enabled;
        this.enabled = enabled;
        if(oldValue != this.enabled) {
            //notifyObservers(this.enabled ? Event.ENABLED : Event.DISABLED);
        }
    }

    public boolean isEnabled(){ return enabled; }
}
