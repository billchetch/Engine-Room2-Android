package net.chetch.engineroom2.data;

import net.chetch.utilities.Utils;

import java.util.Calendar;

public class Pump extends AOData{

    public enum Event{
        PUMP_ON,
        PUMP_OFF,
    }

    public Calendar lastOn;
    public Calendar lastOff;

    private boolean on = false;
    public long runningFor;
    public long ranFor;

    public Pump(String id){ super(id); }

    public boolean isOn(){ return on; }

    public void setOn(boolean on){
        boolean oldOn = this.on;
        this.on = on;
        if(oldOn != this.on) {
            notifyObservers();
        }
    }

    public String getSummary(){
        String summary;
        String df = "dd/MM/yy HH:mm:ss";
        if(isOn()){
            summary = "Pumping for " + Utils.formatDuration(runningFor * 1000, Utils.DurationFormat.D0H0M0S0) ;
        } else {
            if (lastOn != null) {
                summary = "Last pumped on @ " + Utils.formatDate(lastOn, df) + " for " + Utils.formatDuration(ranFor * 1000, Utils.DurationFormat.D0H0M0S0);
            } else {
                summary = "Has not yet been used...";
            }
        }
        return summary;
    }
}
