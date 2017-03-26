package com.italankin.lazyworker.app.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HandlerManager {

    private static final Logger LOG = LoggerFactory.getLogger(HandlerManager.class)

    private final LinkedHashMap<String, Handler> handlers = new LinkedHashMap<>()
    private final Handler fallback

    HandlerManager(Handler fallback) {
        this.fallback = fallback
    }

    HandlerManager add(Handler handler) {
        String name = handler.name()
        if (handlers.containsKey(name) || fallback.name() == name) {
            throw new IllegalArgumentException("Duplicate handler: $name")
        }
        handlers.put(name, handler)
        return this
    }

    void process(Request request) {
        String name = request.getName()
        if (name == null || !handlers.containsKey(name)) {
            LOG.info("No command found for name '$name'")
            fallback.handle(request)
            return
        }
        Handler handler = handlers.get(name)
        try {
            boolean handled = handler.handle(request)
            if (!handled) {
                String help = handler.helpMessage()
                if (help) {
                    request.response(help)
                }
            }
        } catch (Exception e) {
            LOG.error("Handler '${handler.name()}' failed to handle request with exception:", e)
            fallback.handle(request)
        }
    }

    LinkedHashMap<String, Handler> getHandlers() {
        return handlers
    }

}
