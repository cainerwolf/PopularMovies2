package net.rayray.popularmovies2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rhawley on 7/23/15.
 *
 * The Movie class contains everything you need to make a movie and a detail view, except
 * for trailers and reviews, which are handled in the detail view itself.  The class is parcelable,
 * so that it can be passed in Intents and in Bundles.
 */
public class Movie implements Parcelable {
    private int id;
    private String strTitle;
    private String strReleaseDate;
    private String strPosterPath;
    private String strVoteAverage;
    private String strSynopsis;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(strTitle);
        dest.writeString(strReleaseDate);
        dest.writeString(strPosterPath);
        dest.writeString(strVoteAverage);
        dest.writeString(strSynopsis);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Movie(int id, String strTitle, String strReleaseDate, String strPosterPath,
                 String strVoteAverage, String strSynopsis) {
        this.id = id;
        this.strTitle = strTitle;
        this.strReleaseDate = strReleaseDate;
        this.strPosterPath = strPosterPath;
        this.strVoteAverage = strVoteAverage;
        this.strSynopsis = strSynopsis;
    }

    private Movie(Parcel in) {
        this.id = in.readInt();
        this.strTitle = in.readString();
        this.strReleaseDate = in.readString();
        this.strPosterPath = in.readString();
        this.strVoteAverage = in.readString();
        this.strSynopsis = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    public int getId() { return id; }

    public String getTitle() { return strTitle; }

    public String getReleaseDate() { return strReleaseDate; }

    public String getPosterPath() { return strPosterPath; }

    public String getVoteAverage() { return strVoteAverage; }

    public String getSynopsis() { return strSynopsis; }

    public String getIdAsString() { return Integer.toString(id); }

    public String getFullPosterPath() {
        return "http://image.tmdb.org/t/p/w185/" + strPosterPath;
    }
}
