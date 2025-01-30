package com.mwc.inventory.service.domain.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mwc.application.handler.ErrorDTO;
import com.mwc.inventory.service.domain.exception.*;

@Slf4j
@ControllerAdvice
public class InventoryGlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(value = {InventoryDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(InventoryDomainException inventoryDomainException) {
        log.error(inventoryDomainException.getMessage(), inventoryDomainException);
        return ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(inventoryDomainException.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(value = {InventoryNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleException(InventoryNotFoundException inventoryNotFoundException) {
        log.error(inventoryNotFoundException.getMessage(), inventoryNotFoundException);
        return ErrorDTO.builder()
                .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(inventoryNotFoundException.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(value = {InsufficientStock.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handleException(InsufficientStock insufficientStock) {
        log.error(insufficientStock.getMessage(), insufficientStock);
        return ErrorDTO.builder()
                .code(HttpStatus.CONFLICT.getReasonPhrase())
                .message(insufficientStock.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(value = {NotEnoughInventoryException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handleException(NotEnoughInventoryException notEnoughInventoryException) {
        log.error(notEnoughInventoryException.getMessage(), notEnoughInventoryException);
        return ErrorDTO.builder()
                .code(HttpStatus.CONFLICT.getReasonPhrase())
                .message(notEnoughInventoryException.getMessage())
                .build();
    }
}
