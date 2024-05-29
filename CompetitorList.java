import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompetitorList {

  //array list
  private final List<Competitor> competitors;

  public CompetitorList() {
    this.competitors = new ArrayList<>();
  }

  // check competitor with same Email and Category
  private boolean competitorExists(Competitor new_competitor) {
    return competitors.contains(new_competitor);
  }

  // new competitor register
  public boolean register_competitor(Competitor new_competitor) {
    if (competitorExists(new_competitor)) {
      return false; // Registration fail
    } else {
      competitors.add(new_competitor);
      return true; // Registered successfull
    }
  }

  // Remove Competitor
  public boolean remove(int competitor_number) {
    Competitor competitorToRemove = getCompetitorByNumber(competitor_number);
    if (competitorToRemove != null) {
      competitors.remove(competitorToRemove);
      return true; // Successfully remove
    } else {
      return false; // failed
    }
  }

  // Update competitor data using competitor number
  public boolean update_details(int competitor_number, Competitor updatedCompetitor) {
    Competitor existingCompetitor = getCompetitorByNumber(competitor_number);
    if (existingCompetitor != null) {
      // Update data
      existingCompetitor.setLevel(updatedCompetitor.getLevel());

      return true; // Success
    } else {
      return false; // failure
    }
  }

  // get all competitors
  public List<Competitor> getAllCompetitors() {
    return competitors;
  }

  // get competitor data using competitor number
  public Competitor getCompetitorByNumber(int competitor_number) {
    return competitors.stream()
        .filter(competitor -> competitor.getCompetitorNumber() == competitor_number)
        .findFirst()
        .orElse(null);
  }

  // Top scorer and average
  public Competitor getWinner() {
    return competitors.stream()
        .max(Comparator.comparingDouble(Competitor::calculateOverallScore))
        .orElse(null);
  }

  // Score score frequency
  public Map<Integer, Integer> getScoreFrequency() {
    Map<Integer, Integer> scoreFrequency = new HashMap<>();

    for (Competitor competitor : competitors) {
      for (int score : competitor.getScores()) {
        scoreFrequency.put(score, scoreFrequency.getOrDefault(score, 0) + 1);
      }
    }

    return scoreFrequency;
  }

  // Total Scores
  public int getOverallScore() {
    return competitors.stream()
        .flatMapToInt(competitor -> competitor.getScores().stream().mapToInt(Integer::intValue))
        .sum();
  }

  // Average
  public double getAverageScore() {
    int totalScore = getOverallScore();
    int totalCompetitors = competitors.size();

    return totalCompetitors > 0 ? (double) totalScore / totalCompetitors : 0.0;
  }

  // Highest
  public int getMaxScore() {
    return competitors.stream()
        .flatMapToInt(competitor -> competitor.getScores().stream().mapToInt(Integer::intValue))
        .max()
        .orElse(0);
  }

  // Lowest
  public int getMinScore() {
    return competitors.stream()
        .flatMapToInt(competitor -> competitor.getScores().stream().mapToInt(Integer::intValue))
        .min()
        .orElse(0);
  }
}
