from rest_framework.views import exception_handler

def core_exceptions_handler(exc, contex):

    # Если исключение не обрабатывается явно в этом месте,
    # то оно передается стандартному обработчику DRF

    response = exception_handler(exc, contex)
    handlers = {
        'ValidationError': _handle_generic_error
    }

    exception_class = exc.__class__.__name__

    if exception_class in handlers:
        return handlers[exception_class](exc, contex, response)
    
    return response

def _handle_generic_error(exc, contex, response):

    response.data = {
        'errors': response.data
    }

    return response