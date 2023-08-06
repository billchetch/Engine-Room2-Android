package net.chetch.engineroom2;

import android.os.Bundle;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;

import net.chetch.appframework.GenericActivity;
import net.chetch.appframework.IDialogManager;
import net.chetch.appframework.NotificationBar;
import net.chetch.engineroom2.models.EngineRoomMessageSchema;
import net.chetch.engineroom2.models.EngineRoomMessagingModel;
import net.chetch.messaging.ClientConnection;
import net.chetch.messaging.MessagingViewModel;
import net.chetch.messaging.exceptions.MessagingServiceException;
import net.chetch.utilities.Logger;
import net.chetch.utilities.SLog;
import net.chetch.utilities.Utils;
import net.chetch.webservices.ConnectManager;
import net.chetch.webservices.Webservice;
import net.chetch.webservices.WebserviceViewModel;


public class MainActivity extends GenericActivity implements NotificationBar.INotifiable{

    static boolean connected = false;
    static boolean suppressConnectionErrors = false;


    ConnectManager connectManager = new ConnectManager();

    EngineRoomMessagingModel model;

    Observer connectProgress  = obj -> {
        showProgress();
        if(obj instanceof WebserviceViewModel.LoadProgress) {
            WebserviceViewModel.LoadProgress progress = (WebserviceViewModel.LoadProgress) obj;
            try {
                String state = progress.startedLoading ? "Loading" : "Loaded";
                String progressInfo = state + (progress.info == null ? "" : " " + progress.info.toLowerCase());
                /*if(progress.dataLoaded != null){
                    progressInfo += " - " + progress.dataLoaded.getClass().toString();
                }*/
                setProgressInfo(progressInfo);
                Log.i("Main", "in load data progress ..." + progressInfo);

            } catch (Exception e) {
                Log.e("Main", "load progress: " + e.getMessage());
            }
        } else if(obj instanceof ClientConnection){

        } else if(obj instanceof ConnectManager) {
            ConnectManager cm = (ConnectManager) obj;
            switch(cm.getState()){
                case CONNECT_REQUEST:
                    if(cm.fromError()){
                        setProgressInfo("There was an error ... retrying...");
                    } else {
                        setProgressInfo("Connecting...");
                    }
                    break;

                case RECONNECT_REQUEST:
                    setProgressInfo("Disconnected!... Attempting to reconnect...");
                    break;

                case CONNECTED:
                    hideProgress();
                    Log.i("Main", "All connections made");
                    connected = true;
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        includeActionBar(SettingsActivity.class);

        model = ViewModelProviders.of(this).get(EngineRoomMessagingModel.class);
        model.getError().observe(this, throwable -> {
            try {
                handleError(throwable, model);
            } catch (Exception e){
                SLog.e("Main", e.getMessage());
            }
        });

        try {
            Logger.info("Main activity sstting cm client name, adding modules and requesting connect ...");
            model.setClientName("ACMCAPEngineRoom", getApplicationContext());

            connectManager.addModel(model);

            connectManager.setPermissableServerTimeDifference(5 * 60);

            connectManager.requestConnect(connectProgress);

            NotificationBar.setView(findViewById(R.id.notificationbar), 100);
            NotificationBar.monitor(this, connectManager, "connection");
        } catch (Exception e){
            showError(e);
        }
    }

    private String getStackTrace(Throwable t){
        String stackTrace = "";
        StackTraceElement[] st = t.getStackTrace();
        for(StackTraceElement ste : st){
            String s = ste.getFileName() + " @ " + ste.getLineNumber() + " in " + ste.getMethodName();
            stackTrace += s + "\n";
        }
        return stackTrace;
    }

    private void handleError(Throwable t, Object source){
        String errMsg = "SCE: " + suppressConnectionErrors + ", CNCT: " + connected + ", ICE: " + ConnectManager.isConnectionError(t);
        errMsg += "\n" + t.getClass().getName() + "\n" + t.getMessage() + "\n" + t.getCause() + "\n" + getStackTrace(t);

        showError(errMsg);

        SLog.e("MAIN", t.getClass() + ": " + t.getMessage());
    }

    @Override
    public void openAbout() {
        super.openAbout();
        try {
            String lf = "\n";
            ERApplication app = (ERApplication)getApplication();
            ClientConnection client = model.getClient();
            String s = "";
            s += "App uptime: " + Utils.formatDuration(app.getUpTime(), Utils.DurationFormat.DAYS_HOURS_MINS_SECS) + lf;
            s += client.getName() + " is of state " + client.getState() + lf;
            MessagingViewModel.MessagingService bbalarms = model.getMessaingService(EngineRoomMessageSchema.SERVICE_NAME);
            s += bbalarms.name + " service is of state " + bbalarms.state + lf;
            s += "Last message received on: " + Utils.formatDate(bbalarms.lastMessageReceivedOn, Webservice.DEFAULT_DATE_FORMAT);
            aboutDialog.aboutBlurb = s;

        } catch (Exception e){

        }
    }

    @Override
    public void handleNotification(Object notifier, String tag) {
        if(notifier instanceof ConnectManager){
            ConnectManager cm = (ConnectManager)notifier;
            switch(cm.getState()){
                case CONNECTED:
                    NotificationBar.show(NotificationBar.NotificationType.INFO, "Connected and ready to use.", null,5);
                    break;

                case ERROR:
                    NotificationBar.show(NotificationBar.NotificationType.ERROR, "Service unavailable.");
                    break;

                case RECONNECT_REQUEST:
                    NotificationBar.show(NotificationBar.NotificationType.WARNING, "Attempting to reconnect...");
                    break;
            }
        }
    }
}