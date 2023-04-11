package ca.lambton.habittracker.leaderboard.model;

import java.io.Serializable;
import java.util.Comparator;

public class Leaderboard implements Serializable, Comparator<Leaderboard> {
    private String accountId;
    private String name;
    private int score;
    private String imageUrl;

    public Leaderboard(String name, int score, String imageUrl) {
        this.name = name;
        this.score = score;
        this.imageUrl = imageUrl;
    }

    public Leaderboard() {
    }

    public Leaderboard(String accountId, String name, int score, String imageUrl) {
        this(name, score, imageUrl);
        this.accountId = accountId;
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

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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
