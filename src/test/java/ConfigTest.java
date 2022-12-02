import me.deltaorion.common.config.FileConfig;
import me.deltaorion.common.config.InvalidConfigurationException;
import me.deltaorion.common.config.yaml.YamlAdapter;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ConfigTest {

    @Test
    public void dogTest() {
        FileConfig config = null;
        try {
            config = FileConfig.loadConfiguration(new YamlAdapter(), IOUtils.toInputStream("gladia: ['1234,1234', '5678,9101112']"));
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        System.out.println(config.getStringList("gladia"));
    }

}
