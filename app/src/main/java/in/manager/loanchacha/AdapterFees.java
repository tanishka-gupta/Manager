package in.manager.loanchacha;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AdapterFees extends FragmentPagerAdapter {

    public AdapterFees(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        Bundle bundle = new Bundle();
        int imgResId = 0;
        String tab="";
        int colorResId = 0;
        switch (index) {

            case 0:
                tab = Constant.Developer;
                break;
        }
        bundle.putInt("image", imgResId);
        bundle.putString("tab",tab);
        bundle.putInt("color", colorResId);
        SwipeTabFragmentFees swipeTabFragment = new SwipeTabFragmentFees();
        swipeTabFragment.setArguments(bundle);
        return swipeTabFragment;
    }

    @Override
    public int getCount() {
        return 1;
    }
}