# CascadeCrawler

一个简单的支持类js的链式规则，无限层级调用的java多线程处理工具，目前主要用于爬虫不定层次的爬取。

# 关键术语

### Message类 

* 定义的在整个爬虫爬取过程的数据存储结构， _初始阶段_ 的message，可以理解为类似传统爬虫术语里面的"种子"概念。 
* Message封装了hashmap结构， 不是线程安全的，而实际抓取过程，也是被1个线程持有，因此应该不存在线程安全问题。
* Message默认存储的参数规范： 
    * 可在Message类看到定义的常量， 例如  "\_URL\_" 等（可根据实际情况定义更多常用参数）


##Code Sample

```java


final FileWriter fw = new FileWriter(outputFileName);
FetcherExecutor executor = new FetcherExecutor(10);//并发数为10
//可以无限do~~
executor.setParallel(true)//开启并发（使用线程池并发操作）
		.doFetcher(set, new ItemUrlFetcher())
		.doFetcher(new ContentFetcher())
		.setParallel(false)//写文件，多线程写时候是非线程安全，关掉并发
		.doFetcher(new Fetcher() {
				@Override
				public List<String> fetch(String input) {
						try {
							fw.write(input + "\n");
						} catch (IOException e) {
							e.printStackTrace();
						}

					return null;
				}
			});
                
fw.close();
        
```

