package net.chetch.cmengineroom.models;

import net.chetch.cmengineroom.data.Pump;
import net.chetch.messaging.Message;
import net.chetch.utilities.SLog;

import androidx.lifecycle.MutableLiveData;

public class PumpDataFilter extends ServiceDataFilter {
    public Pump pump;
    final public MutableLiveData<Pump> liveData = new MutableLiveData<>();

    private EngineRoomMessageSchema schema = new EngineRoomMessageSchema();

    public PumpDataFilter(String pumpID){
        super("AO:Type,AO:ID", "Pump", pumpID);
        pump = new Pump(pumpID);
    }


    @Override
    protected void onMatched(Message message) {
        SLog.i("PF", "Message received");

        //assign message values
        schema.message = message;
        schema.assignPumpData(pump);


        //notify listeners
        liveData.postValue(pump);
    }
}
