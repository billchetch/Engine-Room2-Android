package net.chetch.cmengineroom.models;

import net.chetch.cmengineroom.data.Engine;
import net.chetch.messaging.Message;
import net.chetch.utilities.SLog;

import androidx.lifecycle.MutableLiveData;

public class EngineDataFilter extends ServiceDataFilter{
    public Engine engine;
    final public MutableLiveData<Engine> liveData = new MutableLiveData<>();

    private EngineRoomMessageSchema schema = new EngineRoomMessageSchema();

    public EngineDataFilter(String engineID){
        super("AO:Type,AO:ID", "Engine", engineID);
        engine = new Engine(engineID);
    }


    @Override
    protected void onMatched(Message message) {
        SLog.i("EF", "Message received");

        //assign message values
        schema.message = message;
        schema.assignEngineData(engine);

        //notify listeners
        liveData.postValue(engine);
    }
}
