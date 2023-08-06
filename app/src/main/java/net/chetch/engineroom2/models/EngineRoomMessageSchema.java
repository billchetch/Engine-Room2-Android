package net.chetch.engineroom2.models;

import net.chetch.engineroom2.data.Engine;
import net.chetch.messaging.MessageSchema;

public class EngineRoomMessageSchema extends MessageSchema {

    static public final String SERVICE_NAME = "BBEngineRoom";
    static public final String AO_PREFIX = "AO:";
    static public final String GS1_ID = "gs1";
    static public final String GS2_ID = "gs2";

    private String fn(String fieldName){
        return AO_PREFIX + fieldName;
    }

    public void assignEngineData(Engine engine){
        engine.running = message.getBoolean(fn("Running"));
        engine.runningFor = message.getLong(fn("RunningFor"));
        engine.ranFor = message.getLong(fn("RanFor"));
        engine.rpm = message.getInt(fn("RPM"));
        engine.temp = message.getDouble(fn("Temp"));
        engine.oil = message.getEnum(fn("OilPressure"), Engine.OilPressureState.class);
        engine.lastOn = message.getCalendar(fn("LastOn"));
        engine.lastOff = message.getCalendar(fn("LastOff"));

    }
}
