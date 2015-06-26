package fetcher;

import java.util.List;

/**
 * @author sugan
 * @since 2015-05-25.
 */
public interface Fetcher {
    /**
     * @param input
     * @return
     */
    public List<String> fetch(String input);
}
