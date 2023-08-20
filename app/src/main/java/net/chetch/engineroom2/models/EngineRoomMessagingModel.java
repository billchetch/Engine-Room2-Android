package net.chetch.engineroom2.models;

import android.util.Log;

import net.chetch.engineroom2.data.Engine;
import net.chetch.messaging.Message;
import net.chetch.messaging.MessagingViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EngineRoomMessagingModel extends MessagingViewModel {
    Map<String, EngineDataFilter> engineDataFilters = new HashMap<>();

    public EngineRoomMessagingModel(){
        super();

        permissableServerTimeDifference = 60 * 5;

        try {
            //Engines to listen to
            engineDataFilters.put(EngineRoomMessageSchema.GS1_ID, new EngineDataFilter(EngineRoomMessageSchema.GS1_ID));
            engineDataFilters.put(EngineRoomMessageSchema.GS2_ID, new EngineDataFilter(EngineRoomMessageSchema.GS2_ID));

            for(EngineDataFilter f : engineDataFilters.values()) {
                addMessageFilter(f);
            }

        } catch (Exception e){
            Log.e("ERMM", e.getMessage());
        }

    }

    @Override
    public void onClientConnected() {
        super.onClientConnected();
        Log.i("ERMM", "Client connected");
    }


    public MutableLiveData<Engine> getEngine(String id){
        return engineDataFilters.containsKey(id) ? engineDataFilters.get(id).liveData : null;
    }
}
