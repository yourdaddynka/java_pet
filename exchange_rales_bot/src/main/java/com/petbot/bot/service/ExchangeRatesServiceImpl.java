package com.petbot.bot.service;

import com.petbot.bot.client.CyberClient;
import com.petbot.bot.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.Optional;

@Service
public class ExchangeRatesServiceImpl implements ExchangeRatesService {
    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";
    private static final String BEL_XPATH = "/ValCurs//Valute[@ID='R01090B']/Value";

    @Autowired
    private CyberClient client;


    @Override
    public String getUSDExchangeRate() throws ServiceException {
        Optional<String> xmlOpt = client.getCurrencyRatesXML();
        String xml = xmlOpt.orElseThrow(() -> new ServiceException("Не удалось получить XML"));
        return extractCurrencyValueFromXML(xml, USD_XPATH);
    }

    @Override
    public String getEURExchangeRate() throws ServiceException {
        Optional<String> xmlOpt = client.getCurrencyRatesXML();
        String xml = xmlOpt.orElseThrow(() -> new ServiceException("Не удалось получить XML"));
        return extractCurrencyValueFromXML(xml, EUR_XPATH);
    }

    @Override
    public String getBELExchangeRate() throws ServiceException {
        Optional<String> xmlOpt = client.getCurrencyRatesXML();
        String xml = xmlOpt.orElseThrow(() -> new ServiceException("Не удалось получить XML"));
        return extractCurrencyValueFromXML(xml, BEL_XPATH);
    }

    private static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws ServiceException {
        InputSource source = new InputSource(new StringReader(xml));
        try {
            XPath xPatch = XPathFactory.newInstance().newXPath();
            Document document = (Document) xPatch.evaluate("/", source, XPathConstants.NODE);
            return xPatch.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e) {
            throw new ServiceException("Не удалось распарсить XML", e);
        }
    }
}
