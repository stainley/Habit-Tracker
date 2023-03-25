package ca.lambton.habittracker.view.ongoingHabits;

public class AchievementInfo {
    private String achievementLabel;
    private int scoreImage;
    private int starImage;

    public AchievementInfo(String achievementLabel, int scoreImage, int starImage) {
        this.achievementLabel = achievementLabel;
        this.scoreImage = scoreImage;
        this.starImage = starImage;
    }

    public String getAchievementLabel() {
        return achievementLabel;
    }

    public void setAchievementLabel(String achievementLabel) {
        this.achievementLabel = achievementLabel;
    }

    public int getScoreImage() {
        return scoreImage;
    }

    public void setScoreImage(int scoreImage) {
        this.scoreImage = scoreImage;
    }

    public int getStarImage() {
        return starImage;
    }

    public void setStarImage(int starImage) {
        this.starImage = starImage;
    }
}
