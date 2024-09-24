import java.io.IOException;
import java.net.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import models.Contact;

public class MainServer {

    private static final Set<Contact> contacts = new HashSet<>();

    private static final Set<Consumer<Collection<Contact>>> callbacks = new HashSet<>();

    public static synchronized void createContact(Contact contact) {
        contacts.add(contact);

        for (Consumer<Collection<Contact>> callback : callbacks) {
            callback.accept(contacts);
        }
    }

    public static synchronized Collection<Contact> getContacts() {
        return contacts;
    }

    public static synchronized void removeCallback(Consumer<Collection<Contact>> callback) {
        callbacks.remove(callback);
    }

    public static void startServer(String[] args) {

        tryConnectToOtherServerInstances(args);

        int port = Integer.parseInt(args[0]);
        Logger.info("Starting server on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            Logger.info("Server started");

            while (true) {
                
                Socket socket = serverSocket.accept();
                createClientThread(socket);

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
                createClientThread(socket);

            } catch (IOException e) {
                Logger.error("Error connecting to other server instance: " + e.getMessage());
            }
        }
    }

    private static void createClientThread(Socket socket) {
        Logger.info("New client connected");
        Logger.info("Creating client thread");

        try {

            ClientThread clientThread = new ClientThread(socket);
            callbacks.add(clientThread::onContactsListChanged);
            clientThread.start();

        } catch (IOException e) {
            Logger.error("Error creating client thread: " + e.getMessage());
        }
    }
}
