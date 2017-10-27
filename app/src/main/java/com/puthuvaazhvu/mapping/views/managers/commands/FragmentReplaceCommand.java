package com.puthuvaazhvu.mapping.views.managers.commands;

import android.support.v4.app.Fragment;

import com.puthuvaazhvu.mapping.views.managers.receiver.IStackFragmentManager;

/**
 * Created by muthuveerappans on 10/27/17.
 */

public class FragmentReplaceCommand implements IManagerCommand {
    private final IStackFragmentManager iStackFragmentManager;
    private final String tag;
    private final Fragment fragment;

    public FragmentReplaceCommand(IStackFragmentManager iStackFragmentManager, String tag, Fragment fragment) {
        this.iStackFragmentManager = iStackFragmentManager;
        this.tag = tag;
        this.fragment = fragment;
    }

    @Override
    public void execute() {
        iStackFragmentManager.replaceFragment(tag, fragment);
    }

    @Override
    public void unExecute() {
        throw new IllegalArgumentException("Not imeplemented");
    }
}
