package net.chetch.engineroom2.models;

import android.util.Log;

import net.chetch.engineroom2.data.Engine;
import net.chetch.messaging.Message;
import net.chetch.utilities.SLog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EngineDataFilter extends ServiceDataFilter{
    Engine engine;
    final public MutableLiveData<Engine> liveData = new MutableLiveData<>();

    public EngineDataFilter(String engineID){
        super("AO:Type,AO:ID", "Engine", engineID);
        engine = new Engine(engineID);
    }


    @Override
    protected void onMatched(Message message) {
        SLog.i("EF", "Message received");
        liveData.postValue(null);
        liveData.postValue(engine);
    }
}
