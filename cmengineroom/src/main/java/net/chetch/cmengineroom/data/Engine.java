package net.chetch.cmengineroom.data;

import net.chetch.utilities.Utils;

import java.util.Calendar;

public class Engine extends AOData{

    public enum Event{
        ENGINE_ON,
        ENGINE_OFF,
    }

    public enum OilPressureState
    {
        OK_ENGINE_ON,
        OK_ENGINE_OFF,
        NO_PRESSURE,
        SENSOR_FAULT
    }

    public int rpm;
    public double temp;
    public OilPressureState oil;

    public Calendar lastOn;
    public Calendar lastOff;

    private boolean running;
    public long runningFor;
    public long ranFor;
    public long stoppedRunningFor;

    public Engine(String id){
        super(id);
    }

    public void setRunning(boolean running){
        boolean oldRunning = this.running;
        this.running = running;
        if(oldRunning != this.running) {
            notifyObservers(this.running ? Event.ENGINE_ON : Event.ENGINE_OFF);
        }
    }

    public String getSummary(){
        String summary;
        String df = "dd/MM/yy HH:mm:ss";
        String tempSymbol = " \u00B0" + "C";
        if(isRunning()){
            summary = "Running " + Utils.formatDuration(runningFor * 1000, Utils.DurationFormat.D0H0M0S0) ;
            summary += " @ " + rpm + " rpm " + String.format("%.1f", temp) + tempSymbol;
        } else {
            if (lastOn != null) {
                summary = "Last on @ " + Utils.formatDate(lastOn, df) + " and ran for " + Utils.formatDuration(ranFor * 1000, Utils.DurationFormat.D0H0M0S0);
            } else {
                summary = "Has not yet run...";
            }
        }
        return summary;
    }

    public boolean isRunning(){
        return running;
    }

    public boolean oilPressureDetected(){
        switch (oil){
            case NO_PRESSURE:
            case OK_ENGINE_OFF:
                return false;
            case OK_ENGINE_ON:
            case SENSOR_FAULT:
                return true;
        }
        return false;
    }

    public double getHzFromRPM(){
        return (rpm / 60.0) * 2.0;
    }

}
