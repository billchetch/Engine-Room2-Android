package net.chetch.engineroom2.data;

import android.view.ViewStub;

import net.chetch.engineroom2.models.ServiceDataFilter;
import net.chetch.messaging.Message;
import net.chetch.utilities.Utils;
import net.chetch.webservices.Webservice;

import java.util.Calendar;

import androidx.lifecycle.MutableLiveData;

public class Engine {

    public enum OilPressureState
    {
        OK_ENGINE_ON,
        OK_ENGINE_OFF,
        NO_PRESSURE,
        SENSOR_FAULT
    }

    public String id;
    public int rpm;
    public double temp;
    public OilPressureState oil;

    public Calendar lastOn;
    public Calendar lastOff;

    public boolean running;
    public long runningFor;
    public long ranFor;
    public long stoppedRunningFor;

    public Engine(String id){
        this.id = id;
    }

    public String getSummary(){
        String summary;
        String df = "dd/MM/yy HH:mm:ss";
        if(isRunning()){
            summary = "RPM is " + rpm + ", Temp is " + temp + ", oil state " + oil.toString();
        } else {
            if (lastOn != null) {
                summary = "Last on @ " + Utils.formatDate(lastOn, df) + " and ran for " + Utils.formatDuration(ranFor * 1000, Utils.DurationFormat.DAYS_HOURS_MINS_SECS);
            } else {
                summary = "Has not yet run...";
            }
        }
        return summary;
    }

    public boolean isRunning(){
        return running;
    }

}
