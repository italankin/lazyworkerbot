package com.italankin.lazyworker.app.core

import com.italankin.lazyworker.app.handlers.StaticMessageHandler
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
        Handler handler = getHandler(name)
        if (!handler) {
            LOG.info("No command found for name '$name'")
            fallback.handle(request)
            return
        }
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

    Handler getHandler(String name) {
        if (name == null || name.isEmpty()) {
            // no handlers with empty names
            return null
        } else if (handlers.containsKey(name)) {
            // handler name specified and present
            return handlers.get(name)
        } else {
            // find handler by prefix
            List<Handler> candidates = handlers.findAll { key, value ->
                return key.startsWith(name) &&
                        // do not include privileged handlers
                        value.getClass().getPackage().getName() != "com.italankin.lazyworker.app.handlers.owner"
            }.collect { key, value -> value }
            int size = candidates.size()
            switch (size) {
                case 0:
                    // no handlers found
                    return null
                case 1:
                    // one handler
                    Handler handler = candidates.first()
                    LOG.info("Command found by prefix: ${handler.name()}")
                    return handler
                default:
                    // list possibilities
                    LOG.info("Candidates for prefix '$name' are: $candidates")
                    StringBuilder sb = new StringBuilder("Candidates for `/$name` are:")
                    candidates.each {
                        sb.append("\n")
                        sb.append(" - /")
                        sb.append(it.name())
                    }
                    return new StaticMessageHandler(sb.toString())
            }
        }
    }
}
