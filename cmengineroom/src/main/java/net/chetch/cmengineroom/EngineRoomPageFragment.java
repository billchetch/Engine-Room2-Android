package net.chetch.cmengineroom;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import net.chetch.cmengineroom.models.EngineRoomMessagingModel;
import net.chetch.messaging.MessagingViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

class EngineRoomPageFragment extends Fragment {
   View contentView;
   EngineRoomMessagingModel model;

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      if (model == null) {
         View pageLayout = contentView.findViewById(R.id.pageLayout);
         View progressCtn = contentView.findViewById(R.id.progressCtn);
         TextView progressInfo = contentView.findViewById(R.id.progressInfo);

         pageLayout.setVisibility(View.INVISIBLE);
         progressCtn.setVisibility(View.VISIBLE);
         progressInfo.setText("Connecting ... please wait");

         model = new ViewModelProvider(getActivity()).get(EngineRoomMessagingModel.class);
         model.observeMessagingServices(getViewLifecycleOwner(), ms -> {
            //we assume this is always the alarms messaging service
            switch(ms.state){
               case RESPONDING:
                  if(ms.isReady()) {
                     if (progressCtn != null) progressCtn.setVisibility(View.INVISIBLE);
                     pageLayout.setVisibility(View.VISIBLE);
                  } else {
                     if (progressCtn != null) progressCtn.setVisibility(View.VISIBLE);
                     pageLayout.setVisibility(View.INVISIBLE);
                     progressInfo.setVisibility(View.VISIBLE);
                     if(ms.serviceStatus != null) {
                        progressInfo.setText(ms.serviceStatus);
                     } else {
                        progressInfo.setText("Service is responding waiting for it to become ready...");
                     }
                  }
                  Log.i("EngineRoomPageFragment", "Messaging service is RESPONDING");
                  break;

               case NOT_CONNECTED:
               case NOT_RESPONDING:
               case NOT_FOUND:
                  pageLayout.setVisibility(View.INVISIBLE);
                  if(progressCtn != null)progressCtn.setVisibility(View.VISIBLE);

                  if(progressInfo != null) {
                     progressInfo.setVisibility(View.VISIBLE);
                     String msg = "";
                     if (ms.state == MessagingViewModel.MessagingServiceState.NOT_FOUND) {
                        msg = "Cannot configure service as configuration details not found (possible webserver issue)";
                     } else if (ms.state == MessagingViewModel.MessagingServiceState.NOT_RESPONDING) {
                        msg = "Engine Room service is not responding";
                     } else {
                        msg = "Engine Room service is not connected.  Check service has started.";
                     }
                     progressInfo.setText(msg);
                  }
                  Log.i("EngineRoomPageFragment", "Messaging service is " + ms.state);
                  break;

               default:
                  Log.i("EngineRoomPageFragment", "Messaging service is " + ms.state);
                  break;
            }
         });
      }
   }
}
