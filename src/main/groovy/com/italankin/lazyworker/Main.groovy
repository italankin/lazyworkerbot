package com.italankin.lazyworker

import com.italankin.lazyworker.app.App

class Main {

    static void main(String[] args) {
        if (!(args.length in [1, 2])) {
            System.err.println("Usage: lazyworkerbot <token> [hostname]")
            System.exit(1)
        }
        String token = args[0]
        String hostname
        if (args.length == 2) {
            hostname = args[1]
        } else {
            hostname = InetAddress.getLocalHost().hostAddress
        }
        startApp(token, hostname)
    }

    private static void startApp(String token, String hostname) {
        final App app = new App(token, hostname)
        app.start()
        Runtime.getRuntime().addShutdownHook(new Thread({
            app.stop()
        }))
    }

}
