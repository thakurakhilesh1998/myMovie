package com.example.mymovie.Modal;

import android.os.Parcel;
import android.os.Parcelable;

public class MoviesData implements Comparable, Parcelable {

    public static final Creator<MoviesData> CREATOR = new Creator<MoviesData>() {
        @Override
        public MoviesData createFromParcel(Parcel in) {
            return new MoviesData(in);
        }

        @Override
        public MoviesData[] newArray(int size) {
            return new MoviesData[size];
        }
    };
    String title;
    String posterUrl;
    String releaseDate;
    String overview;
    int id;
    double popularity;
    int voteCount;
    double voteAverage;

    public MoviesData(String title, String posterUrl, String releaseDate, String overview, int id, double popularity, int voteCount, double voteAverage) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.id = id;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
    }

    protected MoviesData(Parcel in) {
        title = in.readString();
        posterUrl = in.readString();
        releaseDate = in.readString();
        overview = in.readString();
        id = in.readInt();
        popularity = in.readDouble();
        voteCount = in.readInt();
        voteAverage = in.readDouble();
    }

    public String getTitle() {
        return title;
    }


    public String getPosterUrl() {
        return posterUrl;
    }


    public String getReleaseDate() {
        return releaseDate;
    }


    public String getOverview() {
        return overview;
    }


    public double getPopularity() {
        return popularity;
    }


    public int getVoteCount() {
        return voteCount;
    }


    public double getVoteAverage() {
        return voteAverage;
    }

    @Override
    public int compareTo(Object o) {
        double popularity = ((MoviesData) o).getPopularity();
        return (int) (popularity - this.popularity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(posterUrl);
        parcel.writeString(releaseDate);
        parcel.writeString(overview);
        parcel.writeInt(id);
        parcel.writeDouble(popularity);
        parcel.writeInt(voteCount);
        parcel.writeDouble(voteAverage);
    }
}
