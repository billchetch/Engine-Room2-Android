package net.chetch.cmengineroom.models;

import net.chetch.cmengineroom.data.AOData;
import net.chetch.cmengineroom.data.Engine;
import net.chetch.cmengineroom.data.Pump;
import net.chetch.messaging.MessageSchema;
import net.chetch.utilities.SLog;

import java.util.Map;

public class EngineRoomMessageSchema extends MessageSchema {

    static public final String SERVICE_NAME = "BBEngineRoom";
    static public final String AO_PREFIX = "AO:";
    static public final String INDUK_ID = "idk";
    static public final String BANTU_ID = "bnt";
    static public final String GS1_ID = "gs1";
    static public final String GS2_ID = "gs2";
    static public final String POMPA_CELUP_ID = "pmp-clp";
    static public final String POMPA_SOLAR_ID = "pmp-sol";

    static public String statusCommand(String aoid){
        return "adm:" + aoid + ":status";
    }
    static public String enableCommand(String aoid){
        return "adm:" + aoid + ":enable";
    }


    private String fn(String fieldName){
        return AO_PREFIX + fieldName;
    }

    private void assignAOData(AOData aodata){
        aodata.enable(message.getBoolean(fn("Enabled")));
        SLog.i("ERMS", "Setting enabled for " + message.getString(fn("UID")) + " to: " + message.getBoolean(fn("Enabled")));
    }

    public void assignEngineData(Engine engine){
        assignAOData(engine);

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
        assignAOData(pump);

        pump.setOn(message.getBoolean(fn("Position")));
        pump.startedOn = message.getCalendar(fn("StartedOn"));
        pump.stoppedOn = message.getCalendar(fn("StoppedOn"));
        Map<String, Double> m = message.getMap(fn("RunningFor"), Double.class);
        pump.runningFor = m.get("TotalSeconds").intValue();
        m = message.getMap(fn("RanFor"), Double.class);
        pump.ranFor = m.get("TotalSeconds").intValue();
    }
}
