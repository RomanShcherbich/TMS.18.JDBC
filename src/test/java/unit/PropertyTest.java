package unit;

import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.PropertiesUtils;

@Log4j2
@Listeners(TestMethodCapture.class)
public class PropertyTest {

    @Test(testName = "Test property")
    public void checkProperty() {
        log.info(TestMethodCapture.getTestMethod().getMethodName());
        PropertiesUtils properties = new PropertiesUtils();
        String propertyName = "datasource.url";
        String propertyExpectedValue = "jdbc:postgresql://localhost:5432/web-market-db";
        Assertions.assertThat(properties.get(propertyName))
                .describedAs("Validation of property '%s' value", propertyName)
                .isEqualTo(propertyExpectedValue);
    }

}