package ca.lambton.habittracker.leaderboard.model;

import java.io.Serializable;
import java.util.Comparator;

public class Leaderboard implements Serializable, Comparator<Leaderboard> {

    private String name;
    private int score;
    private String imageUrl;

    public Leaderboard(String name, int score, String imageUrl) {
        this.name = name;
        this.score = score;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Leaderboard{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public int compare(Leaderboard leaderboardOne, Leaderboard leaderboardTwo) {
        return Integer.compare(leaderboardTwo.getScore(), leaderboardOne.getScore());
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
