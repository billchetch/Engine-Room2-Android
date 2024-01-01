package net.chetch.engineroom2;

import net.chetch.cmengineroom.EnginesPageFragment;
import net.chetch.cmengineroom.PumpsPageFragment;

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
         page = new EnginesPageFragment();
      } else {
         page = new PumpsPageFragment();
      }

      return page;
   }

   @Override
   public int getItemCount() {
      return 2;
   }
}
