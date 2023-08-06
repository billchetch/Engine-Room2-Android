package net.chetch.engineroom2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.chetch.appframework.controls.ExpandIconFragment;
import net.chetch.appframework.controls.IExpandIconListener;
import net.chetch.engineroom2.models.EngineRoomMessageSchema;
import net.chetch.engineroom2.models.EngineRoomMessagingModel;
import net.chetch.engineroom2.data.Engine;
import net.chetch.utilities.SLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;

public class EngineFragment extends Fragment implements IExpandIconListener {
    public String name = "Engine";

    View contentView;
    IndicatorFragment title;
    ExpandIconFragment expandDetails;
    View detailsView;
    LinearScaleFragment rpm;
    LinearScaleFragment temperature;
    IndicatorFragment oil;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.engine_layout, container, false);

        FragmentManager fm = getChildFragmentManager();
        Object tag = getTag();

        //Indicator and title
        title = (IndicatorFragment)fm.findFragmentById(R.id.engineTitle);
        title.setName(tag.toString());
        title.update(IndicatorFragment.State.OFF, "Connecting...");
        title.getView().setOnClickListener((view)->{
            expandDetails.onClick(null);
        });

        MenuItem.OnMenuItemClickListener selectMenuItem = (item) -> {
            switch(item.getItemId()){
                case IndicatorFragment.MENU_ITEM_DISABLE:
                    return true;
                case IndicatorFragment.MENU_ITEM_ENABLE:
                    return true;
                case IndicatorFragment.MENU_ITEM_VIEW_STATS:
                    return true;
            }
            return true;
        };

        title.setContextMenu(selectMenuItem);

        //Expand Icon
        expandDetails = (ExpandIconFragment)fm.findFragmentById(R.id.expandDetails);
        expandDetails.setListener(this);
        detailsView = contentView.findViewById(R.id.engineDetails);

        //RPM
        rpm = (LinearScaleFragment)fm.findFragmentById(R.id.rpm);
        rpm.setLimits(0, 2000);
        rpm.setName("RPM");
        rpm.setThresholdColours(
                ContextCompat.getColor(getContext(), R.color.bluegreen2),
                ContextCompat.getColor(getContext(), R.color.bluegreen),
                ContextCompat.getColor(getContext(), R.color.age2),
                ContextCompat.getColor(getContext(), R.color.age4));
        rpm.setThresholdValues(1620, 1750, 2000);

        //Temperature
        temperature = (LinearScaleFragment)fm.findFragmentById(R.id.temp);
        temperature.setLimits(0, 60);
        temperature.setName("Temp");
        temperature.setThresholdColours(
                ContextCompat.getColor(getContext(), R.color.bluegreen2),
                ContextCompat.getColor(getContext(), R.color.age2),
                ContextCompat.getColor(getContext(), R.color.age4));
        temperature.setThresholdValues(40, 45);

        //Oil
        oil  = (IndicatorFragment) fm.findFragmentById(R.id.oil);
        oil.setName("Oil");
        oil.update(IndicatorFragment.State.OFF, "");

        //Listen to data coming in...
        EngineRoomMessagingModel model = ViewModelProviders.of(getActivity()).get(EngineRoomMessagingModel.class);
        String idName = getResources().getResourceEntryName(getId());
        LiveData<Engine> ld = model.getEngine(idName);
        if(ld != null) {
            ld.observe(getViewLifecycleOwner(), (engine) -> {
                updateEngine(engine);
            });
        }

        return contentView;
    }

    private void updateEngine(Engine engine){
        SLog.i("EF", "Updating engine dong...");

        if(engine.isRunning()){
            title.update(IndicatorFragment.State.ON, engine.getSummary());
        } else {
            title.update(IndicatorFragment.State.OFF, engine.getSummary());
        }



        temperature.updateValue(engine.temp);
    }

    @Override
    public void onExpand() {
        detailsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onContract() {
        detailsView.setVisibility(View.GONE);
    }
}
