import java.io.IOException;
import java.net.*;

import java.util.*;
import java.util.function.Consumer;

import models.Contact;
import models.ContactsListChangedEvent;

public class MainServer {

    private static final Map<Long, Contact> _contacts = new HashMap<>();

    private static final Set<Consumer<ContactsListChangedEvent>> _callbacks = new HashSet<>();

    public static synchronized void createContact(Contact contact) {
        _contacts.put(contact.getId(), contact);
        notifyChanges(false);
    }

    public static synchronized void updateContacts(Collection<Contact> contacts) {
        for (Contact contact : contacts) {
            _contacts.put(contact.getId(), contact);
        }
        notifyChanges(true);
    }

    public static synchronized Collection<Contact> getContacts() {
        return _contacts.values();
    }

    public static synchronized void deleteContact(Contact contact) {
        _contacts.remove(contact.getId());
        notifyChanges(false);
    }

    public static synchronized void updateContact(Contact contact) {
        _contacts.put(contact.getId(), contact);
        notifyChanges(false);
    }

    public static synchronized void removeCallback(Consumer<ContactsListChangedEvent> callback) {
        _callbacks.remove(callback);
    }

    private static void notifyChanges(boolean wasChangedByOtherServer) {
        for (Consumer<ContactsListChangedEvent> callback : _callbacks) {
            callback.accept(new ContactsListChangedEvent(wasChangedByOtherServer, _contacts.values()));
        }
    }

    public static void startServer(String[] args) {

        tryConnectToOtherServerInstances(args);

        int port = Integer.parseInt(args[0]);
        Logger.info("Starting server on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            Logger.info("Server started");

            while (true) {
                
                Socket socket = serverSocket.accept();
                createClientThread(socket, false);

            }
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    private static void tryConnectToOtherServerInstances(String[] args) {
        for (String arg : Arrays.stream(args).skip(1).toArray(String[]::new)) {
            int port = Integer.parseInt(arg);

            try {

                Socket socket = new Socket("localhost", port);
                createClientThread(socket, true);

            } catch (IOException e) {
                Logger.error("Error connecting to other server instance: " + e.getMessage());
            }
        }
    }

    private static void createClientThread(Socket socket, boolean isOtherServerInstance) {
        Logger.info("New client connected");
        Logger.info("Creating client thread");

        try {

            ClientThread clientThread = new ClientThread(socket, isOtherServerInstance);
            _callbacks.add(clientThread::onContactsListChanged);
            clientThread.start();

        } catch (IOException e) {
            Logger.error("Error creating client thread: " + e.getMessage());
        }
    }
}
