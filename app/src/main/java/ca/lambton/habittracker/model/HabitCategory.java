package ca.lambton.habittracker.model;

public class HabitCategory {

    private String name;
    private String imageName;

    public HabitCategory(String name, String imageName) {
        this.name = name;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public String getImageName() {
        return imageName;
    }
}
