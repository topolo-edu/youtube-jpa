package io.goorm.youtube.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleFileSizeException(MaxUploadSizeExceededException exc,RedirectAttributes redirectAttributes) {

        log.debug("MaxUploadSizeExceededException 처리" + exc.toString());

        redirectAttributes.addFlashAttribute("errormsg", "over file limit");

        return "redirect:/errorPage";
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException ex,RedirectAttributes redirectAttributes) {

        log.debug("MethodArgumentNotValidException 처리" + ex.toString());

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage())
        );

        redirectAttributes.addFlashAttribute("errormsg", errors);

        return "redirect:/errorPage";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes) throws Exception {

        log.debug("Exception 처리" + ex.toString());

        redirectAttributes.addFlashAttribute("errormsg", ex.toString());

        return "redirect:/errorPage";

    }

}
