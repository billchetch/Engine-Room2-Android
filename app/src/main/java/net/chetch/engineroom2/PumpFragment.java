package net.chetch.engineroom2;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.chetch.appframework.controls.ExpandIconFragment;
import net.chetch.appframework.controls.IExpandIconListener;
import net.chetch.engineroom2.data.Engine;
import net.chetch.engineroom2.data.Pump;
import net.chetch.engineroom2.models.EngineRoomMessagingModel;
import net.chetch.utilities.SLog;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

public class PumpFragment extends Fragment {
    public String pumpName = "Pump";

    View contentView;
    IndicatorFragment title;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.pump_layout, container, false);

        FragmentManager fm = getChildFragmentManager();

        //Indicator and title
        title = (IndicatorFragment)fm.findFragmentById(R.id.pumpTitle);
        title.setName(pumpName);
        title.update(IndicatorFragment.State.OFF, "Waiting to connect...");

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

        //Listen to data coming in...
        EngineRoomMessagingModel model = ViewModelProviders.of(getActivity()).get(EngineRoomMessagingModel.class);
        String idName = getResources().getResourceEntryName(getId());
        LiveData<Pump> ld = model.getPump(idName);
        if(ld != null) {
            ld.observe(getViewLifecycleOwner(), (pump) -> {
                updatePump(pump);
            });
        }

        return contentView;
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        try {
            TypedArray a = getActivity().obtainStyledAttributes(attrs, R.styleable.PumpFragment);
            pumpName = a.getString(R.styleable.PumpFragment_pump_name);
            a.recycle();

        } catch (Exception e){
            Log.e("IndicatorFragment", e.getMessage());
        }
    }

    private void updatePump(Pump pump) {
        SLog.i("PF", "Updating pump " + pump.id + " dong...");

        if(pump.isOn()){
            title.update(IndicatorFragment.State.ON, pump.getSummary());
        } else {
            title.update(IndicatorFragment.State.OFF, pump.getSummary());
        }
    }
}
