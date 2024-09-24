import models.Contact;
import models.MessageType;

import javax.json.*;
import java.util.Collection;
import java.util.stream.Collectors;

public class JsonUtils {

    public static Contact buildContact(JsonObject contactJson) {
        return new Contact(contactJson.getString("name"), contactJson.getString("phone"));
    }

    public static JsonObject buildContactJson(Contact contact) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

        jsonObjectBuilder.add("name", contact.getName());
        jsonObjectBuilder.add("phone", contact.getPhone());

        return jsonObjectBuilder.build();
    }

    public static String buildContactsJson(Collection<Contact> contacts) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("messageType", MessageType.contactsList.ordinal());

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (Contact contact : contacts) {
            jsonArrayBuilder.add(buildContactJson(contact));
        }

        builder.add("contacts", jsonArrayBuilder.build());

        return builder.build().toString();
    }

    public static Collection<Contact> buildContacts(JsonArray contactsJsonArray) {
        return contactsJsonArray
                .stream()
                .map(contactJsonObject -> buildContact(contactJsonObject.asJsonObject()))
                .collect(Collectors.toList());
    }
}
