package net.chetch.engineroom2.models;

import net.chetch.engineroom2.data.Engine;
import net.chetch.engineroom2.data.Pump;
import net.chetch.messaging.MessageSchema;

import java.util.HashMap;
import java.util.Map;

public class EngineRoomMessageSchema extends MessageSchema {

    static public final String SERVICE_NAME = "BBEngineRoom";
    static public final String AO_PREFIX = "AO:";
    static public final String INDUK_ID = "induk";
    static public final String BANTU_ID = "bantu";
    static public final String GS1_ID = "gs1";
    static public final String GS2_ID = "gs2";
    static public final String POMPA_CELUP_ID = "pmp-clp";
    static public final String POMPA_SOLAR_ID = "pmp-sol";

    static public String statusCommand(String aoid){
        return "adm:" + aoid + ":status";
    }

    private String fn(String fieldName){
        return AO_PREFIX + fieldName;
    }

    public void assignEngineData(Engine engine){
        engine.setRunning(message.getBoolean(fn("Running")));
        engine.runningFor = message.getLong(fn("RunningFor"));
        engine.ranFor = message.getLong(fn("RanFor"));
        engine.rpm = message.getInt(fn("RPM"));
        engine.temp = message.getDouble(fn("Temp"));
        engine.oil = message.getEnum(fn("OilPressure"), Engine.OilPressureState.class);
        engine.lastOn = message.getCalendar(fn("LastOn"));
        engine.lastOff = message.getCalendar(fn("LastOff"));

    }

    public void assignPumpData(Pump pump){
        pump.setOn(message.getBoolean(fn("Position")));
        pump.lastOn = message.getCalendar(fn("LastOn"));
        pump.lastOff = message.getCalendar(fn("LastOff"));
        Map<String, Double> m = message.getMap(fn("RunningFor"), Double.class);
        pump.runningFor = m.get("TotalSeconds").intValue();
        m = message.getMap(fn("RanFor"), Double.class);
        pump.ranFor = m.get("TotalSeconds").intValue();
    }
}
