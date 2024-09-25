package models;

public class Contact {
    private final long id;
    private final String name;
    private final String phone;

    public Contact(long id, String name, String phone) {
        this.id = id == -1 ? System.currentTimeMillis() : id;
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public long getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Contact)) return false;
        return this.id == ((Contact) obj).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.id);
    }
}
