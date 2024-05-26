package synfron.reshaper.burp.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CustomProbabilityRandomizer<T> {
    private final List<T> values;
    private final List<Integer> progressiveSums;
    private final int sum;
    private final Random random;

    public CustomProbabilityRandomizer(Random random, Map<T, Integer> probabilityMap) {
        this.random = random;
        values = new ArrayList<>(probabilityMap.size());
        progressiveSums = new ArrayList<>(probabilityMap.size());
        int sum = 0;
        for (Map.Entry<T, Integer> entry : probabilityMap.entrySet()) {
            int probability = entry.getValue();
            values.add(entry.getKey());
            sum += probability;
            progressiveSums.add(sum);
        }
        this.sum = sum;
    }

    public T next() {
        int randValue = random.nextInt(0, sum);
        for (int i = 0; i + 1 < progressiveSums.size(); i++) {
            if (randValue < progressiveSums.get(i)) {
                return values.get(i);
            }
        }
        return values.get(values.size() - 1);
    }

}
