package net.chetch.engineroom2;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import net.chetch.appframework.GenericActivity;
import net.chetch.appframework.IDialogManager;
import net.chetch.appframework.NotificationBar;
import net.chetch.cmengineroom.data.Pump;
import net.chetch.cmengineroom.models.EngineRoomMessageSchema;
import net.chetch.cmengineroom.models.EngineRoomMessagingModel;
import net.chetch.cmengineroom.data.Engine;
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
    static ConnectManager connectManager = new ConnectManager();

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

    //ViewPager2 viewPager;
    //TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        includeActionBar(SettingsActivity.class);


        MainViewPagerAdapter mainViewPagerAdapater = new MainViewPagerAdapter(this);
        ViewPager2 viewPager = findViewById(R.id.mainViewPager);
        viewPager.setAdapter(mainViewPagerAdapater);

        TabLayout tabLayout = findViewById(R.id.mainTabLayout);
        String[] labels = new String[]{"Engines", "Pumps"};
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(labels[position]));
        tabLayoutMediator.attach();

        if(!connectManager.isConnected()) {
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
                NotificationBar.monitor(this, model.dataEvent, "engine room data event");

            } catch (Exception e) {
                showError(e);
            }
        } else {
            //already connected so ensure things are hidden that might otherwise be displayed by default
            hideProgress();
            NotificationBar.hide();
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
        if (suppressConnectionErrors && connected && (ConnectManager.isConnectionError(t) || t instanceof MessagingServiceException)) {
            final String errMsg = t.getClass().getName() + "\n" + t.getMessage() + "\n" + t.getCause() + "\n" + getStackTrace(t);
            SLog.e("MAIN", "Suppressed connection error: " + errMsg);
            Logger.error("Suppressed connection error: " + errMsg);
            NotificationBar.show(NotificationBar.NotificationType.ERROR,
                    "An exception has occurred ...click for more details",
                    t).setListener(new NotificationBar.INotificationListener() {
                @Override
                public void onClick(NotificationBar nb, NotificationBar.NotificationType ntype) {
                    showError(errMsg);
                }
            });
            return;
        }

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
    public void handleNotification(Object notifier, String tag, Object data) {
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
        } else if(notifier instanceof LiveData){
            LiveData<EngineRoomMessagingModel.DataEvent> ld = (LiveData)notifier;
            EngineRoomMessagingModel.DataEvent dataEvent = ld.getValue();
            if(dataEvent.source instanceof Engine){
                Engine engine = (Engine)dataEvent.source;
                int rid = getStringResource(engine.id.replace('-', '_'));
                String engineName = getString(rid);
                switch((Engine.Event)dataEvent.data){
                    case ENGINE_ON:
                        NotificationBar.show(NotificationBar.NotificationType.INFO, engineName + " is running", null,5);
                        break;

                    case ENGINE_OFF:
                        NotificationBar.show(NotificationBar.NotificationType.INFO, engineName + " has stopped running", null,5);
                        break;
                }
            } else if(dataEvent.source instanceof Pump){
                Pump pump = (Pump)dataEvent.source;
                int rid = getStringResource(pump.id.replace('-', '_'));
                String pumpName = getString(rid);
                switch((Pump.Event)dataEvent.data){
                    case PUMP_ON:
                        NotificationBar.show(NotificationBar.NotificationType.INFO, pumpName + " is pumping", null,5);
                        break;

                    case PUMP_OFF:
                        NotificationBar.show(NotificationBar.NotificationType.INFO, pumpName + " has stopped pumping", null,5);
                        break;
                }
            }
        }
    }
}