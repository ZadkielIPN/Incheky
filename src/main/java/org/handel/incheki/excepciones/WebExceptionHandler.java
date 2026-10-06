package org.handel.incheki.excepciones;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class WebExceptionHandler {
    @ExceptionHandler(ResourcesNotFoundException.class)
    public ModelAndView handleNotFound(ResourcesNotFoundException ex,
                                       HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("mensaje", ex.getMessage());
        mav.addObject("ruta", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(EmailDuplicadoException.class)
    public ModelAndView handleEmailDuplicado(EmailDuplicadoException ex,
                                             HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/409");
        mav.setStatus(HttpStatus.CONFLICT);
        mav.addObject("mensaje", ex.getMessage());
        mav.addObject("ruta", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    public ModelAndView handleMethodNotAllowed(MethodNotAllowedException ex,
                                               HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/405");
        mav.setStatus(HttpStatus.METHOD_NOT_ALLOWED);
        mav.addObject("mensaje", ex.getMessage());
        mav.addObject("ruta", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneral(Exception ex,
                                      HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/500");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("mensaje", "Ha ocurrido un error inesperado");
        mav.addObject("ruta", request.getRequestURI());
        return mav;
    }
}
