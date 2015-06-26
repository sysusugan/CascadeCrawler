# CascadeCrawler

一个简单的支持链式规则，无限层级调用的java多线程处理工具，目前主要用于爬虫测试。



	   FetcherExecutor executor = new FetcherExecutor(10);//并发数为10
        executor.setParallel(true)//使用线程池并发操作
        		.doFetcher(set, new ItemUrlFetcher())
                .doFetcher(new ContentFetcher())
                .setParallel(false)//写文件，多线程写非线程安全
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
  

