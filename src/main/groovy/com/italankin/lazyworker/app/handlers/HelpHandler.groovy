package com.italankin.lazyworker.app.handlers

import com.italankin.lazyworker.app.core.Handler
import com.italankin.lazyworker.app.core.HandlerManager
import com.italankin.lazyworker.app.core.Request

class HelpHandler implements Handler {

    private final Map<String, Handler> handlers

    HelpHandler(HandlerManager handlerManager) {
        this.handlers = handlerManager.getHandlers()
    }

    @Override
    String name() {
        return "help"
    }

    @Override
    boolean handle(Request request) throws Exception {
        String args = request.getRawArgs()
        if (!args || args.isEmpty()) {
            return request.response(helpMessage())
        }
        if (handlers.containsKey(args)) {
            String message = handlers.get(args).helpMessage()
            if (message) {
                return request.response(message)
            } else {
                return true
            }
        } else {
            return request.response(helpMessage())
        }
    }

    @Override
    String helpMessage() {
        return "Available commands:\n\n" +
                "/new - Start new activity\n" +
                "/current - Show current activity\n" +
                "/finish - Finish current activity\n" +
                "/today - Get stats for today\n" +
                "/week - Get stats for this week\n" +
                "/month - Get stats for this month\n" +
                "/date - Get stats for specific date\n" +
                "/show - Show acitivty details\n" +
                "/report - Create report\n" +
                "/last - Show last activities\n" +
                "/delete - Delete activity\n\n" +
                "To see help about command use '/help command'."
    }

}
