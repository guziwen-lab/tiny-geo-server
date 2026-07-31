import com.supermap.util.ShapeEncodingDetector;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author gzw
 */
@SpringBootTest(classes = ShapeEncodingDetectorTest.class)
@Slf4j
public class ShapeEncodingDetectorTest {

    @Test
    public void test() {
        String path = "/Users/guziwen/Downloads/python检测分析任务/监测图斑/13河北省/130283迁安市";
        String detect = ShapeEncodingDetector.detect(path, null);
        System.out.println(detect);
    }

}
