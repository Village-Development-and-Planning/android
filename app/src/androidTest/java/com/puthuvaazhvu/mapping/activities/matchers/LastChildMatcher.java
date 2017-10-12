package com.puthuvaazhvu.mapping.activities.matchers;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by muthuveerappans on 10/11/17.
 */

// get the count of a repetitive child in a given root viewgroup
public class LastChildMatcher extends TypeSafeMatcher<View> {
    private final Matcher<View> matcher;
    private final ViewGroup root;

    private int count = 0;
    private int matchCount = -1;

    public LastChildMatcher(Matcher<View> matcher, ViewGroup root) {
        this.matcher = matcher;
        this.root = root;
    }

    /**
     * Iterates through all the views in the decor window until we return true here.
     *
     * @param item
     * @return True to select view
     */
    @Override
    protected boolean matchesSafely(View item) {
        if (matchCount < 0)
            matchCount = matchCount(root, this.matcher);
        return matcher.matches(item)
                && count++ == (matchCount - 1); // we are taking indexes into account
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with index: ");
        description.appendValue(matchCount - 1);
        matcher.describeTo(description);
    }

    /**
     * Recursively searches through the root to find the matcher
     *
     * @param root The root to search for
     * @return The count of the match found
     */
    protected int matchCount(ViewGroup root, Matcher<View> matcher) {
        int matchCount = 0;
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (matcher.matches(child)) {
                matchCount++;
            } else if (child instanceof ViewGroup) {
                matchCount += matchCount((ViewGroup) child, matcher);
            }
        }
        return matchCount;
    }

    @Factory
    public static Matcher<View> matchLastChild(ViewGroup root, Matcher<View> matcher) {
        return new LastChildMatcher(matcher, root);
    }
}
