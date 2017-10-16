package com.puthuvaazhvu.mapping.views.managers.commands;

import android.support.v4.app.Fragment;

import com.puthuvaazhvu.mapping.views.managers.receiver.IStackFragmentManager;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public class FragmentPushCommand implements IManagerCommand {
    private final IStackFragmentManager iStackFragmentManager;
    private final String tag;
    private final Fragment fragment;

    public FragmentPushCommand(IStackFragmentManager iStackFragmentManager, String tag, Fragment fragment) {
        this.iStackFragmentManager = iStackFragmentManager;
        this.tag = tag;
        this.fragment = fragment;
    }

    @Override
    public void execute() {
        iStackFragmentManager.pushFragment(tag, fragment);
    }

    @Override
    public void unExecute() {
        iStackFragmentManager.popFragment(fragment);
    }
}
