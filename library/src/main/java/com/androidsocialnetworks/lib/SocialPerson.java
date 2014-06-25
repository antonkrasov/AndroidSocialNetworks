package com.androidsocialnetworks.lib;

import android.os.Parcel;
import android.os.Parcelable;

public class SocialPerson implements Parcelable {

    public static final Parcelable.Creator<SocialPerson> CREATOR
            = new Parcelable.Creator<SocialPerson>() {
        public SocialPerson createFromParcel(Parcel in) {
            return new SocialPerson(in);
        }

        public SocialPerson[] newArray(int size) {
            return new SocialPerson[size];
        }
    };

    public String id;
    public String name;
    public String company;
    public String position;
    public String avatarURL;

    public String profileURL; // url to users profile, can be generated for twitter, facebook, but need to get via api from LinkedIn
    public String nickname;

    public SocialPerson() {

    }

    private SocialPerson(Parcel in) {
        id = in.readString();
        name = in.readString();
        company = in.readString();
        position = in.readString();
        avatarURL = in.readString();
        profileURL = in.readString();
        nickname = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(company);
        dest.writeString(position);
        dest.writeString(avatarURL);
        dest.writeString(profileURL);
        dest.writeString(nickname);
    }

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
        if (nickname != null ? !nickname.equals(that.nickname) : that.nickname != null)
            return false;
        if (position != null ? !position.equals(that.position) : that.position != null)
            return false;
        if (profileURL != null ? !profileURL.equals(that.profileURL) : that.profileURL != null)
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
        result = 31 * result + (profileURL != null ? profileURL.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
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
                ", profileURL='" + profileURL + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
