import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.json.*;

import models.Contact;
import models.MessageType;

public class ClientThread extends Thread {

    private final PrintStream printStream;
    private final BufferedReader reader;

    public ClientThread(Socket socket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.printStream = new PrintStream(socket.getOutputStream(), true, StandardCharsets.UTF_8);
    }

    public void run() {

        Logger.info("Client thread started");

        Logger.info("Sending current contacts");
        sendContacts(MainServer.getContacts());

        Logger.info("Waiting for client messages...");

        while (true) {
            try {

                // Wait for messages
                String message = reader.readLine();

                Logger.info("Message received from socket: " + message);

                handleClientMessage(message);

            } catch (Exception e) {
                Logger.error(e.getMessage());
                break;
            }
        }

        MainServer.removeCallback(this::onContactsListChanged);

        Logger.info("Client thread stopped!");
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

            case contactsList:
                receiveContactsList(jsonObject.getJsonArray("contacts"));
                break;

            default:
                break;
        }
    }

    private void createContact(JsonObject jsonObject) {
        Contact contact = JsonUtils.buildContact(jsonObject);

        MainServer.createContact(contact);

        Logger.info("Successfully created client: " + contact.getName());
    }

    private void receiveContactsList(JsonArray contacts) {
        for (Contact contact : JsonUtils.buildContacts(contacts)) {
            MainServer.createContact(contact);
        }
    }

    private void sendContacts(Collection<Contact> contacts) {
        Logger.info("Sending contacts list");

        String contactsJsonString = JsonUtils.buildContactsJson(contacts);

        printStream.println(contactsJsonString);
    }
}
