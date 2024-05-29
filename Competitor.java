import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Competitor {
    private int competitorNumber;
    private String competitorName;
    private String email;
    private String country;
    private String competitorDob;
    private String category;
    private String level;
    private List<Integer> scores;

    public Competitor(int competitorNumber, String competitorName, String email, String country, String competitorDob, String category, String level) {
        this.competitorNumber = competitorNumber;
        this.competitorName = competitorName;
        this.email = email;
        this.country = country;
        this.competitorDob = competitorDob;
        this.category = category;
        this.level = level;
        this.scores = new ArrayList<>();
    }

    // Getter and setter methods
    public int getCompetitorNumber() {
        return competitorNumber;
    }

    public void setCompetitorNumber(int competitorNumber) {
        this.competitorNumber = competitorNumber;
    }

    public String getCompetitorName() {
        return competitorName;
    }

    public void setCompetitorName(String competitorName) {
        this.competitorName = competitorName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCompetitorDob() {
        return competitorDob;
    }

    public void setCompetitorDob(String competitorDob) {
        this.competitorDob = competitorDob;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
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
        result.append(String.format("%-5d %-20s %-15s", competitorNumber, competitorName, level));
        result.append(" ");
        for (int score : scores) {
            result.append(score).append(" ");
        }
        result.append(String.format("   %.1f", calculateOverallScore()));
        return result.toString();
    }

    // full details
    public String getFullDetails() {
        StringBuilder result = new StringBuilder();
        int age = calculateAge(competitorDob);
        result.append(String.format("Competitor Number: %d, Name: %s, Country: %s, Age: %d, Category: %s, Level: %s, Scores: ",
                competitorNumber, competitorName, country, age, category, level));
        for (int score : scores) {
            result.append(score).append(" ");
        }
        result.append("\n");
        return result.toString();
    }

    // age calculation using DOB
    private int calculateAge(String dob) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate birthDate = LocalDate.parse(dob, formatter);
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears();
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + dob);
            return -1;
        }
    }

    // short details
    public String getShortDetails() {
        return String.format("CN %d (%s) scored overall score %.1f.", competitorNumber, getInitials(), calculateOverallScore());
    }

    private String getInitials() {
        StringBuilder initials = new StringBuilder();
        String[] nameParts = competitorName.split(" ");
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
