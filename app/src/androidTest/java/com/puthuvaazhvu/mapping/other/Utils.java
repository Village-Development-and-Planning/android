package com.puthuvaazhvu.mapping.other;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

import static android.support.test.espresso.core.deps.guava.base.Predicates.notNull;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by muthuveerappans on 10/10/17.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class Utils {
    @Test
    public void readTestAsset() throws Exception {
        Context ctx = InstrumentationRegistry.getContext();
        InputStream is = ctx.getResources().getAssets().open("survey_6.json");
        String s = com.puthuvaazhvu.mapping.utils.Utils.readFromInputStream(is);
        assertThat(s, is(notNullValue()));
    }
}
