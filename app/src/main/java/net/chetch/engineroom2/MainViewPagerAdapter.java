package net.chetch.engineroom2;

import net.chetch.cmengineroom.EnginesFragment;
import net.chetch.cmengineroom.PumpsFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainViewPagerAdapter extends FragmentStateAdapter {


   public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
      super(fragmentActivity);
   }

   @NonNull
   @Override
   public Fragment createFragment(int position) {
      Fragment page = null;
      if(position == 0) {
         page = new EnginesFragment();
      } else {
         page = new PumpsFragment();
      }

      return page;
   }

   @Override
   public int getItemCount() {
      return 2;
   }
}
