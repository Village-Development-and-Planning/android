package org.ptracking.vdp.modals;

import java.io.Serializable;

/**
 * Created by muthuveerappans on 10/19/17.
 */

public abstract class BaseObject implements Serializable {
    public BaseObject copy() {
        return this;
    }
}
