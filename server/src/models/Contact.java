package models;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Contact {
    private final String name;
    private final String phone;

    private Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public static Contact createClientFromJsonObject(JsonObject jsonObject) {
        return new Contact(jsonObject.getString("name"), jsonObject.getString("phone"));
    }

    public JsonObject toJsonObject() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

        jsonObjectBuilder.add("name", this.name);
        jsonObjectBuilder.add("phone", this.phone);

        return jsonObjectBuilder.build();
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Contact)) return false;
        return this.name.equals(((Contact) obj).name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
