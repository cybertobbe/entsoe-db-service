package tech.sjostrom.entsoedbservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PriceParserService {

    @Autowired
    private SpotPriceRepository repository;

    public void parseAndSave(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));

        List<SpotPrice> prices = new ArrayList<>();

        NodeList timeSeriesList = doc.getElementsByTagNameNS("*", "TimeSeries");

        for (int i = 0; i < timeSeriesList.getLength(); i++) {
            Element timeSeries = (Element) timeSeriesList.item(i);

            String area = getElementValue(timeSeries, "in_Domain.mRID");
            String currency = getElementValue(timeSeries, "currency_Unit.name");

            NodeList periods = timeSeries.getElementsByTagNameNS("*", "Period");

            for (int j = 0; j < periods.getLength(); j++) {
                Element period = (Element) periods.item(j);

                String startStr = getElementValue(period, "start");
                String resolution = getElementValue(period, "resolution");

                LocalDateTime start = parseDateTime(startStr);
                int minutesPerPoint = parseResolution(resolution);

                NodeList points = period.getElementsByTagNameNS("*", "Point");

                for (int k = 0; k < points.getLength(); k++) {
                    Element point = (Element) points.item(k);

                    int position = Integer.parseInt(getElementValue(point, "position"));
                    BigDecimal price = new BigDecimal(getElementValue(point, "price.amount"));

                    LocalDateTime timestamp = start.plusMinutes((long) (position - 1) * minutesPerPoint);

                    prices.add(new SpotPrice(timestamp, price, currency, area));
                }
            }
        }

        repository.saveAll(prices);
        System.out.println("Saved " + prices.size() + " price points");
    }

    private String getElementValue(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagNameNS("*", tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return "";
    }

    private LocalDateTime parseDateTime(String dateStr) {
        return ZonedDateTime.parse(dateStr).toLocalDateTime();
    }

    private int parseResolution(String resolution) {
        if ("PT15M".equals(resolution)) return 15;
        if ("PT30M".equals(resolution)) return 30;
        if ("PT60M".equals(resolution) || "PT1H".equals(resolution)) return 60;
        return 60;
    }
}