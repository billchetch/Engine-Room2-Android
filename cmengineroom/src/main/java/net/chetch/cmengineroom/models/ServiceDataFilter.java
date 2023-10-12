package net.chetch.cmengineroom.models;

import net.chetch.messaging.filters.DataFilter;

abstract public class ServiceDataFilter extends DataFilter {

    public ServiceDataFilter(String requiredFields, Object ... requiredValues){
        super(EngineRoomMessagingModel.SERVICE_NAME, requiredFields, requiredValues);
    }

}
