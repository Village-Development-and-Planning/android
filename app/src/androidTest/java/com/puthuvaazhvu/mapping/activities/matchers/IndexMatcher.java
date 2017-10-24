package com.puthuvaazhvu.mapping.activities.matchers;

import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by muthuveerappans on 10/11/17.
 */

public class IndexMatcher extends TypeSafeMatcher<View> {
    final Matcher<View> matcher;
    final int index;
    int currentIndex = 0;

    public IndexMatcher(Matcher<View> matcher, int index) {
        this.matcher = matcher;
        this.index = index;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with index: ");
        description.appendValue(index);
        matcher.describeTo(description);
    }

    @Override
    public boolean matchesSafely(View view) {
        return matcher.matches(view) && currentIndex++ == index;
    }

    @Factory
    public static Matcher<View> indexMatcher(Matcher<View> matcher, int index) {
        return new IndexMatcher(matcher, index);
    }
}
