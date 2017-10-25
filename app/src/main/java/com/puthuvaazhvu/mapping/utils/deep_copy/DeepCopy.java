package com.puthuvaazhvu.mapping.utils.deep_copy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DeepCopy {

    /**
     * Returns a copy of the object, or null if the object cannot
     * be serialized.
     * Takes care of all of the details:
     * superclass fields, following object graphs,
     * and handling repeated references to the same object within the graph.
     */

    public static Object copy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            FastByteArrayOutputStream fbos =
                    new FastByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(fbos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Retrieve an input stream from the byte array and read
            // a copy of the object back in. 
            ObjectInputStream in =
                    new ObjectInputStream(fbos.getInputStream());
            obj = in.readObject();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error in Deep Copy. " + e.getMessage());
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("Error in Deep Copy. " + cnfe.getMessage());
        }
        return obj;
    }

}