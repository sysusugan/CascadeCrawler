package fetcher;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author sugan
 * @since 2015-05-26.
 */
public class FetcherRunner implements Callable {

    private final Fetcher fetcher;
    private final String input;
    private ConcurrentLinkedQueue<String> all;

    public FetcherRunner(Fetcher fetcher, String input, final ConcurrentLinkedQueue<String> all) {
        this.fetcher = fetcher;
        this.input = input;
        this.all = all;
    }

    public void run() {
        if (fetcher != null && input != null) {
            List<String> result = fetcher.fetch(input);
            if (result != null) {
                all.addAll(result);
            }
        }
    }

    @Override
    public Object call() throws Exception {
        run();
        return 1;
    }
}
