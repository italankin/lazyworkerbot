package com.italankin.lazyworker.app.core

class HandlerManager {

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

    void process(Command command) {
        String name = command.getName()
        if (name == null || !handlers.containsKey(name)) {
            fallback.handle(command)
            return
        }
        try {
            Handler handler = handlers.get(name)
            boolean handled = handler.handle(command)
            if (!handled) {
                String help = handler.helpMessage()
                if (help) {
                    command.reply(help)
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
            fallback.handle(command)
        }
    }

}
