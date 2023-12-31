package net.chetch.cmengineroom.models;

import android.util.Log;

import net.chetch.cmengineroom.data.Engine;
import net.chetch.cmengineroom.data.Pump;
import net.chetch.messaging.ClientConnection;
import net.chetch.messaging.MessagingViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class EngineRoomMessagingModel extends MessagingViewModel implements Observer {
    static public final String SERVICE_NAME = "BBEngineRoom";

    Map<String, EngineDataFilter> engineDataFilters = new HashMap<>();
    Map<String, PumpDataFilter> pumpDataFilters = new HashMap<>();

    public class DataEvent{
        public Observable source;
        public Object data;

        public DataEvent(Observable source, Object data){
            this.source = source;
            this.data = data;
        }
    }
    public MutableLiveData<DataEvent> dataEvent = new MutableLiveData<>();

    public EngineRoomMessagingModel(){
        super();

        permissableServerTimeDifference = 60 * 5;

        try {
            //Engines to listen to
            engineDataFilters.put(EngineRoomMessageSchema.INDUK_ID, new EngineDataFilter(EngineRoomMessageSchema.INDUK_ID));
            engineDataFilters.put(EngineRoomMessageSchema.BANTU_ID, new EngineDataFilter(EngineRoomMessageSchema.BANTU_ID));
            engineDataFilters.put(EngineRoomMessageSchema.GS1_ID, new EngineDataFilter(EngineRoomMessageSchema.GS1_ID));
            engineDataFilters.put(EngineRoomMessageSchema.GS2_ID, new EngineDataFilter(EngineRoomMessageSchema.GS2_ID));

            addMessageFilters(engineDataFilters.values());

            pumpDataFilters.put(EngineRoomMessageSchema.POMPA_CELUP_ID, new PumpDataFilter(EngineRoomMessageSchema.POMPA_CELUP_ID));
            pumpDataFilters.put(EngineRoomMessageSchema.POMPA_SOLAR_ID, new PumpDataFilter(EngineRoomMessageSchema.POMPA_SOLAR_ID));

            addMessageFilters(pumpDataFilters.values());


            for(EngineDataFilter f : engineDataFilters.values()){
                f.engine.addObserver(this);
            }
        } catch (Exception e){
            Log.e("ERMM", e.getMessage());
        }

    }

    @Override
    public void onReady() {
        super.onReady();

        for(EngineDataFilter f : engineDataFilters.values()){
            requestStatus(f.engine.id);
        }
        for(PumpDataFilter f : pumpDataFilters.values()){
            requestStatus(f.pump.id);
        }
    }

    public void requestStatus(String aoid){

    }

    public void enable(String aoid, boolean enable){
        ClientConnection client = getClient();
        if(client != null) {
            client.sendCommand(SERVICE_NAME, EngineRoomMessageSchema.enableCommand(aoid), enable);
        }
    }

    public void enable(String aoid){
        enable(aoid, true);
    }

    public void disable(String aoid){
        enable(aoid, false);
    }

    public MutableLiveData<Engine> getEngine(String id){
        id = EngineRoomMessageSchema.sanitiseID(id);
        return engineDataFilters.containsKey(id) ? engineDataFilters.get(id).liveData : null;
    }

    public MutableLiveData<Pump> getPump(String id){
        id = EngineRoomMessageSchema.sanitiseID(id);
        return pumpDataFilters.containsKey(id) ? pumpDataFilters.get(id).liveData : null;
    }

    @Override
    public void update(Observable observable, Object o) {
        dataEvent.postValue(new DataEvent(observable, o));
    }
}
