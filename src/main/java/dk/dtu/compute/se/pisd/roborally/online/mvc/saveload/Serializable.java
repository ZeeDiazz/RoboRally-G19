package dk.dtu.compute.se.pisd.roborally.online.mvc.saveload;

import com.google.gson.JsonElement;

/**
 * Interface for all the classes that needs to be serialized
 *
 * @author Zigalow
 */
public interface Serializable {
    /**
     * @return A serialized json-element
     * @author Zigalow
     */
    JsonElement serialize();

    /**
     * @param element The json element that needs to be deserialized
     * @return A Serializable object from given json element
     * @author Zigalow
     */

    Serializable deserialize(JsonElement element);

}
