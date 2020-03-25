package de.hpi3d.gamepgrog.trap.datatypes;


/**
 * A displayable is an object which can be displayed by the list on our screen.
 * <p>
 * This functionality was first added for extensibility during an earlier prototype, and
 * never removed, since it works just fine, and doesn't add a lot of complexity.
 */
public interface Displayable {

    String getDisplayString();
}
