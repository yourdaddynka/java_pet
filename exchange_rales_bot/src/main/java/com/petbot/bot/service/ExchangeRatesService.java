package com.petbot.bot.service;

import com.petbot.bot.exception.ServiceException;

public interface ExchangeRatesService {

    String getUSDExchangeRate() throws ServiceException;

    String getEURExchangeRate() throws ServiceException;

    String getBELExchangeRate() throws ServiceException;

}
