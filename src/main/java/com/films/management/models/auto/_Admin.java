package com.films.management.models.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.Property;

import com.films.management.models.Film;

/**
 * Class _Admin was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Admin extends BaseDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "Id";

    public static final Property<String> EMAIL = Property.create("email", String.class);
    public static final Property<String> FIRST_NAME = Property.create("firstName", String.class);
    public static final Property<String> LAST_NAME = Property.create("lastName", String.class);
    public static final Property<String> PASSWORD = Property.create("password", String.class);
    public static final Property<List<Film>> MANAGE = Property.create("manage", List.class);

    protected String email;
    protected String firstName;
    protected String lastName;
    protected String password;

    protected Object manage;

    public void setEmail(String email) {
        beforePropertyWrite("email", this.email, email);
        this.email = email;
    }

    public String getEmail() {
        beforePropertyRead("email");
        return this.email;
    }

    public void setFirstName(String firstName) {
        beforePropertyWrite("firstName", this.firstName, firstName);
        this.firstName = firstName;
    }

    public String getFirstName() {
        beforePropertyRead("firstName");
        return this.firstName;
    }

    public void setLastName(String lastName) {
        beforePropertyWrite("lastName", this.lastName, lastName);
        this.lastName = lastName;
    }

    public String getLastName() {
        beforePropertyRead("lastName");
        return this.lastName;
    }

    public void setPassword(String password) {
        beforePropertyWrite("password", this.password, password);
        this.password = password;
    }

    public String getPassword() {
        beforePropertyRead("password");
        return this.password;
    }

    public void addToManage(Film obj) {
        addToManyTarget("manage", obj, true);
    }

    public void removeFromManage(Film obj) {
        removeToManyTarget("manage", obj, true);
    }

    @SuppressWarnings("unchecked")
    public List<Film> getManage() {
        return (List<Film>)readProperty("manage");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "email":
                return this.email;
            case "firstName":
                return this.firstName;
            case "lastName":
                return this.lastName;
            case "password":
                return this.password;
            case "manage":
                return this.manage;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "email":
                this.email = (String)val;
                break;
            case "firstName":
                this.firstName = (String)val;
                break;
            case "lastName":
                this.lastName = (String)val;
                break;
            case "password":
                this.password = (String)val;
                break;
            case "manage":
                this.manage = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.email);
        out.writeObject(this.firstName);
        out.writeObject(this.lastName);
        out.writeObject(this.password);
        out.writeObject(this.manage);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.email = (String)in.readObject();
        this.firstName = (String)in.readObject();
        this.lastName = (String)in.readObject();
        this.password = (String)in.readObject();
        this.manage = in.readObject();
    }

}