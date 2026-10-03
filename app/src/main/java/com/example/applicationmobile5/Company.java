package com.example.applicationmobile5;

public class Company {

    private int id;
    private String name, services, phone, url, email, location;
    private String logoUri;

    public Company(int id, String name, String services, String phone,
                   String url, String email, String location, String logoUri) {
        this.id = id;
        this.name = name;
        this.services = services;
        this.phone = phone;
        this.url = url;
        this.email = email;
        this.location = location;
        this.logoUri = logoUri;
    }

    public Company(String name, String services, String phone,
                   String url, String email, String location, String logoUri) {
        this(-1, name, services, phone, url, email, location, logoUri);
    }



    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public String getServices() { return services; }
    public String getPhone() { return phone; }
    public String getUrl() { return url; }
    public String getEmail() { return email; }
    public String getLocation() { return location; }
    public String getLogoUri() { return logoUri; }

    public void setName(String name) { this.name = name; }
    public void setServices(String services) { this.services = services; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setUrl(String url) { this.url = url; }
    public void setEmail(String email) { this.email = email; }
    public void setLocation(String location) { this.location = location; }
    public void setLogoUri(String logoUri) { this.logoUri = logoUri; }
}
