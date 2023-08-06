package net.chetch.engineroom2.data;

import android.view.ViewStub;

import net.chetch.engineroom2.R;
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
