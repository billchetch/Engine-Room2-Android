package net.chetch.engineroom2.models;

import android.util.Log;

import net.chetch.engineroom2.data.Engine;
import net.chetch.messaging.Message;
import net.chetch.messaging.MessagingViewModel;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EngineRoomMessagingModel extends MessagingViewModel {
    Map<String, EngineDataFilter> engineDataFilters = new HashMap<>();

    public EngineRoomMessagingModel(){
        super();

        permissableServerTimeDifference = 60 * 5;

        try {
            engineDataFilters.put(EngineRoomMessageSchema.GS1_ID, new EngineDataFilter(EngineRoomMessageSchema.GS1_ID));

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
        return engineDataFilters.get(id).liveData;
    }
}
