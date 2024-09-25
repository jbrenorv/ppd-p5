import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.json.*;

import models.Contact;
import models.ContactsListChangedEvent;
import models.MessageType;

public class ClientThread extends Thread {

    private final boolean _isOtherServerInstance;
    private final PrintStream printStream;
    private final BufferedReader reader;

    public ClientThread(Socket socket, boolean isOtherServerInstance) throws IOException {
        this._isOtherServerInstance = isOtherServerInstance;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.printStream = new PrintStream(socket.getOutputStream(), true, StandardCharsets.UTF_8);
    }

    public void run() {

        Logger.info("Client thread started");

        if (!_isOtherServerInstance) {
            sendContacts(MainServer.getContacts());
        }

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

    public void onContactsListChanged(ContactsListChangedEvent event) {
        if (_isOtherServerInstance && event.wasChangedByOtherServer()) return;
        sendContacts(event.contacts());
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

            case deleteContact:
                deleteContact(jsonObject.getJsonObject("contact"));
                break;

            case updateContact:
                updateContact(jsonObject);
                break;

            default:
                break;
        }
    }

    private void createContact(JsonObject jsonObject) {
        Contact contact = JsonUtils.buildContact(jsonObject);

        MainServer.createContact(contact);

        Logger.info("Successfully created contact: " + contact.getName());
    }

    private void receiveContactsList(JsonArray contacts) {
        Logger.info("Receiving contacts list from other server instance");
        MainServer.updateContacts(JsonUtils.buildContacts(contacts));
    }

    private void deleteContact(JsonObject jsonObject) {
        Contact contact = JsonUtils.buildContact(jsonObject);

        MainServer.deleteContact(contact);

        Logger.info("Successfully deleted contact: " + contact.getName());
    }

    private void updateContact(JsonObject jsonObject) {
        Contact contact = JsonUtils.buildContact(jsonObject.getJsonObject("contact"));

        MainServer.updateContact(contact);

        Logger.info("Successfully updated contact: " + contact.getName());
    }

    private void sendContacts(Collection<Contact> contacts) {
        Logger.info("Sending contacts list");

        String contactsJsonString = JsonUtils.buildContactsJson(contacts);

        printStream.println(contactsJsonString);
    }
}
