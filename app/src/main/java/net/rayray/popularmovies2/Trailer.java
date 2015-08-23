package net.rayray.popularmovies2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rhawley on 7/23/15.
 * The trailer class was created to handle trailers, each of which have a title and a youtube link.
 * It is parcelable, so that it can be handled in bundles (such as saved states).
 */
public class Trailer implements Parcelable {
    private String strName;
    private String strYoutubeKey;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strName);
        dest.writeString(strYoutubeKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Trailer(String strName, String strYoutubeKey) {
        this.strName = strName;
        this.strYoutubeKey = strYoutubeKey;
    }

    private Trailer(Parcel in) {
        this.strName = in.readString();
        this.strYoutubeKey = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {

        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int i) {
            return new Trailer[i];
        }
    };

    public String getName() { return strName; }

    public String getYoutubeKey() { return strYoutubeKey; }

}
