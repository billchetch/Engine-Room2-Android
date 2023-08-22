package net.chetch.engineroom2.models;

import net.chetch.engineroom2.data.Pump;
import net.chetch.messaging.Message;
import net.chetch.messaging.filters.DataFilter;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

abstract public class ServiceDataFilter extends DataFilter {

    public ServiceDataFilter(String requiredFields, Object ... requiredValues){
        super(EngineRoomMessageSchema.SERVICE_NAME, requiredFields, requiredValues);
    }

}
