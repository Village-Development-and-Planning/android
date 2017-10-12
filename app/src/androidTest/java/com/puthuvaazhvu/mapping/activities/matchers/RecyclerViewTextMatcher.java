package com.puthuvaazhvu.mapping.activities.matchers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.puthuvaazhvu.mapping.views.activities.Contract;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by muthuveerappans on 10/11/17.
 */

public class RecyclerViewTextMatcher extends TypeSafeMatcher<View> {
    private final String itemText;
    private final String contentDescription;

    public RecyclerViewTextMatcher(String itemText, String contentDescription) {
        this.itemText = itemText;
        this.contentDescription = contentDescription;
    }

    @Override
    public boolean matchesSafely(View item) {
        Matcher<View> itemMatcher = allOf(isDescendantOfA(isAssignableFrom(RecyclerView.class))
                , withText(itemText), is(not(isChecked())), withContentDescription(contentDescription));

        return itemMatcher.matches(item);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is isDescendantOfA RV with text " + itemText);
    }

    @Factory
    public static Matcher<View> withItemText(String text, String contentDescription) {
        return new RecyclerViewTextMatcher(text, contentDescription);
    }
}
