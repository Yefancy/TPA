package yefancy.tpa.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import main.yefancy.lfz.datacube.PrivacyCube.TabularDataPoint;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class TabularDataPointSerializer extends JsonSerializer<TabularDataPoint> {
    @Override
    public void serialize(TabularDataPoint tabularDataPoint, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(tabularDataPoint.i);
    }
}
