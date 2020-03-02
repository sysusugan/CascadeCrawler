package crawler.site.sj.qq;

import crawler.entity.Message;

/**
 * 网站名： sj.qq.com
 * 目标： 抓取腾讯应用宝， 应用市场的APP列表：  https://sj.qq.com/myapp/category.htm?orgame=1
 * 方法： Ajax翻页异步请求地址：https://sj.qq.com/myapp/cate/appList.htm?orgame=1&categoryId=0&pageSize=20&pageContext=40
 */

// 自定义App的实体类
public class AppNameObj extends Message {
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
