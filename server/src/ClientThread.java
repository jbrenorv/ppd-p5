import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.json.*;

import models.Contact;
import models.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientThread extends Thread {

    private static final Logger logger = LogManager.getLogger();

    private final PrintStream printStream;
    private final BufferedReader reader;

    public ClientThread(Socket socket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.printStream = new PrintStream(socket.getOutputStream(), true, StandardCharsets.UTF_8);
    }

    public void run() {

        logger.info("Client thread started!");

        // Notifies the client that the channel is available
        printStream.println("available");

        logger.info("Waiting for client messages...");

        while (true) {
            try {

                // Wait for messages
                String message = reader.readLine();

                logger.info("Message received from socket: {}", message);

                handleClientMessage(message);

            } catch (Exception e) {
                logger.error(e.getMessage());
                break;
            }
        }

        RemoteServer.removeCallback(this::onContactsListChanged);

        logger.info("Client thread stopped!");
    }

    public void onContactsListChanged(Collection<Contact> contacts) {
        sendContacts(contacts);
    }

    private void handleClientMessage(String message) {
        JsonObject jsonObject = Json.createReader(new StringReader(message)).readObject();

        MessageType messageType = MessageType.values()[jsonObject.getInt("messageType")];

        switch (messageType) {
            case createContact:
                createContact(jsonObject.getJsonObject("contact"));
                break;

            case getContacts:
                sendContacts(RemoteServer.getContacts());
                break;

            default:
                break;
        }
    }

    private void createContact(JsonObject jsonObject) {
        Contact contact = Contact.createClientFromJsonObject(jsonObject);

        RemoteServer.createContact(contact);

        logger.info("Successfully created client: {}", contact.getName());
    }

    private String getContactsJsonString(Collection<Contact> contacts) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("messageType", MessageType.contactsList.ordinal());

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (Contact contact : contacts) {
            JsonObject clientJsonObject = contact.toJsonObject();
            jsonArrayBuilder.add(clientJsonObject);
        }

        builder.add("contacts", jsonArrayBuilder.build());

        return builder.build().toString();
    }

    private void sendContacts(Collection<Contact> contacts) {
        logger.info("Sending contacts list");

        String contactsJsonString = getContactsJsonString(contacts);

        printStream.println(contactsJsonString);
    }
}
