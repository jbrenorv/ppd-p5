import java.io.IOException;
import java.net.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import models.Contact;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoteServer {

    private static final Logger logger = LogManager.getLogger();

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

    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(1024)) {

            logger.info("Server started");

            while (true) {
                
                Socket socket = serverSocket.accept();

                logger.info("New client connected");

                ClientThread clientThread = new ClientThread(socket);

                callbacks.add(clientThread::onContactsListChanged);

                clientThread.start();

            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
