package crawler.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * 新闻类对象：
 * <p>
 * * 标题
 * * 作者
 * * 日期
 * * 内容/正文
 */
@Slf4j
@Getter
@Setter
public class NewsMessage extends Message {
    private String title;
    private String author;
    private String content;
    private String date; //yyyyMMddHHmmss

    public NewsMessage(String title, String content, String date, String author) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.author = author;
    }

    public NewsMessage() {
    }

    public static Logger getLog() {
        return log;
    }

    @Override
    public String toJsonString() {
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return toJsonString();
    }
}
