import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Competitor {
    private int competitor_number;
    private String competitor_name;
    private String email;
    private String competitor_dob;
    private String category;
    private String level;
    private List<Integer> scores;

    public Competitor(int competitor_number, String competitor_name, String email, String competitor_dob, String category, String level) {
        this.competitor_number = competitor_number;
        this.competitor_name = competitor_name;
        this.email = email;
        this.competitor_dob = competitor_dob;
        this.category = category;
        this.level = level;
        this.scores = new ArrayList<>();
    }
    
    // getter setters
    public int getcompetitor_number() {
        return competitor_number;
    }

    public void setcompetitor_number(int competitor_number) {
        this.competitor_number = competitor_number;
    }

    public String getcompetitor_name() {
        return competitor_name;
    }

    public void setcompetitor_name(String competitor_name) {
        this.competitor_name = competitor_name;
    }

    public String getemail() {
        return email;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public String getcompetitor_dob() {
        return competitor_dob;
    }

    public void setcompetitor_dob(String competitor_dob) {
        this.competitor_dob = competitor_dob;
    }

    public String getcategory() {
        return category;
    }

    public void setcategory(String category) {
        this.category = category;
    }

    public String getlevel() {
        return level;
    }

    public void setlevel(String level) {
        this.level = level;
    }

    public List<Integer> getScores() {
        return scores;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }

    public void addScore(int score) {
        scores.add(score);
    }

    public double calculateOverallScore() {
        return scores.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(String.format("%-5d %-20s %-15s", competitor_number, competitor_name, level));
        result.append(" ");
        for (int score : scores) {
            result.append(score).append(" ");
        }
        result.append(String.format("   %.1f", calculateOverallScore()));
        return result.toString();
    }

    public String getFullDetails() {
        StringBuilder result = new StringBuilder();
        int age = calculateAge(competitor_dob);
        result.append(String.format("Competitor Number %d, Name %s, Aged %d.%n", competitor_number, competitor_name, age));
        result.append(String.format("%s is a %s and awarded scores: ", competitor_name, level));
        for (int score : scores) {
            result.append(score).append(" ");
        }
        result.append("\n");
        return result.toString();
    }

    private int calculateAge(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate birthDate = LocalDate.parse(dob, formatter);
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears();
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + dob);
            return -1;
        }
    }

    public String getShortDetails() {
        return String.format("CN %d (%s) scored overall score %.1f.", competitor_number, getInitials(), calculateOverallScore());
    }

    private String getInitials() {
        StringBuilder initials = new StringBuilder();
        String[] nameParts = competitor_name.split(" ");
        for (String part : nameParts) {
            initials.append(part.charAt(0));
        }
        return initials.toString().toUpperCase();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Competitor that = (Competitor) obj;
        return email.equals(that.email) && category.equals(that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, category);
    }
}
