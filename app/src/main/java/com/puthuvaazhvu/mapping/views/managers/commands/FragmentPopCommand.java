package com.puthuvaazhvu.mapping.views.managers.commands;

import android.support.v4.app.Fragment;

import com.puthuvaazhvu.mapping.views.managers.receiver.IStackFragmentManager;

/**
 * Created by muthuveerappans on 10/16/17.
 */

public class FragmentPopCommand implements IManagerCommand {
    private final IStackFragmentManager iStackFragmentManager;
    private final String tag;
    private final Fragment fragment;

    public FragmentPopCommand(IStackFragmentManager iStackFragmentManager, String tag) {
        this.iStackFragmentManager = iStackFragmentManager;
        this.tag = tag;
        this.fragment = iStackFragmentManager.getFragment(tag);
    }

    @Override
    public void execute() {
        iStackFragmentManager.popFragment(fragment);
    }

    @Override
    public void unExecute() {
        iStackFragmentManager.pushFragment(tag, fragment);
    }
}
