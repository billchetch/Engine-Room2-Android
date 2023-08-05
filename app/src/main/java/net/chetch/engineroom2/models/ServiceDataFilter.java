package net.chetch.engineroom2.models;

import net.chetch.messaging.filters.DataFilter;

abstract public class ServiceDataFilter extends DataFilter {
    public ServiceDataFilter(String requiredFields, Object ... requiredValues){
        super(EngineRoomMessageSchema.SERVICE_NAME, requiredFields, requiredValues);
    }
}
