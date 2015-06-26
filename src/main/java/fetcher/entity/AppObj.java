package fetcher.entity;

/**
 * @author sugan
 * @since 2015-06-26.
 */
public class AppObj {
    private String appName;
    private String categoryName;
    private Double averageRating = 0.0;
    private Long appDownCount = 0L;

    public String toCsvString() {
        return categoryName +
                "," + appName +
                "," + averageRating +
                "," + appDownCount;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getAppDownCount() {
        return appDownCount;
    }

    public void setAppDownCount(Long appDownCount) {
        this.appDownCount = appDownCount;
    }
}
