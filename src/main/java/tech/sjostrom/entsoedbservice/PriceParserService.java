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

    public void parseAndSave(String xml) {
        try {
            List<SpotPrice> prices = parseXml(xml);

            int saved = 0;
            for (SpotPrice price : prices) {
                try {
                    repository.save(price);
                    saved++;
                } catch (Exception e) {
                    // Duplicate - ignorera
                }
            }

            System.out.println("Saved " + saved + " new prices (ignored " + (prices.size() - saved) + " duplicates)");
        } catch (Exception e) {
            System.err.println("Error parsing XML: " + e.getMessage());
        }
    }

    private List<SpotPrice> parseXml(String xml) throws Exception {
        List<SpotPrice> prices = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));

        NodeList timeSeries = doc.getElementsByTagNameNS("*", "TimeSeries");

        for (int i = 0; i < timeSeries.getLength(); i++) {
            Element ts = (Element) timeSeries.item(i);

            String area = getElementValue(ts, "in_Domain.mRID");
            if (area.isEmpty()) {
                area = getElementValue(ts, "out_Domain.mRID");
            }

            NodeList periods = ts.getElementsByTagNameNS("*", "Period");

            for (int j = 0; j < periods.getLength(); j++) {
                Element period = (Element) periods.item(j);

                String startStr = getElementValue(period, "start");
                String resolution = getElementValue(period, "resolution");

                LocalDateTime start = parseDateTime(startStr);
                int intervalMinutes = parseResolution(resolution);

                NodeList points = period.getElementsByTagNameNS("*", "Point");

                for (int k = 0; k < points.getLength(); k++) {
                    Element point = (Element) points.item(k);

                    int position = Integer.parseInt(getElementValue(point, "position"));
                    BigDecimal price = new BigDecimal(getElementValue(point, "price.amount"));

                    LocalDateTime timestamp = start.plusMinutes((long) (position - 1) * intervalMinutes);

                    SpotPrice spotPrice = new SpotPrice();
                    spotPrice.setTimestamp(timestamp);
                    spotPrice.setPrice(price);
                    spotPrice.setCurrency("EUR");
                    spotPrice.setArea(area);

                    prices.add(spotPrice);
                }
            }
        }

        return prices;
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