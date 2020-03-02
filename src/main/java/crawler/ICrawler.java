package crawler;

import crawler.entity.Message;

import java.util.List;

/**
 * 爬虫类接口定义
 */
public interface ICrawler<T extends Message> {
    /**
     * @param input 输入种子（URL、 或爬虫参数）
     * @return
     */
    public List<T> crawl(T input);
}
