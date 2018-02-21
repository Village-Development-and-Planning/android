package com.puthuvaazhvu.mapping.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by muthuveerappans on 21/02/18.
 */

public class BaseFragment extends Fragment {

    private FragmentInterface fragmentInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        fragmentInterface = (FragmentInterface) context;
    }

    public void showLoading(String msg) {
        fragmentInterface.showLoading(msg);
    }

    public void hideLoading() {
        fragmentInterface.hideLoading();
    }
}
