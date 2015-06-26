# CascadeCrawler

一个简单的支持类js的链式规则，无限层级调用的java多线程处理工具，目前主要用于爬虫不定层次的爬取。

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

