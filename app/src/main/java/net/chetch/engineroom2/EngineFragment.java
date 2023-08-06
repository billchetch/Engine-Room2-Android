package net.chetch.engineroom2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.chetch.engineroom2.models.EngineRoomMessageSchema;
import net.chetch.engineroom2.models.EngineRoomMessagingModel;
import net.chetch.engineroom2.data.Engine;
import net.chetch.utilities.SLog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStoreOwner;

public class EngineFragment extends Fragment {
    View contentView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.engine_layout, container, false);

        EngineRoomMessagingModel model = ViewModelProviders.of(getActivity()).get(EngineRoomMessagingModel.class);
        LiveData<Engine> ld = model.getEngine(EngineRoomMessageSchema.GS1_ID);
        ld.observe(getViewLifecycleOwner(), (engine)->{
            updateEngine(engine);
        });

        return contentView;
    }

    private void updateEngine(Engine engine){
        SLog.i("EF", "Updating engine dong...");

        TextView tv = contentView.findViewById(R.id.textView);
        tv.setText("Engine " + engine.id + ": RPM " + engine.rpm + ", Temp "  + engine.temp);
    }

}
