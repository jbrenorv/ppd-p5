package models;

import java.util.Collection;

public class ContactsListChangedEvent {
    private final boolean _wasChangedByOtherServer;
    private final Collection<Contact> _contacts;

    public ContactsListChangedEvent(boolean wasChangedByOtherServer, Collection<Contact> contacts) {
        _wasChangedByOtherServer = wasChangedByOtherServer;
        _contacts = contacts;
    }

    public boolean wasChangedByOtherServer() {
        return _wasChangedByOtherServer;
    }

    public Collection<Contact> contacts() {
        return _contacts;
    }
}
