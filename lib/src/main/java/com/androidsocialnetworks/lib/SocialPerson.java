package com.androidsocialnetworks.lib;

public class SocialPerson {

    public String id;
    public String name;
    public String company;
    public String position;
    public String avatarURL;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SocialPerson that = (SocialPerson) o;

        if (avatarURL != null ? !avatarURL.equals(that.avatarURL) : that.avatarURL != null)
            return false;
        if (company != null ? !company.equals(that.company) : that.company != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (position != null ? !position.equals(that.position) : that.position != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (company != null ? company.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        result = 31 * result + (avatarURL != null ? avatarURL.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SocialPerson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", position='" + position + '\'' +
                ", avatarURL='" + avatarURL + '\'' +
                '}';
    }
}
