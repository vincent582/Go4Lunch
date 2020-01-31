package com.pdv.go4lunch.Model.Place;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pdv.go4lunch.Model.GooglePlacesApiModel.Geometry;
import java.util.List;

public class Result implements Parcelable{

    @SerializedName("place_id")
    @Expose
    private String place_id;
    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry  = null;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;

    private int distance;

    private Boolean isSomeoneEatingHere = false;

    protected Result(Parcel in) {
        place_id = in.readString();
        formattedPhoneNumber = in.readString();
        icon = in.readString();
        name = in.readString();
        types = in.createStringArrayList();
        vicinity = in.readString();
        photos = in.createTypedArrayList(Photo.CREATOR);
        website = in.readString();
        distance = in.readInt();
    }


    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public Boolean getSomeoneEatingHere() {
        return isSomeoneEatingHere;
    }

    public void setSomeoneEatingHere(Boolean someoneEatingHere) {
        isSomeoneEatingHere = someoneEatingHere;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(place_id);
        dest.writeString(formattedPhoneNumber);
        dest.writeString(icon);
        dest.writeString(name);
        dest.writeStringList(types);
        dest.writeString(vicinity);
        dest.writeTypedList(photos);
        dest.writeString(website);
        dest.writeInt(distance);
        dest.writeByte((byte) (isSomeoneEatingHere == null ? 0 : isSomeoneEatingHere ? 1 : 2));
    }
}